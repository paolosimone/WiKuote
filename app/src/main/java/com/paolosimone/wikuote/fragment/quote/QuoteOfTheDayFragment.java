package com.paolosimone.wikuote.fragment.quote;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Window;
import android.widget.Toast;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.api.WikiQuoteProvider;
import com.paolosimone.wikuote.fragment.Titled;
import com.paolosimone.wikuote.model.Quote;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Retrieve and shows the "quote of the day".
 */
public class QuoteOfTheDayFragment extends QuoteFragment implements Titled {

    private static Quote todaysQuote;

    private FetchQOTDTask fetchQOTDTask;

    @Override
    public void onStart(){
        super.onStart();
        if (!isValidTodaysQuote()) {
            new FetchQOTDTask().execute();
        }
        else {
            updateTodaysQuote();
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

    private boolean isValidTodaysQuote(){
        if (todaysQuote==null){
            return false;
        }

        int today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        Calendar savedQuoteDay = Calendar.getInstance();
        savedQuoteDay.setTime(todaysQuote.getTimestamp());
        return savedQuoteDay.get(Calendar.DAY_OF_YEAR) == today;
    }

    private void updateTodaysQuote(){
        ArrayList<Quote> quotes = new ArrayList<>();
        quotes.add(todaysQuote);
        changeQuotes(quotes);
    }

    /**
     * Asynchronous task that handles the retrieval of the quote of the day.
     */
    private class FetchQOTDTask extends AsyncTask<Void, Void, Quote> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute(){
            pd = new ProgressDialog(getActivity());
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

            todaysQuote = result;
            updateTodaysQuote();
        }
    }
}
