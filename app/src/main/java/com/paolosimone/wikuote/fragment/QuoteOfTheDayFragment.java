package com.paolosimone.wikuote.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.api.WikiQuoteProvider;
import com.paolosimone.wikuote.model.Page;
import com.paolosimone.wikuote.model.Quote;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Paolo Simone on 15/04/2016.
 */
public class QuoteOfTheDayFragment extends QuoteFragment implements Titled{

    private FetchQOTDTask fetchQOTDTask;

    @Override
    public void onStart(){
        super.onStart();
        if (getCurrentQuote()==null) {
            new FetchQOTDTask().execute();
        }
    }

    @Override
    public void onDestroy(){
        if(fetchQOTDTask!=null) {
            fetchQOTDTask.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    public String getTitle(Context context){
        return context.getString(R.string.tab_quote_of_the_day);
    }

    private class FetchQOTDTask extends AsyncTask<Void, Void, Quote> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute(){
            pd = new ProgressDialog(getContext());
            pd.setCancelable(false);
            pd.requestWindowFeature(Window.FEATURE_NO_TITLE);
            pd.setMessage(getString(R.string.loading));
            pd.show();
        }

        @Override
        protected Quote doInBackground(Void... params) {
            try {
                return WikiQuoteProvider.getInstance().getQuoteOfTheDay();
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Quote result){
            fetchQOTDTask = null;
            pd.dismiss();

            if (result==null){
                Toast.makeText(getContext(), R.string.err_generic,Toast.LENGTH_LONG).show();
                return;
            }

            ArrayList<Quote> quotes = new ArrayList<>();
            quotes.add(result);
            changeQuotes(quotes);
            onQuoteChange(0);
        }
    }
}