package com.paolosimone.wikuote.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.model.Category;
import com.paolosimone.wikuote.model.Page;
import com.paolosimone.wikuote.model.Quote;
import com.paolosimone.wikuote.adapter.QuotePagerAdapter;
import com.paolosimone.wikuote.model.WiKuoteDatabaseHelper;

import java.util.ArrayList;

/**
 * Created by Paolo Simone on 24/03/2016.
 */
public abstract class QuoteFragment extends Fragment implements Titled{

    protected final static String QUOTES = "quotes";
    protected final static String INDEX = "index";

    protected ViewPager quotePager;
    private QuotePagerAdapter quotePagerAdapter;
    private String title;
    private Integer restoredIndex;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        quotePager = (ViewPager) view.findViewById(R.id.quote_pager);
        quotePagerAdapter = new QuotePagerAdapter(getActivity());
        quotePagerAdapter.setQuotes(retrieveQuotes(savedInstanceState));

        if(savedInstanceState!=null){
            restoredIndex = savedInstanceState.getInt(INDEX);
        }

        quotePager.setAdapter(quotePagerAdapter);
        return  view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_dynamic_quote, menu);
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
        saveQuotes(state);
    }

    @Override
    public String getTitle(Context context){
        return title;
    }

    public Quote getCurrentQuote(){
        return quotePagerAdapter.getQuotes().get(quotePager.getCurrentItem());
    }

    protected ArrayList<Quote> retrieveQuotes(Bundle savedInstanceState){
        if(savedInstanceState!=null){
            return savedInstanceState.getParcelableArrayList(QUOTES);
        }

        return new ArrayList<>();
    }

    public void changeQuotes(ArrayList<Quote> quotes){
        quotePagerAdapter.setQuotes(quotes);
    }

    protected void saveQuotes(Bundle state){
        ArrayList<Quote> quotes = quotePagerAdapter.getQuotes();
        int index = quotePager.getCurrentItem();

        state.putParcelableArrayList(QUOTES, new ArrayList<>(quotes));
        state.putInt(INDEX, index);
    }
}
