package com.forfun.paolosimone.wikuote.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forfun.paolosimone.wikuote.R;
import com.forfun.paolosimone.wikuote.model.Quote;
import com.forfun.paolosimone.wikuote.adapter.QuotePagerAdapter;
import com.forfun.paolosimone.wikuote.model.Subscription;

import java.util.ArrayList;

/**
 * Created by Paolo Simone on 24/03/2016.
 */
public class QuoteFragment extends Fragment implements Titled{

    protected final static String SUBSCRIPTION = "subscription";
    protected final static String QUOTES = "quotes";
    protected final static String INDEX = "index";

    protected ViewPager quotePager;
    private QuotePagerAdapter quotePagerAdapter;
    private Subscription subscription;
    private Integer restoredIndex;

    public static QuoteFragment newInstance(Subscription subscription, ArrayList<Quote> quotes){
        Bundle args = new Bundle();
        args.putParcelableArrayList(QUOTES, quotes);
        args.putParcelable(SUBSCRIPTION,subscription);
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

        subscription = getArguments().getParcelable(SUBSCRIPTION);
        if(savedInstanceState!=null){
            subscription = savedInstanceState.getParcelable(SUBSCRIPTION);
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
        state.putParcelable(SUBSCRIPTION,subscription);
        saveQuotes(state);
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    @Override
    public String getTitle(Context context){
        if (subscription==null) {
            subscription = getArguments().getParcelable(SUBSCRIPTION);
        }
        return subscription.getTitle();
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
