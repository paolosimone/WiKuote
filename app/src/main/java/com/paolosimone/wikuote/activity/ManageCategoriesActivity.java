package com.paolosimone.wikuote.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.fragment.ManageCategoriesFragment;

/**
 * Contains a fragment that allow to manage the saved pages and categories.
 */
public class ManageCategoriesActivity extends AppCompatActivity{

    ManageCategoriesFragment categoriesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        categoriesFragment = new ManageCategoriesFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_fragment, categoriesFragment)
                .commit();
    }

}
