package com.paolosimone.wikuote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.activity.FavoritesActivity;
import com.paolosimone.wikuote.model.Category;
import com.paolosimone.wikuote.model.Quote;

import java.util.ArrayList;

/**
 * Adapter that holds a list of favorite quotes and provide them to the ListView in FavoritesListFragment.
 */
public class FavoritesListAdapter extends ArrayAdapter<Quote> {

    private final String defaultCategory;

    /**
     * Creates a new empty adapter.
     * @param context the context in which the adapter is run
     */
    public FavoritesListAdapter(Context context){
        super(context,0, new ArrayList<Quote>());
        defaultCategory = context.getString(R.string.uncategorized);
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

        TextView tvCategory = (TextView) convertView.findViewById(R.id.quote_category);
        Category category = quote.getPage().getCategory();
        tvCategory.setText(category!=null ? category.getTitle() : defaultCategory);

        return convertView;
    }
}
