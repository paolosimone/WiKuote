package com.forfun.paolosimone.wikuote.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forfun.paolosimone.wikuote.R;
import com.forfun.paolosimone.wikuote.model.Quote;
import com.forfun.paolosimone.wikuote.view.QuotePagerAdapter;

import java.util.ArrayList;

/**
 * Created by Paolo Simone on 24/03/2016.
 */
public class QuoteFragment extends Fragment {

    protected final static String QUOTES_TAG = "quotes";
    protected final static String INDEX_TAG = "index";

    protected ViewPager quotePager;
    protected QuotePagerAdapter quotePagerAdapter;
    private Integer restoredIndex;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        quotePager = (ViewPager) view.findViewById(R.id.quote_pager);
        quotePagerAdapter = new QuotePagerAdapter(getActivity());

        if(savedInstanceState!=null){
            restoredIndex = savedInstanceState.getInt(INDEX_TAG);
            ArrayList<Quote> quotes = savedInstanceState.getParcelableArrayList(QUOTES_TAG);
            quotePagerAdapter.setQuotes(quotes);
        }

        quotePager.setAdapter(quotePagerAdapter);
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

    public void changeQuotes(ArrayList<Quote> quotes){
        quotePagerAdapter.setQuotes(quotes);
    }

    protected void saveQuotes(Bundle state){
        ArrayList<Quote> quotes = quotePagerAdapter.getQuotes();
        int index = quotePager.getCurrentItem();

        state.putParcelableArrayList(QUOTES_TAG, new ArrayList<>(quotes));
        state.putInt(INDEX_TAG, index);
    }

}
