package com.paolosimone.wikuote.fragment;

import android.os.Bundle;

import com.paolosimone.wikuote.model.Quote;
import com.paolosimone.wikuote.model.WiKuoteDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paolo Simone on 11/04/2016.
 */
public class FavoritesQuoteFragment extends QuoteFragment implements WiKuoteDatabaseHelper.DatabaseObserver {

    private WiKuoteDatabaseHelper db;

    public static FavoritesQuoteFragment newInstance(Integer index){
        Bundle args = new Bundle();
        args.putInt(INDEX, index);
        FavoritesQuoteFragment frag = new FavoritesQuoteFragment();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        db = WiKuoteDatabaseHelper.getInstance();
        db.attach(this);
    }

    @Override
    public void onDestroy(){
        db.detach(this);
        super.onDestroy();
    }

    @Override
    protected Integer retrieveIndex(Bundle savedInstanceState){
        Integer restored = super.retrieveIndex(savedInstanceState);
        Integer args = getArguments().getInt(INDEX);
        return restored!=null ? restored : args;
    }

    @Override
    protected ArrayList<Quote> retrieveQuotes(Bundle savedInstanceState){
        WiKuoteDatabaseHelper db = WiKuoteDatabaseHelper.getInstance();
        List<Quote> favorites = db.getAllQuotes();
        return new ArrayList<>(favorites);
    }


    @Override
    public void onDataChanged() {
        changeQuotes(retrieveQuotes(null));
    }
}
