package com.paolosimone.wikuote.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.model.Quote;

import java.util.ArrayList;

/**
 * Created by Paolo Simone on 24/03/2016.
 */
public class QuotePagerAdapter extends PagerAdapter {

    protected ArrayList<Quote> quotes = new ArrayList<>();

    private Context context;

    public QuotePagerAdapter(Context context){
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setQuotes(ArrayList<Quote> quotes){
        this.quotes = quotes;
        notifyDataSetChanged();
    }

    public ArrayList<Quote> getQuotes(){
        return (ArrayList<Quote>) quotes.clone();
    }

    public int getQuotesNumber(){
        return quotes.size();
    }

    @Override
    public int getCount() {
        return quotes.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (getCount()==0){
            // TODO add placeholder here in case of error
        }

        View page = LayoutInflater.from(context).inflate(R.layout.quote_page, container, false);

        Quote quote = quotes.get(position);
        setupPage(page,quote);

        container.addView(page);
        return page;
    }

    protected void setupPage(View view, Quote quote){
        TextView quoteTextView = (TextView) view.findViewById(R.id.quote_text);
        TextView authorTextView = (TextView) view.findViewById(R.id.author_text);

        quoteTextView.setText(quote.getText());
        authorTextView.setText(quote.getPage().getName());
    }
}
