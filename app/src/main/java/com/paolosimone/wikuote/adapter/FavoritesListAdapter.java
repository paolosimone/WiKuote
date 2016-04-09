package com.paolosimone.wikuote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.model.Quote;

import java.util.ArrayList;

/**
 * Created by Paolo Simone on 09/04/2016.
 */
public class FavoritesListAdapter extends ArrayAdapter<Quote> {

    public FavoritesListAdapter(Context context){
        super(context,0, new ArrayList<Quote>());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Quote quote = getItem(position);

        if (convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_favorite_quote, parent, false);
        }

        TextView tvText = (TextView) convertView.findViewById(R.id.quote_text);
        tvText.setText(quote.getText());

        TextView tvPage = (TextView) convertView.findViewById(R.id.quote_page);
        tvPage.setText(quote.getPage().getName());

        return convertView;
    }


}
