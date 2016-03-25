package com.forfun.paolosimone.wikuote.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.forfun.paolosimone.wikuote.fragment.DynamicQuoteFragment;
import com.forfun.paolosimone.wikuote.R;
import com.forfun.paolosimone.wikuote.fragment.SearchFragment;
import com.forfun.paolosimone.wikuote.fragment.Titled;
import com.forfun.paolosimone.wikuote.model.Subscription;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchFragment.SearchItemCallback{

    private static final String CONTENT = "content";

    private boolean isFirstStart;
    private Fragment contentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isFirstStart = savedInstanceState == null;

        if(!isFirstStart){
            contentFragment = getSupportFragmentManager().getFragment(savedInstanceState,CONTENT);
            replaceContent(contentFragment);
        }

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
            setupQuoteFragment("Albert Einstein");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state){
        super.onSaveInstanceState(state);
        getSupportFragmentManager().putFragment(state, CONTENT, contentFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        setupSearchView(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(String author) {
        // TODO distinguish between search and addSubscripion (using fragment Title)
        setupQuoteFragment(author);
    }

    private void replaceContent(Fragment contentFragment){
        this.contentFragment = contentFragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_fragment, contentFragment)
                .commit();

        String title = (contentFragment instanceof Titled) ?
                ((Titled) contentFragment).getTitle(this) :
                getString(R.string.app_name);
        setTitle(title);

    }

    private void setupSearchView(Menu menu){
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (contentFragment instanceof SearchFragment) {
                    ((SearchFragment) contentFragment).setQuery(query);
                } else {
                    String title = getString(R.string.search_author);
                    SearchFragment searchFragment = SearchFragment.newInstance(title,query);
                    replaceContent(searchFragment);
                }
                searchView.clearFocus();
                searchItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void setupQuoteFragment(String author){
        Subscription subscription = new Subscription(author,new ArrayList<String>());
        subscription.addAuthor(author);

        DynamicQuoteFragment quoteFragment = DynamicQuoteFragment.newInstance(subscription);
        replaceContent(quoteFragment);
    }
}
