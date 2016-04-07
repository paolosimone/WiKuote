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
 * Created by Paolo Simone on 24/03/2016.
 */
public class SearchAuthorAdapter extends ArrayAdapter<Page> {

    public SearchAuthorAdapter(Context context){
        super(context,0, new ArrayList<Page>());
    }

    public void replaceSuggestions(ArrayList<Page> suggestions){
        clear();
        addAll(suggestions);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Page suggestion = getItem(position);

        if (convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.search_page_item, parent, false);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.search_result_name);
        tvName.setText(suggestion.getName());

        TextView tvDescription = (TextView) convertView.findViewById(R.id.search_result_description);
        tvDescription.setText(suggestion.getDescription());

        return convertView;
    }

}
