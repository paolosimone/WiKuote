package com.paolosimone.wikuote.fragment;

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

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.adapter.SearchAuthorAdapter;
import com.paolosimone.wikuote.api.QuoteProvider;
import com.paolosimone.wikuote.api.WikiQuoteProvider;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Paolo Simone on 24/03/2016.
 */
public class SearchFragment extends Fragment implements Titled{

    public static final int SIMPLE_SEARCH_TASK = 0;
    public static final int ADD_PAGE_TASK = 1;

    private static final String QUERY = "query";
    private static final String TASK = "task";

    private QuoteProvider quoteProvider;
    private SearchAuthorAdapter searchAuthorAdapter;
    private String query;

    private Integer task;
    private boolean isFirstStart;
    private boolean isAttached = false;


    public static SearchFragment newInstance(int task, String query){
        Bundle args = new Bundle();
        args.putInt(TASK, task);
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
            task = getArguments().getInt(TASK);
            query = getArguments().getString(QUERY);
        }
        else {
            task = savedInstanceState.getInt(TASK);
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
                //TODO handle redirect and page id
                ((SearchPageListener) activity).onPageClicked(searchAuthorAdapter.getItem(position));
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
        state.putInt(TASK, task);
        state.putString(QUERY,query);
    }

    @Override
    public String getTitle(Context context){
        if (task == null){
            task = getArguments().getInt(TASK);
        }

        switch (task){
            case SIMPLE_SEARCH_TASK:
                return context.getString(R.string.tab_search_page);
            case ADD_PAGE_TASK:
                return context.getString(R.string.tab_add_page);
            default:
                return context.getString(R.string.tab_search_page);
        }
    }

    public Integer getTask() {
        return task;
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

    public interface SearchPageListener {
        //TODO return page
        void onPageClicked(String name);
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
                Toast.makeText(getActivity(),R.string.msg_generic_error,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
