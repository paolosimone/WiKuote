package com.forfun.paolosimone.wikuote.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.forfun.paolosimone.wikuote.fragment.DynamicQuoteFragment;
import com.forfun.paolosimone.wikuote.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String QUOTE_FRAGMENT = "quote_fragment";

    private boolean isFirstStart;
    private DynamicQuoteFragment quoteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isFirstStart = savedInstanceState == null;

        quoteFragment = isFirstStart ?
                new DynamicQuoteFragment() :
                (DynamicQuoteFragment) getSupportFragmentManager().getFragment(savedInstanceState,QUOTE_FRAGMENT);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_fragment, quoteFragment)
                .commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Saved to favourites", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        if(isFirstStart) {
            Toast.makeText(getApplicationContext(),
                    R.string.swipe_tip,Toast.LENGTH_SHORT).show();

            ArrayList<String> authors = new ArrayList<>();
            authors.add("Albert Einstein");
            quoteFragment.changeAuthors(authors);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state){
        super.onSaveInstanceState(state);
        getSupportFragmentManager().putFragment(state,QUOTE_FRAGMENT,quoteFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            quoteFragment.refresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
