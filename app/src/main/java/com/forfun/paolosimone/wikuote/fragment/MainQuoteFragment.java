package com.forfun.paolosimone.wikuote.fragment;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forfun.paolosimone.wikuote.R;
import com.forfun.paolosimone.wikuote.api.QuoteProvider;
import com.forfun.paolosimone.wikuote.api.WikiQuoteProvider;
import com.forfun.paolosimone.wikuote.exceptions.MissingAuthorException;
import com.forfun.paolosimone.wikuote.model.Quote;
import com.forfun.paolosimone.wikuote.view.QuotePagerAdapter;

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

    private final static int MAX_QUOTES = 20;
    private final static int PREFETCH_QUOTES = 5;

    private ArrayList<String> authors;
    private QuoteProvider quoteProvider;
    private HashSet<AsyncTask> currentTasks;

    private ViewPager quotePager;
    private QuotePagerAdapter quotePagerAdapter;
    private Integer restoredIndex;

    public MainQuoteFragment() {}

    public static MainQuoteFragment newInstance(ArrayList<String> authors){
        Bundle args = new Bundle();
        args.putStringArrayList(AUTHORS_TAG, authors);
        MainQuoteFragment mqf = new MainQuoteFragment();
        mqf.setArguments(args);
        return mqf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        authors = getArguments().getStringArrayList(AUTHORS_TAG);
        currentTasks = new HashSet<>();
        quoteProvider = WikiQuoteProvider.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        quotePager = (ViewPager) view.findViewById(R.id.quote_pager);
        quotePagerAdapter = new QuotePagerAdapter(getActivity());

        if(savedInstanceState!=null){
            authors = savedInstanceState.getStringArrayList(AUTHORS_TAG);
            restoredIndex = savedInstanceState.getInt(INDEX_TAG);
            ArrayList<Quote> quotes = savedInstanceState.getParcelableArrayList(QUOTES_TAG);
            quotePagerAdapter.setQuotes(quotes);
        }

        quotePager.setAdapter(quotePagerAdapter);
        quotePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                onQuoteChange(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        return  view;
    }

    @Override
    public void onStart(){
        super.onStart();
        if (restoredIndex!=null){
            quotePager.setCurrentItem(restoredIndex);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state){
        super.onSaveInstanceState(state);
        state.putStringArrayList(AUTHORS_TAG, authors);
        saveQuotes(state);
    }

    @Override
    public void onDestroy(){
        stopFetching();
        super.onDestroy();
    }

    public void refresh(){
        quotePagerAdapter = new QuotePagerAdapter(getActivity());
        quotePager.setAdapter(quotePagerAdapter);
        onQuoteChange(0);
    }

    public void changeAuthors(ArrayList<String> authors){
        if (authors==null || authors.isEmpty()){
            throw new IllegalArgumentException("Authors can't be null or empty");
        }
        this.authors = authors;
        refresh();
    }

    private void onQuoteChange(int index){
        int remaining = quotePagerAdapter.getQuotesNumber()+currentTasks.size()-index;
        int nedeed = 1+PREFETCH_QUOTES;
        if (remaining<nedeed){
            for (int i=0; i<nedeed-remaining; i++){
                newQuote();
            }
        }
    }

    private void stopFetching(){
        for(AsyncTask t : currentTasks) {
            t.cancel(true);
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

    private void saveQuotes(Bundle state){
        ArrayList<Quote> quotes = quotePagerAdapter.getQuotes();
        int index = quotePager.getCurrentItem();

        int start = Math.max(0, index - MAX_QUOTES/2);
        int end = Math.min(quotes.size(), index + MAX_QUOTES/2);

        state.putParcelableArrayList(QUOTES_TAG, new ArrayList<>(quotes.subList(start,end)));
        state.putInt(INDEX_TAG, index - start);
    }

    private class FetchQuoteTask extends AsyncTask<String, Void, Quote> {

        @Override
        protected Quote doInBackground(String... authors) {
            String newText = null;
            try {
                newText = quoteProvider.getRandomQuoteFor(authors[0]);
            } catch (MissingAuthorException e) {
                stopFetching();
            } catch (IOException e) {
                // do nothing
            }

            return (newText!=null) ? new Quote(newText,authors[0]) : null;
        }

        @Override
        protected void onPostExecute(Quote result){
            currentTasks.remove(this);

            if (result!=null){
                quotePagerAdapter.addQuote(result);
            }
        }
    }
}
