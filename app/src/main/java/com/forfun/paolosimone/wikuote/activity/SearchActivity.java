package com.forfun.paolosimone.wikuote.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.forfun.paolosimone.wikuote.R;
import com.forfun.paolosimone.wikuote.fragment.SearchFragment;

/**
 * Created by Paolo Simone on 24/03/2016.
 */
public class SearchActivity extends AppCompatActivity {

    private SearchFragment searchFragment;
    private String currentSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchFragment = new SearchFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.search_fragment, searchFragment)
                .commit();

        currentSearch = getIntent().getStringExtra("search_author");
    }

    @Override
    public void onStart(){
        super.onStart();
        searchFragment.setQuery(currentSearch);
    }
}
