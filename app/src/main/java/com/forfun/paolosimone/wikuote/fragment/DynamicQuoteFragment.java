package com.forfun.paolosimone.wikuote.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.forfun.paolosimone.wikuote.R;
import com.forfun.paolosimone.wikuote.api.QuoteProvider;
import com.forfun.paolosimone.wikuote.api.WikiQuoteProvider;
import com.forfun.paolosimone.wikuote.exceptions.MissingAuthorException;
import com.forfun.paolosimone.wikuote.model.Quote;
import com.forfun.paolosimone.wikuote.adapter.DynamicQuotePagerAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * A placeholder fragment containing a simple view.
 */
public class DynamicQuoteFragment extends QuoteFragment {

    protected final static String AUTHORS = "authors";

    private final static int MAX_QUOTES = 20;
    private final static int PREFETCH_QUOTES = 5;

    private ArrayList<String> authors;
    private QuoteProvider quoteProvider;
    private HashSet<AsyncTask> currentTasks;

    private ViewPager quotePager;
    private DynamicQuotePagerAdapter quotePagerAdapter;
    private boolean isFirstStart;

    public DynamicQuoteFragment() {}

    public static DynamicQuoteFragment newInstanceWithAuthors(ArrayList<String> authors){
        Bundle args = new Bundle();
        args.putStringArrayList(AUTHORS, authors);
        DynamicQuoteFragment dqf = new DynamicQuoteFragment();
        dqf.setArguments(args);
        return dqf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //TODO add refresh action item
        authors = getArguments().getStringArrayList(AUTHORS);
        currentTasks = new HashSet<>();
        quoteProvider = WikiQuoteProvider.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        quotePager = (ViewPager) view.findViewById(R.id.quote_pager);

        isFirstStart = savedInstanceState == null;

        if(!isFirstStart){
            authors = savedInstanceState.getStringArrayList(AUTHORS);
            ArrayList<Quote> quotes = savedInstanceState.getParcelableArrayList(QUOTES);
            quotePagerAdapter.setQuotes(quotes);
            quotePager.setAdapter(quotePagerAdapter);
        }

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
        if (isFirstStart) refresh();
    }

    @Override
    public void onSaveInstanceState(Bundle state){
        super.onSaveInstanceState(state);
        state.putStringArrayList(AUTHORS, authors);
    }

    @Override
    public void onDestroy(){
        stopFetching();
        super.onDestroy();
    }

    public void refresh(){
        quotePagerAdapter = new DynamicQuotePagerAdapter(getActivity());
        quotePager.setAdapter(quotePagerAdapter);
        onQuoteChange(0);
    }

    public void changeAuthors(ArrayList<String> authors){
        if (authors==null || authors.isEmpty()){
            throw new IllegalArgumentException("Authors can't be null or empty");
        }
        this.authors = authors;

        boolean isAttached = getActivity()!= null;
        if (isAttached) refresh();
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

    @Override
    protected void saveQuotes(Bundle state){
        ArrayList<Quote> quotes = quotePagerAdapter.getQuotes();
        int index = quotePager.getCurrentItem();

        int start = Math.max(0, index - MAX_QUOTES/2);
        int end = Math.min(quotes.size(), index + MAX_QUOTES/2);

        state.putParcelableArrayList(QUOTES, new ArrayList<>(quotes.subList(start,end)));
        state.putInt(INDEX, index - start);
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
            else {
                Toast.makeText(getActivity(),R.string.generic_error,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
