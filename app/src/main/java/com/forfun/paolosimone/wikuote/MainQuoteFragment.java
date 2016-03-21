package com.forfun.paolosimone.wikuote;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.os.AsyncTaskCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forfun.paolosimone.wikuote.api.QuoteProvider;
import com.forfun.paolosimone.wikuote.api.WikiQuoteProvider;
import com.forfun.paolosimone.wikuote.exceptions.MissingAuthorException;
import com.forfun.paolosimone.wikuote.model.Quote;
import com.google.gson.JsonElement;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainQuoteFragment extends Fragment {

    private Quote quote;
    private QuoteProvider quoteProvider;
    private AsyncTask currentTask;

    private TextView quoteTextView;
    private TextView authorTextView;

    public MainQuoteFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        quoteProvider = WikiQuoteProvider.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        quoteTextView = (TextView) view.findViewById(R.id.quote_text);
        authorTextView = (TextView) view.findViewById(R.id.author_text);
    }

    @Override
    public void onDestroyView(){
        quoteTextView = null;
        authorTextView = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy(){
        if (currentTask!=null){
            currentTask.cancel(true);
        }
        super.onDestroy();
    }

    public void newQuoteFor(String author){
        currentTask = new FetchQuoteTask().execute(author);
    }

    private void updateQuote(){
        if (quoteTextView!=null){
            quoteTextView.setText(quote.getText());
        }
        if (authorTextView!=null){
            String authorText =  (quote.getAuthor()!=null) ?
                    quote.getAuthor() : "";
            authorTextView.setText(authorText);
        }
    }

    private class FetchQuoteTask extends AsyncTask<String, Void, Quote> {

        @Override
        protected void onPreExecute(){
            quote = Quote.loading();
            updateQuote();
        }

        @Override
        protected Quote doInBackground(String... authors) {
            String newText;
            try {
                newText = quoteProvider.getRandomQuoteFor(authors[0]);
            } catch (MissingAuthorException e) {
                newText = "The requested author doesn't exists";
            } catch (IOException e) {
                newText = "Unable to reach the service! \n Check your connection and retry";
            }
            return (newText!=null) ? new Quote(newText,authors[0]) : Quote.failure();
        }

        @Override
        protected void onPostExecute(Quote newQuote){
            currentTask = null;
            quote = newQuote;
            updateQuote();
        }
    }
}
