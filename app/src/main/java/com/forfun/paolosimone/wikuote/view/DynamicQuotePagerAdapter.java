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
public class DynamicQuotePagerAdapter extends QuotePagerAdapter {

    private Quote loadingQuote;
    private View loadingPage;

    public DynamicQuotePagerAdapter(Context context){
        super(context);
        this.loadingQuote = Quote.loading(context);
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
    public Object instantiateItem(ViewGroup container, int position) {
        if (position < quotes.size()){
            return super.instantiateItem(container,position);
        }

        View page = LayoutInflater.from(getContext()).inflate(R.layout.quote_page,container,false);

        setupPage(page, loadingQuote);
        loadingPage = page;

        container.addView(page);
        return page;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


}
