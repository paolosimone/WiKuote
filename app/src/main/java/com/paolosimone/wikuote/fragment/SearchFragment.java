package com.paolosimone.wikuote.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.adapter.SearchPageListAdapter;
import com.paolosimone.wikuote.api.QuoteProvider;
import com.paolosimone.wikuote.api.WikiQuoteProvider;
import com.paolosimone.wikuote.model.Page;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Presents the results of a query, as a list of pages.
 * A query is a string representing a, possibly partial, name of a page.
 */
public class SearchFragment extends Fragment implements Titled{

    private static final String QUERY = "query";

    private QuoteProvider quoteProvider;
    private SearchPageListAdapter searchPageListAdapter;
    private ListView listView;

    private String query;
    private OnPageClickedListener listener;
    private boolean isAttached = false;

    /**
     * Build an instance of the fragment that will show the results of the given query.
     * @param query the query to be answered
     * @return the new instance of the fragment
     */
    public static SearchFragment newInstance(String query){
        Bundle args = new Bundle();
        args.putString(QUERY, query);
        SearchFragment sf = new SearchFragment();
        sf.setArguments(args);
        return sf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        quoteProvider = WikiQuoteProvider.getInstance();
        query = (savedInstanceState!=null) ? savedInstanceState.getString(QUERY) : getArguments().getString(QUERY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchPageListAdapter = new SearchPageListAdapter(getActivity());
        listView = (ListView) view.findViewById(R.id.list_search);
        listView.setAdapter(searchPageListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener!=null)
                    listener.onPageClicked(searchPageListAdapter.getItem(position));
            }
        });

        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        isAttached = true;
        submitQuery();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString(QUERY,query);
    }

    @Override
    public String getTitle(Context context){
        return context.getString(R.string.tab_search_page);
    }

    /**
     * Perform a new query.
     * @param query the new query to be answered
     */
    public void setQuery(String query){
        this.query = query;
        if (isAttached) submitQuery();
    }

    /**
     * Set the listener that will respond when the user select a query result.
     * @param listener the listener to be attach
     */
    public void setOnPageClickedListener(OnPageClickedListener listener) {
        this.listener = listener;
    }

    private void submitQuery(){
        if (query!=null) {
            new FetchSearchTask().execute(query);
        }
    }

    /**
     * Listener that handles the event when the user select a query result.
     */
    public interface OnPageClickedListener {
        /**
         * Handle the event when the user select a query result.
         * @param page the page that has been selected by the user
         */
        void onPageClicked(Page page);
    }

    /**
     * Asynchronous task that handles the retrieval of the results from a single query.
     */
    private class FetchSearchTask extends AsyncTask<String, Void, ArrayList<Page>> {
        @Override
        protected void onPreExecute(){

        }

        @Override
        protected ArrayList<Page> doInBackground(String... search) {
            try {
                return quoteProvider.getSuggestedPages(search[0]);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Page> result){
            if (result!=null) {
                searchPageListAdapter.replaceSuggestions(result);
                if (result.isEmpty()) {
                    Toast.makeText(getActivity(), R.string.err_missing_author, Toast.LENGTH_LONG).show();
                }
            }
            else {
                Toast.makeText(getActivity(),R.string.err_generic,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
