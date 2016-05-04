package com.paolosimone.wikuote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.fragment.FavoritesListFragment;
import com.paolosimone.wikuote.fragment.FavoritesQuoteFragment;
import com.paolosimone.wikuote.fragment.QuoteFragment;

/**
 * Presents the favorites quotes to the user.
 * If the device has a small screen it contains only a fragment containing the list of favorite quotes.
 * If the device has a larger screen contains also a fragment with a detailed view of the selected quote.
 */
public class FavoritesActivity extends AppCompatActivity
        implements FavoritesListFragment.OnFavoriteClickListener, FavoritesQuoteFragment.OnFavoriteQuoteChangedListener {

    private static final String ACTIVATED_ITEM = "activated_item";

    private FavoritesListFragment listFragment;
    private FavoritesQuoteFragment quoteFragment;

    private boolean isTwoPane;
    private int activatedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listFragment = new FavoritesListFragment();
        listFragment.setOnFavoriteClickListener(this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.quote_list_fragment, listFragment)
                .commit();

        FrameLayout detailContainer = (FrameLayout) findViewById(R.id.quote_detail_fragment);
        isTwoPane = detailContainer != null;

        if (isTwoPane) {
            quoteFragment = FavoritesQuoteFragment.newInstance(0);
            quoteFragment.setOnFavouriteQuoteChangedListener(this);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.quote_detail_fragment, quoteFragment)
                    .commit();

            activatedItem = (savedInstanceState != null) ? savedInstanceState.getInt(ACTIVATED_ITEM) : 0;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isTwoPane) listFragment.activateItem(activatedItem);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        if (isTwoPane) state.putInt(ACTIVATED_ITEM, activatedItem);
        super.onSaveInstanceState(state);
    }

    @Override
    public void onFavoriteClick(int position){
        if (isTwoPane) {
            quoteFragment.goToQuote(position);
        }
        else {
            Intent intent = new Intent(this, FavoritesQuoteActivity.class);
            intent.putExtra(FavoritesQuoteActivity.INDEX, position);
            startActivity(intent);
        }
    }

    @Override
    public void onFavoriteQuoteChanged(int position) {
        listFragment.activateItem(position);
        activatedItem = position;
    }
}
