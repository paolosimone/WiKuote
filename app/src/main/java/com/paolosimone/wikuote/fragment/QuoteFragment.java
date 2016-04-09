package com.paolosimone.wikuote.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
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
import java.util.List;

/**
 * Created by Paolo Simone on 24/03/2016.
 */
public abstract class QuoteFragment extends Fragment implements Titled{

    protected final static String QUOTES = "quotes";
    protected final static String INDEX = "index";

    private ViewPager quotePager;
    private QuotePagerAdapter quotePagerAdapter;
    private Integer restoredIndex;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        quotePager = (ViewPager) view.findViewById(R.id.quote_pager);

        QuotePagerAdapter adapter = new QuotePagerAdapter(getActivity());
        adapter.setQuotes(retrieveQuotes(savedInstanceState));
        setPagerAdapter(adapter);

        if(savedInstanceState!=null){
            restoredIndex = savedInstanceState.getInt(INDEX);
        }
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
        saveQuotes(state);
    }

    @Override
    public abstract String getTitle(Context context);

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
