package com.paolosimone.wikuote.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.model.Category;
import com.paolosimone.wikuote.model.Quote;
import com.paolosimone.wikuote.adapter.QuotePagerAdapter;

import java.util.ArrayList;

/**
 * Created by Paolo Simone on 24/03/2016.
 */
public class QuoteFragment extends Fragment implements Titled{

    protected final static String TITLE = "title";
    protected final static String QUOTES = "quotes";
    protected final static String INDEX = "index";

    protected ViewPager quotePager;
    private QuotePagerAdapter quotePagerAdapter;
    private String title;
    private Integer restoredIndex;

    public static QuoteFragment newInstance(String title, ArrayList<Quote> quotes){
        Bundle args = new Bundle();
        args.putString(TITLE,title);
        args.putParcelableArrayList(QUOTES, quotes);
        QuoteFragment qf = new QuoteFragment();
        qf.setArguments(args);
        return qf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        quotePager = (ViewPager) view.findViewById(R.id.quote_pager);
        quotePagerAdapter = new QuotePagerAdapter(getActivity());
        quotePagerAdapter.setQuotes(retrieveQuotes(savedInstanceState));

        title = getArguments().getString(TITLE);
        if(savedInstanceState!=null){
            restoredIndex = savedInstanceState.getInt(INDEX);
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

    @Override
    public String getTitle(Context context){
        return title;
    }

    protected ArrayList<Quote> retrieveQuotes(Bundle savedInstanceState){
        if(savedInstanceState!=null){
            return savedInstanceState.getParcelableArrayList(QUOTES);
        }

        ArrayList<Quote> quotes = getArguments().getParcelableArrayList(QUOTES);
        if (quotes!=null) return quotes;

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
