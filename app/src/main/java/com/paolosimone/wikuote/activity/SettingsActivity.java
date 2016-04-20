package com.paolosimone.wikuote.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.fragment.SettingsFragment;

/**
 * Created by Paolo Simone on 20/04/2016.
 */
public class SettingsActivity extends AppCompatActivity {

    SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // TODO tablet layout
        settingsFragment = new SettingsFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_fragment, settingsFragment)
                .commit();
    }
}
