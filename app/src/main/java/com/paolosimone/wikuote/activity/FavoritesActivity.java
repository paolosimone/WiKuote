package com.paolosimone.wikuote.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.adapter.FavoritesListAdapter;
import com.paolosimone.wikuote.model.Quote;
import com.paolosimone.wikuote.model.WiKuoteDatabaseHelper;

import java.util.List;

/**
 * Created by Paolo Simone on 09/04/2016.
 */
public class FavoritesActivity extends AppCompatActivity {

    FavoritesListAdapter favoritesListAdapter;

    //TODO DB listener

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        WiKuoteDatabaseHelper db = WiKuoteDatabaseHelper.getInstance();
        List<Quote> favorites = db.getAllQuotes();
        Log.d("FAVORITES"," "+ favorites.toString());
        favoritesListAdapter = new FavoritesListAdapter(this);
        favoritesListAdapter.addAll(favorites);
        Log.d("FAVORITES","COUNT: " + favoritesListAdapter.getCount());

        ListView listView = (ListView) findViewById(R.id.list_favorites);
        listView.setAdapter(favoritesListAdapter);
    }

}
