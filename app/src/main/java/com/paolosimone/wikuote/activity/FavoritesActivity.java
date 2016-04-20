package com.paolosimone.wikuote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.fragment.FavoritesListFragment;

/**
 * Created by Paolo Simone on 09/04/2016.
 */
public class FavoritesActivity extends AppCompatActivity {

    FavoritesListFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // TODO tablet layout
        listFragment = new FavoritesListFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_fragment, listFragment)
                .commit();
    }

    public void onFavoriteClick(int position){
        Intent intent = new Intent(this, FavoritesQuoteActivity.class);
        intent.putExtra(FavoritesQuoteActivity.INDEX, position);
        startActivity(intent);
    }

}
