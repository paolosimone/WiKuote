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
import android.widget.Toast;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.activity.MainActivity;
import com.paolosimone.wikuote.activity.WiKuoteNavUtils;
import com.paolosimone.wikuote.api.FetchQuoteResult;
import com.paolosimone.wikuote.api.QuoteProvider;
import com.paolosimone.wikuote.api.WikiQuoteProvider;
import com.paolosimone.wikuote.exceptions.ParserException;
import com.paolosimone.wikuote.model.Page;
import com.paolosimone.wikuote.model.Category;
import com.paolosimone.wikuote.model.Quote;
import com.paolosimone.wikuote.adapter.DynamicQuotePagerAdapter;
import com.paolosimone.wikuote.model.WiKuoteDatabaseHelper;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * A placeholder fragment containing a simple view.
 */
public class DynamicQuoteFragment extends QuoteFragment implements Titled {

    private final static String CATEGORY = "category";
    private final static String PAGE = "page";

    private final static int MAX_QUOTES = 20;
    private final static int PREFETCH_QUOTES = 5;

    private Category category;
    private Page page;
    protected boolean isUnsavedPage;

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
        // Category contains only saved pages!
        isUnsavedPage = page!=null && !WiKuoteDatabaseHelper.getInstance().existsPage(page);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        quotePager = (ViewPager) view.findViewById(R.id.quote_pager);

        quotePagerAdapter = new DynamicQuotePagerAdapter(getActivity());
        quotePagerAdapter.setQuotes(retrieveQuotes(savedInstanceState));
        setPagerAdapter(quotePagerAdapter);

        return  view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.menu_dynamic_quote, menu);
        if(isUnsavedPage){
            MenuItem saveDeleteItem = menu.findItem(R.id.action_save_delete_page);
            saveDeleteItem.setTitle(getString(R.string.action_save_page));
            saveDeleteItem.setIcon(R.drawable.ic_bookmark_border_white_24dp);
        }
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
            case R.id.action_save_delete_page:
                handleSaveDeletePage();
                return true;
            case R.id.action_refresh:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public Category getCategory() {
        return category;
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
        setPagerAdapter(quotePagerAdapter);
        onQuoteChange(0);
    }

    @Override
    public Quote getCurrentQuote(){
        List<Quote> quotes = quotePagerAdapter.getQuotes();
        if (quotes.isEmpty()) return null;

        int index = quotePager.getCurrentItem();
        boolean isValidIndex = index < quotes.size();

        return isValidIndex ? quotes.get(index) : null;
    }

    @Override
    protected void onQuoteChange(int index){
        super.onQuoteChange(index);
        int remaining = quotePagerAdapter.getQuotesNumber()+currentTasks.size()-index;
        int needed = 1+PREFETCH_QUOTES;
        if (remaining<needed){
            for (int i=0; i<needed-remaining; i++){
                fetchQuoteForPage(selectNextPage());
            }
        }
    }

    private void stopFetching(){
        for(AsyncTask t : currentTasks) {
            t.cancel(true);
        }
    }

    protected Page selectNextPage(){
        Page newQuotePage;
        if (category!=null) {
            List<Page> pages = category.getPages();
            if (pages == null || pages.isEmpty()) {
                return null;
            }

            Random rand = new Random();
            newQuotePage = pages.get(rand.nextInt(pages.size()));
        }
        else {
            newQuotePage = page;
        }
        return newQuotePage;
    }

    protected void fetchQuoteForPage(Page page){
        if(page == null){
            return;
        }
        currentTasks.add(new FetchQuoteTask().execute(page));
    }

    private void handleSaveDeletePage(){
        Quote currentQuote = getCurrentQuote();
        if (currentQuote==null){
            return;
        }

        Page currentPage = currentQuote.getPage();
        if (currentPage==null){
            return;
        }

        if (isUnsavedPage) {
            // Save page
            WiKuoteNavUtils.openSelectCategoryDialog((MainActivity) getActivity(),currentPage);
        }
        else {
            // Delete page
            WiKuoteDatabaseHelper db = WiKuoteDatabaseHelper.getInstance();
            db.deletePage(currentPage);
            Toast.makeText(getActivity(),R.string.msg_page_deleted,Toast.LENGTH_SHORT).show();

            if (category==null || !db.existsCategory(category)){
                WiKuoteNavUtils.openExploreFragment((MainActivity) getActivity());
            }
            else {
                refresh();
            }
        }
    }

    private void retrieveInput(Bundle savedInstanceState){
        if (savedInstanceState==null && getArguments()!=null){
            category = getArguments().getParcelable(CATEGORY);
            page = getArguments().getParcelable(PAGE);
        }
        else if (savedInstanceState!=null) {
            category = savedInstanceState.getParcelable(CATEGORY);
            page = savedInstanceState.getParcelable(PAGE);
        }
    }

    protected void handleParseException(final Page requestedPage) {
        View.OnClickListener showWebViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WiKuoteNavUtils.openWebViewFragmentSinglePage((MainActivity) getActivity(), requestedPage);
            }
        };
        quotePagerAdapter.silentNotifyParserError(getString(R.string.err_parser), showWebViewListener);
    }

    protected void handleIOException(final Page requestedPage) {
        fetchQuoteForPage(selectNextPage());
        //TODO better message mechanism or funny quotes db
        quotePagerAdapter.silentNotifyError(getString(R.string.err_generic));
    }

    private class FetchQuoteTask extends AsyncTask<Page, Void, FetchQuoteResult> {

        @Override
        protected FetchQuoteResult doInBackground(Page... params) {
            Page currentPage = params[0];
            try {
                Quote newQuote = quoteProvider.getRandomQuoteFor(currentPage);
                return FetchQuoteResult.success(newQuote);
            } catch (Exception e) {
                return FetchQuoteResult.error(currentPage, e);
            }
        }

        @Override
        protected void onPostExecute(FetchQuoteResult result){
            currentTasks.remove(this);

            if (result.isSuccessful()){
                quotePagerAdapter.addQuote(result.getQuote());
                if (quotePagerAdapter.userIsWaiting()){
                    onQuoteChange(quotePager.getCurrentItem());
                }
            }
            else {
                Exception e = result.getException();
                Page requestedPage = result.getPage();
                if (e instanceof ParserException) {
                    handleParseException(requestedPage);
                }
                else if (e instanceof IOException) {
                    handleIOException(requestedPage);
                }
            }
        }
    }
}
