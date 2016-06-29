package com.paolosimone.wikuote.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.paolosimone.wikuote.fragment.quote.DynamicQuoteFragment;
import com.paolosimone.wikuote.fragment.quote.ExploreQuoteFragment;
import com.paolosimone.wikuote.fragment.ManageCategoriesFragment;
import com.paolosimone.wikuote.fragment.quote.QuoteOfTheDayFragment;
import com.paolosimone.wikuote.fragment.SearchFragment;
import com.paolosimone.wikuote.fragment.dialog.SelectCategoryDialogFragment;
import com.paolosimone.wikuote.fragment.WebViewFragment;
import com.paolosimone.wikuote.model.Category;
import com.paolosimone.wikuote.model.Page;

/**
 * Handle the navigation inside the application.
 * It contains accessory methods to navigate inside the main activity of the application.
 */
public class WiKuoteNavUtils {

    private static WiKuoteNavUtils instance = new WiKuoteNavUtils();
    private MainActivity activity;

    public static WiKuoteNavUtils getInstance() { return instance; }

    protected void replaceActivity(MainActivity activity) {
        this.activity = activity;
    }

    public void launchFavoritesActivity(){
        Intent i = new Intent(activity, FavoritesActivity.class);
        activity.startActivity(i);
    }

    public void launchManageCategoriesActivity(){
        Intent i = new Intent(activity, ManageCategoriesActivity.class);
        activity.startActivity(i);
    }

    public void launchSettingsActivity(){
        Intent i = new Intent(activity, SettingsActivity.class);
        activity.startActivity(i);
    }

    public void openQuoteOfTheDayFragment(){
        QuoteOfTheDayFragment qotdFragment = new QuoteOfTheDayFragment();
        activity.replaceContent(qotdFragment);
    }

    public void openExploreFragment(){
        ExploreQuoteFragment exploreFragment = new ExploreQuoteFragment();
        activity.replaceContent(exploreFragment);
    }

    public void openQuoteFragmentCategory(Category category) {
        DynamicQuoteFragment quoteFragment = DynamicQuoteFragment.newInstance(category);
        activity.replaceContent(quoteFragment);
    }

    public void openQuoteFragmentSinglePage(Page page) {
        DynamicQuoteFragment quoteFragment = DynamicQuoteFragment.newInstance(page);
        activity.replaceContent(quoteFragment);
    }

    public void openWebViewFragmentSinglePage(Page page) {
        WebViewFragment quoteFragment = WebViewFragment.newInstance(page.getUrl(), page.getName());
        activity.replaceContent(quoteFragment);
    }

    public void openSearchFragmentWithQuery(String query) {
        SearchFragment searchFragment = SearchFragment.newInstance(query);
        searchFragment.setOnPageClickedListener(activity);
        activity.replaceContent(searchFragment);
    }

    public void openSelectCategoryDialog(Page page) {
        FragmentManager fm = activity.getSupportFragmentManager();
        SelectCategoryDialogFragment dialog = SelectCategoryDialogFragment.newInstance(page);
        dialog.show(fm, SelectCategoryDialogFragment.TAG);
    }
}