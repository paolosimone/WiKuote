package com.paolosimone.wikuote.fragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.api.WikiQuoteProvider;
import com.paolosimone.wikuote.model.Category;
import com.paolosimone.wikuote.model.Page;
import com.paolosimone.wikuote.model.WiKuoteDatabaseHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Extends DynamicQuoteFragment by retrieving quotes from random pages, that are not been saved by the user.
 */
public class ExploreQuoteFragment extends DynamicQuoteFragment {

    private static final int MAX_PAGES = 5;

    private HashSet<AsyncTask> currentPageTasks;
    private List<Page> currentPages;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isUnsavedPage = true;
        currentPageTasks = new HashSet<>();
        currentPages = new ArrayList<>();

        currentPageTasks.add(new FetchRandomPageTask().execute());
    }

    @Override
    public void onDestroy(){
        for (AsyncTask t : currentPageTasks){
            t.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    public void setCategory(Category category){
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPage(Page page){
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTitle(Context context){
        return context.getString(R.string.tab_explore);
    }

    @Override
    protected Page selectNextPage(){
        if (currentPages.size()>=MAX_PAGES) {
            currentPageTasks.add(new FetchRandomPageTask().execute());
        }

        if (!currentPages.isEmpty()){
            Random rand = new Random();
            return currentPages.get(rand.nextInt(currentPages.size()));
        }

        return null;
    }

    @Override
    protected void handleParseException(Page requestedPage) {
        super.handleParseException(requestedPage);
        currentPages.remove(requestedPage);
    }

    /**
     * Asynchronous task that handles the retrieval of a random page, from which taking the quotes.
     */
    private class FetchRandomPageTask extends AsyncTask<Void, Void, Page> {

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
            currentPageTasks.remove(this);

            if (result!=null && !WiKuoteDatabaseHelper.getInstance().existsPage(result)){
                fetchQuoteForPage(result);

                if (currentPages.size()<MAX_PAGES){
                    currentPageTasks.add(new FetchRandomPageTask().execute());
                }
                else if (currentPages.size()==MAX_PAGES){
                    currentPages.remove(0);
                }
                currentPages.add(result);
            }
            else {
                currentPageTasks.add(new FetchRandomPageTask().execute());
            }
        }
    }
}
