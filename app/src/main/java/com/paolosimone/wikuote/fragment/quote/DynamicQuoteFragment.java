package com.paolosimone.wikuote.fragment.quote;

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
import com.paolosimone.wikuote.adapter.DynamicQuotePagerAdapter;
import com.paolosimone.wikuote.api.FetchQuoteResult;
import com.paolosimone.wikuote.api.QuoteProvider;
import com.paolosimone.wikuote.api.WikiQuoteProvider;
import com.paolosimone.wikuote.exceptions.ParserException;
import com.paolosimone.wikuote.fragment.Titled;
import com.paolosimone.wikuote.model.Category;
import com.paolosimone.wikuote.model.Page;
import com.paolosimone.wikuote.model.Quote;
import com.paolosimone.wikuote.model.WiKuoteDatabaseHelper;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Expands QuoteFragment functionality by allowing to present a dynamic list of quotes, retrieved from an external source.
 */
public class DynamicQuoteFragment extends QuoteFragment implements Titled {

    private final static String CATEGORY = "category";
    private final static String PAGE = "page";

    private final static int MAX_QUOTES = 20;
    private final static int PREFETCH_QUOTES = 5;

    private final static int DUPLICATE_INTERVAL = 3;
    private final static int MAX_ATTEMPTS = 25;

    private static boolean isFirstTime = true;

    private Category category;
    private Page page;
    protected boolean isUnsavedPage;

    private QuoteProvider quoteProvider;
    private HashSet<AsyncTask> currentTasks;
    private int attemptsNumber;

    private ViewPager quotePager;
    private DynamicQuotePagerAdapter quotePagerAdapter;

    public DynamicQuoteFragment() {}

    /**
     * Build a fragment that shows random quotes taken from the pages from the category.
     * @param category the category from which the fragment select the pages
     * @return the instance of the fragment
     */
    public static DynamicQuoteFragment newInstance(Category category){
        Bundle args = new Bundle();
        args.putParcelable(CATEGORY, category);
        DynamicQuoteFragment dqf = new DynamicQuoteFragment();
        dqf.setArguments(args);
        return dqf;
    }

    /**
     * Build a fragment that shows random quotes taken from the given page.
     * @param page the page from which take the quotes
     * @return the instance of the fragment
     */
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
        if (isFirstTime) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.hint_explore, Toast.LENGTH_SHORT).show();
            isFirstTime = false;
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
            case R.id.menu_item_web:
                openWebView();
                return  true;
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

    /**
     * Flush the current quotes and retrieve new ones from scratch.
     */
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
        int needed = 1 + PREFETCH_QUOTES;
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

    /**
     * Internal method that select a page from which retrieve the next quote.
     * @return the page from which retrieve the next quote
     */
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

    /**
     * Internal method to start the fetching of a quote from the given page.
     * @param page the page from which retrieving a quote
     */
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
            WiKuoteNavUtils.getInstance().openSelectCategoryDialog(currentPage);
        }
        else {
            // Delete page
            WiKuoteDatabaseHelper db = WiKuoteDatabaseHelper.getInstance();
            db.deletePage(currentPage);
            Toast.makeText(getActivity(),R.string.msg_page_deleted,Toast.LENGTH_SHORT).show();

            if (category==null || !db.existsCategory(category)){
                WiKuoteNavUtils.getInstance().openExploreFragment();
            }
            else {
                refresh();
            }
        }
    }

    private void openWebView(){
        Quote current = getCurrentQuote();
        if (current==null) {
            return;
        }
        WiKuoteNavUtils.getInstance().openWebViewFragmentSinglePage(current.getPage());
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

    /**
     * Internal method to handle a parse error during the fetching of a quote.
     * @param requestedPage the page from which the quote was requested
     */
    protected void handleParseException(final Page requestedPage) {
        View.OnClickListener showWebViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WiKuoteNavUtils.getInstance().openWebViewFragmentSinglePage(requestedPage);
            }
        };
        quotePagerAdapter.silentNotifyParserError(getString(R.string.err_parser), showWebViewListener);
    }

    /**
     * Internal method to handle an I/O error during the fetching of a quote.
     * @param requestedPage the page from which the quote was requested
     */
    protected void handleIOException(final Page requestedPage) {
        fetchQuoteForPage(selectNextPage());
        //TODO better message mechanism or funny quotes db
        quotePagerAdapter.silentNotifyError(getString(R.string.err_generic));
    }

    /**
     * Asynchronous task that handle the retrieval of a single quote from given page.
     */
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
                boolean isRepeated = quotePagerAdapter.hasDuplicates(result.getQuote(), DUPLICATE_INTERVAL);
                if (isRepeated) {
                    attemptsNumber++;

                    if (attemptsNumber > MAX_ATTEMPTS)
                        handleParseException(result.getPage());
                    else
                        fetchQuoteForPage(selectNextPage());
                }
                else {
                    attemptsNumber = 0;

                    quotePagerAdapter.addQuote(result.getQuote());
                    if (quotePagerAdapter.userIsWaiting()) {
                        onQuoteChange(quotePager.getCurrentItem());
                    }
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
