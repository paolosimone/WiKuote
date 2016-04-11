package com.paolosimone.wikuote.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.fragment.FavoritesQuoteFragment;

/**
 * Created by Paolo Simone on 11/04/2016.
 */
public class FavoritesQuoteActivity extends AppCompatActivity {

    public static final String INDEX = "index";

    FavoritesQuoteFragment quoteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        int index = getIntent().getIntExtra(INDEX,0);
        quoteFragment = FavoritesQuoteFragment.newInstance(index);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.favorite_list_fragment, quoteFragment)
                .commit();
    }
}
