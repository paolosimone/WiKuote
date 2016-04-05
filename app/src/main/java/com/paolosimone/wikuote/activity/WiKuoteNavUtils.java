package com.paolosimone.wikuote.activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.fragment.DynamicQuoteFragment;
import com.paolosimone.wikuote.fragment.SearchFragment;
import com.paolosimone.wikuote.model.Page;
import com.paolosimone.wikuote.model.Category;
import com.paolosimone.wikuote.model.WiKuoteDatabaseHelper;

/**
 * Created by Paolo Simone on 25/03/2016.
 */
public abstract class WiKuoteNavUtils {

    public static void openQuoteFragment(MainActivity activity, Category category) {
        DynamicQuoteFragment quoteFragment = DynamicQuoteFragment.newInstance(category);
        activity.replaceContent(quoteFragment);
    }

    public static void openQuoteFragmentSinglePage(MainActivity activity, Page page) {
        DynamicQuoteFragment quoteFragment = DynamicQuoteFragment.newInstance(page);
        activity.replaceContent(quoteFragment);
    }

    public static void openSearchFragmentWithQuery(MainActivity activity, int task, String query) {
        SearchFragment searchFragment = SearchFragment.newInstance(task, query);
        activity.replaceContent(searchFragment);
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

    public static void openSelectCategoryDialog(final MainActivity activity, final Page page){
        //TODO dialog fragment
        String positive = activity.getString(R.string.btn_ok);
        String negative = activity.getString(R.string.btn_cancel);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Select category for " + page.getName());
        builder.setMessage("=> Uncategorized");
        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Category def = Category.getDefault(activity);
                Log.d("UTILS", "Category before: " + page.getCategory());
                WiKuoteDatabaseHelper.getInstance().movePageToCategory(page, def);
                Log.d("UTILS", "Category after: " + page.getCategory());
                dialog.dismiss();
                openQuoteFragmentSinglePage(activity, page);
            }
        });
        builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }
}