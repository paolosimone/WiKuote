package com.forfun.paolosimone.wikuote;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String QUOTE_FRAGMENT = "quote_fragment";

    private boolean isFirstStart;
    private MainQuoteFragment quoteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isFirstStart = savedInstanceState == null;

        quoteFragment = isFirstStart ?
                new MainQuoteFragment() :
                (MainQuoteFragment) getSupportFragmentManager().getFragment(savedInstanceState,QUOTE_FRAGMENT);

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

            quoteFragment.setAuthors(authors);
            quoteFragment.nextQuote();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state){
        super.onSaveInstanceState(state);
        getSupportFragmentManager().putFragment(state,QUOTE_FRAGMENT,quoteFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            quoteFragment.nextQuote();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
