package com.forfun.paolosimone.wikuote.fragment;

import android.app.Activity;
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

import com.forfun.paolosimone.wikuote.R;
import com.forfun.paolosimone.wikuote.adapter.SearchAuthorAdapter;
import com.forfun.paolosimone.wikuote.api.QuoteProvider;
import com.forfun.paolosimone.wikuote.api.WikiQuoteProvider;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Paolo Simone on 24/03/2016.
 */
public class SearchFragment extends Fragment implements Titled{

    private static final String QUERY = "query";
    private static final String TITLE = "title";

    private QuoteProvider quoteProvider;
    private SearchAuthorAdapter searchAuthorAdapter;
    private String query;

    private String title;
    private boolean isFirstStart;
    private boolean isAttached = false;

    public static SearchFragment newInstance(String title, String query){
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(QUERY, query);
        SearchFragment sf = new SearchFragment();
        sf.setArguments(args);
        return sf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        quoteProvider = WikiQuoteProvider.getInstance();

        isFirstStart = savedInstanceState == null;
        if (isFirstStart){
            title = getArguments().getString(TITLE);
            query = getArguments().getString(QUERY);
        }
        else {
            title = savedInstanceState.getString(TITLE);
            query = savedInstanceState.getString(QUERY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        ListView listView = (ListView) view.findViewById(R.id.search_list);
        searchAuthorAdapter = new SearchAuthorAdapter(getActivity());
        listView.setAdapter(searchAuthorAdapter);

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
                ((SearchItemCallback) activity).onItemClicked(searchAuthorAdapter.getItem(position));
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        submitQuery();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString(QUERY,query);
        state.putString(TITLE,title);
    }

    @Override
    public String getTitle(Context context){
        if (title==null){
            title = getArguments().getString(TITLE);
        }
        return title;
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
                searchAuthorAdapter.replaceSuggestions(result);
                // TODO empty result
            }
            else {
                Toast.makeText(getActivity(),R.string.generic_error,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
