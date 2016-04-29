package com.paolosimone.wikuote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.activity.MainActivity;
import com.paolosimone.wikuote.activity.WiKuoteNavUtils;
import com.paolosimone.wikuote.model.Page;
import com.paolosimone.wikuote.model.Quote;

/**
 * Created by Paolo Simone on 24/03/2016.
 */
public class DynamicQuotePagerAdapter extends QuotePagerAdapter {

    private View placeholderPage;

    public DynamicQuotePagerAdapter(Context context){
        super(context);
    }

    public void addQuote(Quote quote){
        synchronized (quotes){
            if (placeholderPage !=null){
                hideWebViewButton();
                setupPage(placeholderPage,quote);
                placeholderPage = null;
            }
            quotes.add(quote);
            notifyDataSetChanged();
        }
    }

    public boolean userIsWaiting(){
        return placeholderPage != null;
    }

    public void silentNotifyError(String error){
        if (userIsWaiting()){
            Quote errorQuote = new Quote(error,new Page("","",""));
            setupPage(placeholderPage,errorQuote);
        }
    }

    public void silentNotifyParserError(String error, View.OnClickListener listener) {
        if (userIsWaiting()) {
            silentNotifyError(error);
            Button btnWebView = (Button) placeholderPage.findViewById(R.id.btn_webview);
            btnWebView.setOnClickListener(listener);
            btnWebView.setVisibility(View.VISIBLE);
        }
    }

    private void hideWebViewButton() {
        Button btnWebView = (Button) placeholderPage.findViewById(R.id.btn_webview);
        btnWebView.setVisibility(View.INVISIBLE);
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

        View page = LayoutInflater.from(getContext()).inflate(R.layout.page_quote,container,false);
        placeholderPage = page;

        container.addView(page);
        return page;
    }
}
