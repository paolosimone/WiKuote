package com.forfun.paolosimone.wikuote.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forfun.paolosimone.wikuote.R;
import com.forfun.paolosimone.wikuote.model.Quote;

import java.util.ArrayList;

/**
 * Created by Paolo Simone on 24/03/2016.
 */
public class QuotePagerAdapter extends PagerAdapter {

    private ArrayList<Quote> quotes = new ArrayList<>();
    private Quote loadingQuote;

    private LayoutInflater layoutInflater;
    private View loadingPage;


    public QuotePagerAdapter(Context context){
        this.layoutInflater = LayoutInflater.from(context);
        this.loadingQuote = Quote.loading(context);
    }

    public void setQuotes(ArrayList<Quote> quotes){
        this.quotes = quotes;
    }

    public ArrayList<Quote> getQuotes(){
        return (ArrayList<Quote>) quotes.clone();
    }

    public int getQuotesNumber(){
        return quotes.size();
    }

    public void addQuote(Quote quote){
        synchronized (quotes){
            if (loadingPage!=null){
                setupPage(loadingPage,quote);
                loadingPage = null;
            }
            quotes.add(quote);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return quotes.size() + 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View page =layoutInflater.inflate(R.layout.quote_page,container,false);

        Quote quote = (position==quotes.size()) ? loadingQuote : quotes.get(position);
        setupPage(page,quote);

        if (position==quotes.size()){
            loadingPage = page;
        }

        container.addView(page);
        return page;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private void setupPage(View view, Quote quote){
        TextView quoteTextView = (TextView) view.findViewById(R.id.quote_text);
        TextView authorTextView = (TextView) view.findViewById(R.id.author_text);

        quoteTextView.setText(quote.getText());
        authorTextView.setText(quote.getAuthor());
    }
}
