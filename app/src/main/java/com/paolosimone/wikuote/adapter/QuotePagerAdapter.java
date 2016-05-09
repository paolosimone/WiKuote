package com.paolosimone.wikuote.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.model.Quote;

import java.util.ArrayList;

/**
 * Adapter that holds a fixed list of quotes and provide them to the ViewPager inside the QuoteFragment.
 */
public class QuotePagerAdapter extends PagerAdapter {

    protected ArrayList<Quote> quotes = new ArrayList<>();

    private Context context;

    /**
     * Creates a new adapter.
     * @param context the activity context in which the adapter is run
     */
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

    /**
     * Get a copy of the array of quotes stored in the adapter.
     * @return the copy of the array of quotes in the adapter
     */
    public ArrayList<Quote> getQuotes(){
        return (ArrayList<Quote>) quotes.clone();
    }

    /**
     * Return the number of quotes currently contained in the adapter.
     * @return the number of quotes currently contained in the adapter
     */
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
        View page = LayoutInflater.from(context).inflate(R.layout.page_quote, container, false);

        Quote quote = quotes.get(position);
        setupPage(page,quote);

        container.addView(page);
        return page;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    /**
     * Setup the inflated page using the information contained in the given quote.
     * @param view the view to be setup
     * @param quote the quote to be presented in the page
     */
    protected void setupPage(View view, Quote quote){
        hideLoading(view);

        TextView quoteTextView = (TextView) view.findViewById(R.id.quote_text);
        TextView authorTextView = (TextView) view.findViewById(R.id.author_text);

        quoteTextView.setText(quote.getText());
        authorTextView.setText(quote.getPage().getName());
    }

    /**
     * Hide the loading spinner in the page.
     * @param view the page whose loading spinner must be hidden
     */
    protected void hideLoading(View view) {
        ProgressBar loading = (ProgressBar) view.findViewById(R.id.loading_spinner);
        loading.setVisibility(View.INVISIBLE);
    }
}
