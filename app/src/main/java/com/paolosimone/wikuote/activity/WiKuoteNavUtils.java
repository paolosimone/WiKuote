package com.paolosimone.wikuote.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;

import com.paolosimone.wikuote.fragment.DynamicQuoteFragment;
import com.paolosimone.wikuote.fragment.ExploreQuoteFragment;
import com.paolosimone.wikuote.fragment.ManageCategoriesFragment;
import com.paolosimone.wikuote.fragment.QuoteOfTheDayFragment;
import com.paolosimone.wikuote.fragment.SearchFragment;
import com.paolosimone.wikuote.fragment.SelectCategoryDialogFragment;
import com.paolosimone.wikuote.fragment.WebViewFragment;
import com.paolosimone.wikuote.model.Category;
import com.paolosimone.wikuote.model.Page;

/**
 * Handle the navigation inside the application.
 * It contains accessory methods to navigate inside the main activity of the application.
 */
public abstract class WiKuoteNavUtils {

    public static void launchFavoritesActivity(MainActivity activity){
        Intent i = new Intent(activity, FavoritesActivity.class);
        activity.startActivity(i);
    }

    public static void launchSettingsActivity(MainActivity activity){
        Intent i = new Intent(activity, SettingsActivity.class);
        activity.startActivity(i);
    }

    public static void openQuoteOfTheDayFragment(MainActivity activity){
        QuoteOfTheDayFragment qotdFragment = new QuoteOfTheDayFragment();
        activity.replaceContent(qotdFragment);
    }

    public static void openExploreFragment(MainActivity activity){
        ExploreQuoteFragment exploreFragment = new ExploreQuoteFragment();
        activity.replaceContent(exploreFragment);
    }

    public static void openQuoteFragmentCategory(MainActivity activity, Category category) {
        DynamicQuoteFragment quoteFragment = DynamicQuoteFragment.newInstance(category);
        activity.replaceContent(quoteFragment);
    }

    public static void openQuoteFragmentSinglePage(MainActivity activity, Page page) {
        DynamicQuoteFragment quoteFragment = DynamicQuoteFragment.newInstance(page);
        activity.replaceContent(quoteFragment);
    }

    public static void openWebViewFragmentSinglePage(MainActivity activity, Page page) {
        WebViewFragment quoteFragment = WebViewFragment.newInstance(page.getUrl(), page.getName());
        activity.replaceContent(quoteFragment);
    }

    public static void openSearchFragmentWithQuery(MainActivity activity, String query) {
        SearchFragment searchFragment = SearchFragment.newInstance(query);
        searchFragment.setOnPageClickedListener(activity);
        activity.replaceContent(searchFragment);
    }

    public static void openManageCategoriesFragment(MainActivity activity){
        ManageCategoriesFragment manageCategoriesFragment = new ManageCategoriesFragment();
        activity.replaceContent(manageCategoriesFragment);
    }

    public static void openSelectCategoryDialog(MainActivity activity, Page page) {
        FragmentManager fm = activity.getSupportFragmentManager();
        SelectCategoryDialogFragment dialog = SelectCategoryDialogFragment.newInstance(page);
        dialog.show(fm, SelectCategoryDialogFragment.TAG);
    }
}