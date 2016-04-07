package com.paolosimone.wikuote.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.api.WikiQuoteProvider;
import com.paolosimone.wikuote.fragment.DynamicQuoteFragment;
import com.paolosimone.wikuote.fragment.SearchFragment;
import com.paolosimone.wikuote.fragment.SelectCategoryDialogFragment;
import com.paolosimone.wikuote.model.Page;
import com.paolosimone.wikuote.model.Category;

import java.io.IOException;

/**
 * Created by Paolo Simone on 25/03/2016.
 */
public abstract class WiKuoteNavUtils {

    public static void openQuoteFragmentCategory(MainActivity activity, Category category) {
        DynamicQuoteFragment quoteFragment = DynamicQuoteFragment.newInstance(category);
        activity.replaceContent(quoteFragment);
        cleanToolbar(activity);
    }

    public static void openExploreFragment(MainActivity activity){
        new FetchRandomQuoteTask(activity).execute();
    }

    public static void openQuoteFragmentSinglePage(MainActivity activity, Page page) {
        DynamicQuoteFragment quoteFragment = DynamicQuoteFragment.newInstance(page);
        activity.replaceContent(quoteFragment);
        cleanToolbar(activity);
    }

    public static void openSearchFragmentWithQuery(MainActivity activity, int task, String query) {
        SearchFragment searchFragment = SearchFragment.newInstance(task, query);
        activity.replaceContent(searchFragment);
        cleanToolbar(activity);
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

    private static void cleanToolbar(MainActivity activity){
        if (activity.refreshAction.isVisible()) {
            activity.refreshAction.setVisible(false);
            activity.invalidateOptionsMenu();
        }
    }

    private static class FetchRandomQuoteTask extends AsyncTask<Void, Void, Page> {

        private MainActivity activity;
        private ProgressDialog progressDialog;

        protected FetchRandomQuoteTask(MainActivity activity){
            super();
            this.activity = activity;
            progressDialog = new ProgressDialog(activity);
            progressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setMessage(activity.getString(R.string.msg_loading_quote));
            progressDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute(){
            progressDialog.show();
        }

        @Override
        protected Page doInBackground(Void... params) {
            Page randomPage = null;
            try {
                randomPage = WikiQuoteProvider.getInstance().getRandomPage();
            } catch (IOException e) {
                // do nothing
            }
            return randomPage;
        }

        @Override
        protected void onPostExecute(Page result){
            if (result!=null){
                openQuoteFragmentSinglePage(activity, result);

                if (!activity.refreshAction.isVisible()){
                    activity.refreshAction.setVisible(true);
                    activity.invalidateOptionsMenu();
                }
            }
            else {
                Toast.makeText(activity, R.string.err_generic,Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }
}