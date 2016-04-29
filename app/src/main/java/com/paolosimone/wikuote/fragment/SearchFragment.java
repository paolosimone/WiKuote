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
import com.paolosimone.wikuote.adapter.SearchPageAdapter;
import com.paolosimone.wikuote.api.QuoteProvider;
import com.paolosimone.wikuote.api.WikiQuoteProvider;
import com.paolosimone.wikuote.model.Page;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Paolo Simone on 24/03/2016.
 */
public class SearchFragment extends Fragment implements Titled{

    private static final String QUERY = "query";

    private QuoteProvider quoteProvider;
    private SearchPageAdapter searchPageAdapter;
    private ListView listView;

    private String query;
    private SearchPageListener listener;
    private boolean isAttached = false;


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

        searchPageAdapter = new SearchPageAdapter(getActivity());
        listView = (ListView) view.findViewById(R.id.list_search);
        listView.setAdapter(searchPageAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener!=null)
                    listener.onPageClicked(searchPageAdapter.getItem(position));
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

    public void setQuery(String query){
        this.query = query;
        if (isAttached) submitQuery();
    }

    public void setListener(SearchPageListener listener) {
        this.listener = listener;
    }

    private void submitQuery(){
        if (query!=null) {
            new FetchSearchTask().execute(query);
        }
    }

    public interface SearchPageListener {
        void onPageClicked(Page page);
    }

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
                searchPageAdapter.replaceSuggestions(result);
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
