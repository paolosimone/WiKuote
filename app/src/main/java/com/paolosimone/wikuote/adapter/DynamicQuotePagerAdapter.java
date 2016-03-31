package com.paolosimone.wikuote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.model.Quote;

/**
 * Created by Paolo Simone on 24/03/2016.
 */
public class DynamicQuotePagerAdapter extends QuotePagerAdapter {

    private Quote loadingQuote;
    private View placeholderPage;

    public DynamicQuotePagerAdapter(Context context){
        super(context);
        this.loadingQuote = new Quote(context.getString(R.string.msg_loading),"");
    }

    public void addQuote(Quote quote){
        synchronized (quotes){
            if (placeholderPage !=null){
                setupPage(placeholderPage,quote);
                placeholderPage = null;
            }
            quotes.add(quote);
            notifyDataSetChanged();
        }
    }

    public void notifyErrorIfWaiting(Quote error){
        if (placeholderPage !=null){
            setupPage(placeholderPage,error);
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
        placeholderPage = page;

        container.addView(page);
        return page;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


}