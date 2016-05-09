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
 * Adapter that holds a dynamic list of quotes and provides them to the ViewPager in the DynamicQuoteFragment.
 */
public class DynamicQuotePagerAdapter extends QuotePagerAdapter {

    private View placeholderPage;

    /**
     * Creates an empty adapter.
     * @param context the context in which the adapter is run
     */
    public DynamicQuotePagerAdapter(Context context){
        super(context);
    }

    /**
     * Append the given quote to the list.
     * @param quote the quote to be added to the list
     */
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

    /**
     * Check if the user is watching a blank page, waiting for a quote to be retrieved.
     * @return true if the user is waiting, false otherwise
     */
    public boolean userIsWaiting(){
        return placeholderPage != null;
    }

    /**
     * Notify the error to the user, only if the user is waiting for a quote.
     * @param error the text describing the error
     */
    public void silentNotifyError(String error){
        if (userIsWaiting()){
            Quote errorQuote = new Quote(error,new Page("","",""));
            setupPage(placeholderPage,errorQuote);
        }
    }

    /**
     * Notify that no quotes can be retrieved for the current page, only if the user is waiting for a quote.
     * In addition to the error message, a button is shown in order to open the web page containing all the quotes.
     * @param error the text describing the error
     * @param listener the listener that is called when the user click the button
     */
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
