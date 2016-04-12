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
import com.paolosimone.wikuote.model.Quote;
import com.paolosimone.wikuote.adapter.QuotePagerAdapter;
import com.paolosimone.wikuote.model.WiKuoteDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paolo Simone on 24/03/2016.
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

    public Quote getCurrentQuote(){
        List<Quote> quotes = quotePagerAdapter.getQuotes();
        if (quotes.isEmpty()) return null;
        else return quotes.get(quotePager.getCurrentItem());
    }

    protected ArrayList<Quote> retrieveQuotes(Bundle savedInstanceState){
        if(savedInstanceState!=null){
            return savedInstanceState.getParcelableArrayList(QUOTES);
        }

        return new ArrayList<>();
    }

    protected Integer retrieveIndex(Bundle savedInstanceState){
        if(savedInstanceState!=null){
            return savedInstanceState.getInt(INDEX);
        }
        return null;
    }

    public void changeQuotes(ArrayList<Quote> quotes){
        int index = quotePager.getCurrentItem();
        QuotePagerAdapter adapter = new QuotePagerAdapter(getActivity());
        adapter.setQuotes(quotes);
        setPagerAdapter(adapter);
        quotePager.setCurrentItem(index);
    }

    protected void saveQuotes(Bundle state){
        ArrayList<Quote> quotes = quotePagerAdapter.getQuotes();
        int index = quotePager.getCurrentItem();

        state.putParcelableArrayList(QUOTES, new ArrayList<>(quotes));
        state.putInt(INDEX, index);
    }

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
}
