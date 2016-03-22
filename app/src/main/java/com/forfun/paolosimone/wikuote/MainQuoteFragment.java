package com.forfun.paolosimone.wikuote;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.forfun.paolosimone.wikuote.api.QuoteProvider;
import com.forfun.paolosimone.wikuote.api.WikiQuoteProvider;
import com.forfun.paolosimone.wikuote.exceptions.MissingAuthorException;
import com.forfun.paolosimone.wikuote.model.FetchQuoteResult;
import com.forfun.paolosimone.wikuote.model.Quote;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainQuoteFragment extends Fragment {

    private final static String AUTHORS_TAG = "authors";
    private final static String QUOTES_TAG = "quotes";
    private final static String INDEX_TAG = "index";

    private final static int MAX_QUOTES = 10;
    private final static int PREFETCH_QUOTES = 3;

    private ArrayList<String> authors;
    private ArrayList<Quote> quotes;
    private int index;
    private QuoteProvider quoteProvider;
    private HashSet<AsyncTask> currentTasks;

    private TextView quoteTextView;
    private TextView authorTextView;
    private boolean isShowingMessage;

    public MainQuoteFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        authors = new ArrayList<>();
        quotes = new ArrayList<>();
        index = 0;

        currentTasks = new HashSet<>();
        quoteProvider = WikiQuoteProvider.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!=null){
            authors = savedInstanceState.getStringArrayList(AUTHORS_TAG);
            quotes = savedInstanceState.getParcelableArrayList(QUOTES_TAG);
            index = savedInstanceState.getInt(INDEX_TAG);
            updateQuote();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        quoteTextView = (TextView) view.findViewById(R.id.quote_text);
        authorTextView = (TextView) view.findViewById(R.id.author_text);
        updateQuote();

        view.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeLeft() {
                if (isShowingMessage) updateQuote(); else nextQuote();
            }

            @Override
            public void onSwipeRight() {
                if (isShowingMessage) updateQuote(); else previousQuote();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle state){
        super.onSaveInstanceState(state);
        state.putStringArrayList(AUTHORS_TAG, authors);
        state.putParcelableArrayList(QUOTES_TAG, quotes);
        state.putInt(INDEX_TAG, index);
    }

    @Override
    public void onDestroyView(){
        quoteTextView = null;
        authorTextView = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy(){
        for(AsyncTask t : currentTasks) {
            t.cancel(true);
        }
        super.onDestroy();
    }

    public void setAuthors(ArrayList<String> authors){
        if (authors==null || authors.isEmpty()){
            throw new IllegalArgumentException("Authors can't be null or empty");
        }
        this.authors = authors;
    }

    public void previousQuote(){
        if (index == 0) {
            Toast.makeText(getActivity(),
                    R.string.quotelist_end,Toast.LENGTH_SHORT).show();
        }
        else {
            index--;
            updateQuote();
        }
    }

    public void nextQuote(){
        boolean isInitialization = quotes.size() == 0;
        boolean userIsWaiting = index == quotes.size()-1;
        if (userIsWaiting || isInitialization) {
            displayMessage("Loading...");
            newQuote();
        }
        else {
            index++;
            updateQuote();
        }

        int margin = quotes.size()-1-index;
        if (margin < PREFETCH_QUOTES){
            for(int i=0; i<PREFETCH_QUOTES-margin; i++){
                newQuote();
            }
        }
    }

    private void newQuote(){
        if(authors==null || authors.isEmpty()){
            return;
        }
        Random rand = new Random();
        String author = authors.get(rand.nextInt(authors.size()));
        currentTasks.add(new FetchQuoteTask().execute(author));
    }

    private synchronized void onFetchQuote(Quote newQuote){
        boolean isInitialization = quotes.size() == 0;
        boolean userIsWaiting = index == quotes.size()-1;

        quotes.add(newQuote);

        if (quotes.size()>MAX_QUOTES && index>0){
            index--;
            quotes.remove(0);
        }

        if (userIsWaiting || isInitialization) {
            updateQuote();
        }
    }

    private void updateQuote(){
        isShowingMessage = false;

        if (quotes==null || quotes.isEmpty()){
            return;
        }
        if (quoteTextView!=null && authorTextView!=null){
            quoteTextView.setText(quotes.get(index).getText());
            authorTextView.setText(quotes.get(index).getAuthor());
        }
    }

    private void displayMessage(String message){
        isShowingMessage = true;

        if (quoteTextView!=null){
            quoteTextView.setText(message);
        }
        if (authorTextView!=null){
            authorTextView.setText("");
        }
    }

    private class FetchQuoteTask extends AsyncTask<String, Void, FetchQuoteResult> {

        @Override
        protected FetchQuoteResult doInBackground(String... authors) {
            String newText;
            try {
                newText = quoteProvider.getRandomQuoteFor(authors[0]);
            } catch (MissingAuthorException e) {
                return FetchQuoteResult.missingAuthor(getContext());
            } catch (IOException e) {
                return FetchQuoteResult.networkError(getContext());
            }

            if (newText!=null){
                Quote newQuote = new Quote(newText,authors[0]);
                return FetchQuoteResult.success(newQuote,getContext());
            }
            else {
                return FetchQuoteResult.error(getContext());
            }
        }

        @Override
        protected void onPostExecute(FetchQuoteResult result){
            currentTasks.remove(this);

            if (result.getStatus().equals(FetchQuoteResult.SUCCESS)){
                onFetchQuote(result.getQuote());
            }
            else {
                boolean userIsWaiting = index == quotes.size()-1;
                if (userIsWaiting) displayMessage(result.getMessage());
            }
        }
    }
}
