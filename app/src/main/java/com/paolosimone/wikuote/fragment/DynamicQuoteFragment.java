package com.paolosimone.wikuote.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.api.QuoteProvider;
import com.paolosimone.wikuote.api.WikiQuoteProvider;
import com.paolosimone.wikuote.exceptions.MissingAuthorException;
import com.paolosimone.wikuote.model.Page;
import com.paolosimone.wikuote.model.Category;
import com.paolosimone.wikuote.model.Quote;
import com.paolosimone.wikuote.adapter.DynamicQuotePagerAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * A placeholder fragment containing a simple view.
 */
public class DynamicQuoteFragment extends QuoteFragment {

    protected final static String CATEGORY = "category";
    protected final static String PAGE = "page";

    private final static int MAX_QUOTES = 20;
    private final static int PREFETCH_QUOTES = 5;

    private Category category;
    private Page page;

    private QuoteProvider quoteProvider;
    private HashSet<AsyncTask> currentTasks;

    private ViewPager quotePager;
    private DynamicQuotePagerAdapter quotePagerAdapter;

    public DynamicQuoteFragment() {}

    public static DynamicQuoteFragment newInstance(Category category){
        Bundle args = new Bundle();
        args.putParcelable(CATEGORY, category);
        DynamicQuoteFragment dqf = new DynamicQuoteFragment();
        dqf.setArguments(args);
        return dqf;
    }

    public static DynamicQuoteFragment newInstance(Page page){
        Bundle args = new Bundle();
        args.putParcelable(PAGE, page);
        DynamicQuoteFragment dqf = new DynamicQuoteFragment();
        dqf.setArguments(args);
        return dqf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        currentTasks = new HashSet<>();
        quoteProvider = WikiQuoteProvider.getInstance();
        retrieveInput(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        quotePager = (ViewPager) view.findViewById(R.id.quote_pager);
        quotePagerAdapter = new DynamicQuotePagerAdapter(getActivity());
        quotePagerAdapter.setQuotes(retrieveQuotes(savedInstanceState));

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_dynamic_quote, menu);
    }

    @Override
    public void onStart(){
        super.onStart();
        if (quotePagerAdapter.getQuotes().isEmpty()) {
            refresh();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state){
        super.onSaveInstanceState(state);
        state.putParcelable(CATEGORY, category);
        state.putParcelable(PAGE, page);
    }

    @Override
    public void onDestroy(){
        stopFetching();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_refresh:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setCategory(Category category) {
        this.category = category;
        this.page = null;
        if (getActivity()!= null) refresh();
    }

    public void setPage(Page page) {
        this.page = page;
        this.category = null;
        if (getActivity()!= null) refresh();
    }

    @Override
    public String getTitle(Context context){
        if (category==null && page ==null) retrieveInput(null);

        if (category!=null) return category.getTitle();
        if (page !=null) return page.getName();
        return context.getString(R.string.app_name);
    }

    public void refresh(){
        quotePagerAdapter = new DynamicQuotePagerAdapter(getActivity());
        quotePager.setAdapter(quotePagerAdapter);
        onQuoteChange(0);
    }

    private void onQuoteChange(int index){
        int remaining = quotePagerAdapter.getQuotesNumber()+currentTasks.size()-index;
        int needed = 1+PREFETCH_QUOTES;
        if (remaining<needed){
            for (int i=0; i<needed-remaining; i++){
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
        Page newQuotePage;
        if (category!=null) {
            List<Page> pages = category.getPages();
            if (pages == null || pages.isEmpty()) {
                return;
            }

            Random rand = new Random();
            newQuotePage = pages.get(rand.nextInt(pages.size()));
        }
        else {
            newQuotePage = page;
        }
        currentTasks.add(new FetchQuoteTask().execute(newQuotePage.getName()));
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

    private void retrieveInput(Bundle savedInstanceState){
        if (savedInstanceState==null){
            category = getArguments().getParcelable(CATEGORY);
            page = getArguments().getParcelable(PAGE);
        }
        else {
            category = savedInstanceState.getParcelable(CATEGORY);
            page = savedInstanceState.getParcelable(PAGE);
        }
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

            return (newText!=null) ? new Quote(newText,new Page(authors[0])) : null;
        }

        @Override
        protected void onPostExecute(Quote result){
            currentTasks.remove(this);

            if (result!=null){
                quotePagerAdapter.addQuote(result);
            }
            else {
                Quote error = new Quote(getActivity().getString(R.string.msg_generic_error),new Page(""));
                quotePagerAdapter.notifyErrorIfWaiting(error);
            }
        }
    }
}
