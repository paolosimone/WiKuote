package com.paolosimone.wikuote.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.activity.MainActivity;
import com.paolosimone.wikuote.activity.WiKuoteNavUtils;
import com.paolosimone.wikuote.adapter.QuotePagerAdapter;
import com.paolosimone.wikuote.model.Quote;
import com.paolosimone.wikuote.model.WiKuoteDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent a generic presenter for quotes, in which the quotes are viewed one by one.
 */
public abstract class QuoteFragment extends Fragment{

    protected final static String QUOTES = "quotes";
    protected final static String INDEX = "index";

    private ViewPager quotePager;
    private QuotePagerAdapter quotePagerAdapter;
    private FloatingActionButton fab;

    private Integer restoredIndex;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        restoredIndex = retrieveIndex(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        quotePager = (ViewPager) view.findViewById(R.id.quote_pager);

        QuotePagerAdapter adapter = new QuotePagerAdapter(getActivity());
        adapter.setQuotes(retrieveQuotes(savedInstanceState));
        setPagerAdapter(adapter);

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

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFavoriteButtonClick(view);
            }
        });

        return  view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_quote,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_item_share:
                sendShareIntent();
                return true;
            case R.id.menu_item_web:
                openWebView();
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        if (restoredIndex!=null){
            quotePager.setCurrentItem(restoredIndex);
        }
        updateFavoriteButton();
    }

    @Override
    public void onSaveInstanceState(Bundle state){
        super.onSaveInstanceState(state);
        saveQuotes(state);
    }

    protected void setPagerAdapter(QuotePagerAdapter adapter){
        quotePagerAdapter = adapter;
        quotePager.setAdapter(quotePagerAdapter);
    }

    /**
     * Switch the current view to the quote at the given index.
     * @param index the index of the quote
     */
    public void goToQuote(int index) {
        quotePager.setCurrentItem(index);
    }

    /**
     * Return the quote that is currently presented to the user.
     * @return the current quote if valid, null otherwise
     */
    public Quote getCurrentQuote(){
        List<Quote> quotes = quotePagerAdapter.getQuotes();
        if (quotes.isEmpty()) return null;
        else return quotes.get(quotePager.getCurrentItem());
    }

    /**
     * Internal method used to retrieve the list of quotes to be presented.
     * @param savedInstanceState the bundle that contains a state to be restored, possibly null
     * @return a list of quotes, possibly empty
     */
    protected ArrayList<Quote> retrieveQuotes(Bundle savedInstanceState){
        if(savedInstanceState!=null){
            return savedInstanceState.getParcelableArrayList(QUOTES);
        }

        return new ArrayList<>();
    }

    /**
     * Internal method used to retrieve the index of the quote to be shown.
     * @param savedInstanceState the bundle that contains a state to be restored, possibly null
     * @return the index of the quote to be shown, possibly null
     */
    protected Integer retrieveIndex(Bundle savedInstanceState){
        if(savedInstanceState!=null){
            return savedInstanceState.getInt(INDEX);
        }
        return null;
    }

    /**
     * Replace the current list of quotes with the given one.
     * @param quotes the list of quotes to be presented
     */
    public void changeQuotes(ArrayList<Quote> quotes){
        int index = quotePager.getCurrentItem();
        QuotePagerAdapter adapter = new QuotePagerAdapter(getActivity());
        adapter.setQuotes(quotes);
        setPagerAdapter(adapter);
        quotePager.setCurrentItem(index);
        onQuoteChange(index);
    }

    /**
     * Internal method used to save the current list of quotes.
     * @param state the bundle in which store the current of quotes
     */
    protected void saveQuotes(Bundle state){
        ArrayList<Quote> quotes = quotePagerAdapter.getQuotes();
        int index = quotePager.getCurrentItem();

        state.putParcelableArrayList(QUOTES, new ArrayList<>(quotes));
        state.putInt(INDEX, index);
    }

    /**
     * Internal method to perform actions when the user select a new quote.
     * @param position the index of the new selected quote
     */
    protected void onQuoteChange(int position){
        updateFavoriteButton();
    }

    private void onFavoriteButtonClick(View view){
        Quote current = getCurrentQuote();
        if (current==null) return;

        boolean alreadySaved = WiKuoteDatabaseHelper.getInstance().existsQuote(current);
        if (!alreadySaved){
            WiKuoteDatabaseHelper.getInstance().saveFavorite(current);
            Snackbar.make(view, R.string.action_saved_favorite, Snackbar.LENGTH_SHORT).show();
        }
        else {
            WiKuoteDatabaseHelper.getInstance().deleteFavorite(current);
            Snackbar.make(view, R.string.action_deleted_favorite, Snackbar.LENGTH_SHORT).show();
        }

        updateFavoriteButton();
    }

    private void updateFavoriteButton(){
        Quote current = getCurrentQuote();
        if (current==null) {
            fab.hide();
            return;
        }

        boolean alreadySaved = WiKuoteDatabaseHelper.getInstance().existsQuote(current);
        int imageId = alreadySaved
                ? R.drawable.ic_favorite_white_48dp
                : R.drawable.ic_favorite_border_white_48dp;
        fab.setImageResource(imageId);

        if (!fab.isShown()) {
            fab.show();
        }
    }

    private void sendShareIntent(){
        Quote current = getCurrentQuote();
        if (current==null) {
            return;
        }

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.app_name));
        shareIntent.putExtra(Intent.EXTRA_TEXT,current.getText() + " - " + current.getPage().getName());
        startActivity(Intent.createChooser(shareIntent,getString(R.string.title_share_quote)));
    }

    private void openWebView(){
        Quote current = getCurrentQuote();
        if (current==null) {
            return;
        }
        MainActivity activity = (MainActivity) getActivity();
        WiKuoteNavUtils.openWebViewFragmentSinglePage(activity, current.getPage());
    }
}
