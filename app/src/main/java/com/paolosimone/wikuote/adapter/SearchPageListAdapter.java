package com.paolosimone.wikuote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.model.Page;

import java.util.ArrayList;

/**
 * Adapter that holds the results (suggestions) of a query and provides them to the ListView in the SearchFragment.
 */
public class SearchPageListAdapter extends ArrayAdapter<Page> {

    /**
     * Creates a new adapter with an empty list of results.
     * @param context the context in which the adapter is run
     */
    public SearchPageListAdapter(Context context){
        super(context,0, new ArrayList<Page>());
    }

    /**
     * Replace the current list of suggestions with the given one.
     * @param suggestions the list of suggestions to be shown
     */
    public void replaceSuggestions(ArrayList<Page> suggestions){
        clear();
        addAll(suggestions);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Page suggestion = getItem(position);

        if (convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_search_page, parent, false);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.search_result_name);
        tvName.setText(suggestion.getName());

        TextView tvDescription = (TextView) convertView.findViewById(R.id.search_result_description);
        tvDescription.setText(suggestion.getDescription());

        return convertView;
    }

}
