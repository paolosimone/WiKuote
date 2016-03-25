package com.forfun.paolosimone.wikuote.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Paolo Simone on 24/03/2016.
 */
public class SearchAuthorAdapter extends ArrayAdapter<String> {

    public SearchAuthorAdapter(Context context){
        super(context,0, new ArrayList<String>());
    }

    public void replaceSuggestions(ArrayList<String> suggestions){
        clear();
        addAll(suggestions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        String suggestion = getItem(position);

        //TODO improve search result views

        TextView textView;
        if (convertView!=null){
            textView = (TextView) convertView;
        }
        else {
            textView = new TextView(getContext());
        }

        textView.setText(suggestion);
        return textView;
    }

}
