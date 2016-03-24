package com.forfun.paolosimone.wikuote.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.forfun.paolosimone.wikuote.R;
import com.forfun.paolosimone.wikuote.adapter.SuggestionsAdapter;
import com.forfun.paolosimone.wikuote.api.QuoteProvider;
import com.forfun.paolosimone.wikuote.api.WikiQuoteProvider;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Paolo Simone on 24/03/2016.
 */
public class SearchFragment extends Fragment{

    private static final String QUERY = "query";

    private QuoteProvider quoteProvider;
    private SuggestionsAdapter suggestionsAdapter;
    private String query;

    private boolean isFirstStart;
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
        query = getArguments().getString(QUERY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        isFirstStart = savedInstanceState == null;

        ListView listView = (ListView) view.findViewById(R.id.search_list);
        suggestionsAdapter = new SuggestionsAdapter(getActivity());
        listView.setAdapter(suggestionsAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        isAttached = true;

        final Activity activity = getActivity();
        ListView listView = (ListView) activity.findViewById(R.id.search_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((SearchItemCallback) activity).onItemClicked(suggestionsAdapter.getItem(position));
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        if (isFirstStart) submitQuery();
    }

    public void setQuery(String query){
        this.query = query;
        if (isAttached) submitQuery();
    }

    private void submitQuery(){
        if (query!=null) {
            new FetchSearchTask().execute(query);
        }
    }

    public interface SearchItemCallback {
        void onItemClicked(String author);
    }

    private class FetchSearchTask extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected void onPreExecute(){

        }

        @Override
        protected ArrayList<String> doInBackground(String... search) {
            try {
                return quoteProvider.getSuggestedAuthors(search[0]);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> result){
            if (result!=null){
                suggestionsAdapter.replaceSuggestions(result);
            }
            else {
                Toast.makeText(getActivity(),R.string.generic_error,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
