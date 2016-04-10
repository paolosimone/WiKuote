package com.paolosimone.wikuote.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.adapter.CategoriesDrawerAdapter;
import com.paolosimone.wikuote.fragment.SearchFragment;
import com.paolosimone.wikuote.fragment.Titled;
import com.paolosimone.wikuote.model.Category;
import com.paolosimone.wikuote.model.Page;
import com.paolosimone.wikuote.model.WiKuoteDatabaseHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SearchFragment.SearchPageListener, WiKuoteDatabaseHelper.DatabaseObserver {

    private static final String CONTENT = "content";

    private boolean isFirstStart;
    private Fragment contentFragment;

    private DrawerLayout drawer;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupDrawer(toolbar);

        isFirstStart = savedInstanceState == null;
        if(!isFirstStart){
            contentFragment = getSupportFragmentManager().getFragment(savedInstanceState,CONTENT);
            replaceContent(contentFragment);
        }

        WiKuoteDatabaseHelper.getInstance().attach(this);
    }

    @Override
    public void onStart(){
        super.onStart();
        if(isFirstStart) {
            WiKuoteNavUtils.openExploreFragment(this);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onSaveInstanceState(Bundle state){
        super.onSaveInstanceState(state);
        getSupportFragmentManager().putFragment(state, CONTENT, contentFragment);
    }

    @Override
    public void onDestroy(){
        WiKuoteDatabaseHelper.getInstance().detach(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        setupSearchView(menu);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        switch (item.getItemId()){
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPageClicked(Page page) {
        Integer task = ((SearchFragment) contentFragment).getTask();
        switch (task){
            case SearchFragment.SIMPLE_SEARCH_TASK:
                WiKuoteNavUtils.openQuoteFragmentSinglePage(this, page);
                break;
            case SearchFragment.ADD_PAGE_TASK:
                if (WiKuoteDatabaseHelper.getInstance().existsPage(page)) {
                    Toast.makeText(this, R.string.err_existing_page, Toast.LENGTH_SHORT).show();
                    WiKuoteNavUtils.openQuoteFragmentSinglePage(this, page);
                    return;
                }
                WiKuoteNavUtils.openSelectCategoryDialog(this, page);
                break;
            default:
        }
    }

    @Override
    public void onDataChanged(){
        updateCategoryList();
    }

    public Fragment getContentFragment() {
        return contentFragment;
    }

    protected void replaceContent(Fragment contentFragment){
        this.contentFragment = contentFragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_fragment, contentFragment)
                .commit();

        String title = (contentFragment instanceof Titled) ?
                ((Titled) contentFragment).getTitle(this) :
                getString(R.string.app_name);
        setTitle(title);
        drawer.closeDrawers();
    }

    private void setupDrawer(Toolbar toolbar){
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        updateCategoryList();

        findViewById(R.id.nav_explore_fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WiKuoteNavUtils.openExploreFragment(MainActivity.this);
            }
        });
        findViewById(R.id.nav_add_source_fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WiKuoteNavUtils.openAddPageDialog(MainActivity.this);
            }
        });
        findViewById(R.id.nav_favorites_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WiKuoteNavUtils.launchFavoritesActivity(MainActivity.this);
            }
        });

        drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(drawerToggle);
    }

    private void updateCategoryList(){
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.list_drawer_category);

        List<Category> categories = WiKuoteDatabaseHelper.getInstance().getAllCategories();
        if (categories.isEmpty()){
            TextView msgEmpty = (TextView) findViewById(R.id.nav_empty_categories);
            msgEmpty.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
            return;
        }

        if (listView.getVisibility()==View.INVISIBLE){
            TextView msgEmpty = (TextView) findViewById(R.id.nav_empty_categories);
            msgEmpty.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
        }

        Map<Category, List<Page>> pagesByCategory = new HashMap<>();
        for(Category c : categories){
            pagesByCategory.put(c,c.getPages());
        }

        CategoriesDrawerAdapter adapter = new CategoriesDrawerAdapter(this, categories, pagesByCategory);
        listView.setAdapter(adapter);
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
                    WiKuoteNavUtils.openSearchFragmentWithQuery(MainActivity.this, SearchFragment.SIMPLE_SEARCH_TASK, query);
                }
                searchView.clearFocus();
                searchItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (contentFragment instanceof SearchFragment){
                    ((SearchFragment) contentFragment).setQuery(newText);
                    return true;
                }
                return false;
            }
        });
    }
}
