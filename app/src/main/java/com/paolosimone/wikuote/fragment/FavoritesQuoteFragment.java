package com.paolosimone.wikuote.fragment;

import android.os.Bundle;

import com.paolosimone.wikuote.model.Quote;
import com.paolosimone.wikuote.model.WiKuoteDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Presents the favorites quotes, one quote at the time.
 */
public class FavoritesQuoteFragment extends QuoteFragment implements WiKuoteDatabaseHelper.DatabaseObserver {

    private WiKuoteDatabaseHelper db;
    private OnFavoriteQuoteChangedListener listener;

    /**
     * Build a new fragment, which will open showing the favorite quote at the given index.
     * @param index the index of the quote to be shown at start-up
     * @return the new instance of the fragment
     */
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

    @Override
    protected void onQuoteChange(int position) {
        super.onQuoteChange(position);
        if (listener !=null ) listener.onFavoriteQuoteChanged(position);
    }

    /**
     * Set the listener that will respond to the event when the quote navigate to a new quote.
     * @param listener the listener
     */
    public void setOnFavouriteQuoteChangedListener(OnFavoriteQuoteChangedListener listener) {
        this.listener = listener;
    }

    /**
     * Listener that will respond to the event when the user selects a new favorite quote.
     */
    public interface OnFavoriteQuoteChangedListener {
        /**
         * Handle the event when the user navigates to a new favorite quote.
         * @param position the index of the new quote
         */
        void onFavoriteQuoteChanged(int position);
    }
}
