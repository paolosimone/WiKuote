package com.paolosimone.wikuote.activity;

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
        String negative = activity.getString(R.string.btn_cancel);
        final EditText input = new EditText(activity);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(dialogTitle);
        builder.setView(input);
        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String query = input.getText().toString();
                dialog.dismiss();
                openSearchFragmentWithQuery(activity, SearchFragment.ADD_PAGE_TASK, query);
            }
        });
        builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    public static void openSelectCategoryDialog(MainActivity activity, Page page) {
        FragmentManager fm = activity.getSupportFragmentManager();
        SelectCategoryDialogFragment fragment = SelectCategoryDialogFragment.newInstance(page);
        fragment.show(fm, SelectCategoryDialogFragment.TAG);
    }
}