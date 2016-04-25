package com.paolosimone.wikuote.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.WindowManager;
import android.widget.EditText;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.fragment.DynamicQuoteFragment;
import com.paolosimone.wikuote.fragment.ExploreQuoteFragment;
import com.paolosimone.wikuote.fragment.ManageCategoriesFragment;
import com.paolosimone.wikuote.fragment.QuoteOfTheDayFragment;
import com.paolosimone.wikuote.fragment.SearchFragment;
import com.paolosimone.wikuote.fragment.SelectCategoryDialogFragment;
import com.paolosimone.wikuote.fragment.SimpleTextInputDialogFragment;
import com.paolosimone.wikuote.model.Page;
import com.paolosimone.wikuote.model.Category;

/**
 * Created by Paolo Simone on 25/03/2016.
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

    public static void openQuoteFragmentCategory(MainActivity activity, Category category) {
        DynamicQuoteFragment quoteFragment = DynamicQuoteFragment.newInstance(category);
        activity.replaceContent(quoteFragment);
    }

    public static void openExploreFragment(MainActivity activity){
        ExploreQuoteFragment exploreFragment = new ExploreQuoteFragment();
        activity.replaceContent(exploreFragment);
    }

    public static void openQuoteFragmentSinglePage(MainActivity activity, Page page) {
        DynamicQuoteFragment quoteFragment = DynamicQuoteFragment.newInstance(page);
        activity.replaceContent(quoteFragment);
    }

    public static void openSearchFragmentWithQuery(MainActivity activity, int task, String query) {
        SearchFragment searchFragment = SearchFragment.newInstance(task, query);
        activity.replaceContent(searchFragment);
    }

    public static void openManageCategoriesFragment(MainActivity activity){
        ManageCategoriesFragment manageCategoriesFragment = new ManageCategoriesFragment();
        activity.replaceContent(manageCategoriesFragment);
    }

    public static void openAddPageDialog(final MainActivity activity) {
        String dialogTitle = activity.getString(R.string.msg_search_request);
        String positive = activity.getString(R.string.btn_search);

        FragmentManager fm = activity.getSupportFragmentManager();
        SimpleTextInputDialogFragment dialog = SimpleTextInputDialogFragment.newInstance(dialogTitle,positive);
        dialog.setOnInputSubmitListener(new SimpleTextInputDialogFragment.OnInputSubmitListener() {
            @Override
            public void onInputSubmit(String query) {
                openSearchFragmentWithQuery(activity, SearchFragment.ADD_PAGE_TASK, query);
            }
        });
        dialog.show(fm, "fragment_add_page");
    }

    public static void openSelectCategoryDialog(MainActivity activity, Page page) {
        FragmentManager fm = activity.getSupportFragmentManager();
        SelectCategoryDialogFragment dialog = SelectCategoryDialogFragment.newInstance(page);
        dialog.show(fm, SelectCategoryDialogFragment.TAG);
    }
}