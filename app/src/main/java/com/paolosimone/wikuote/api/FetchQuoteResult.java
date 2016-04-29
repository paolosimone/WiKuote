package com.paolosimone.wikuote.api;

import android.content.Context;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.model.Page;
import com.paolosimone.wikuote.model.Quote;

/**
 * Created by Paolo Simone on 22/03/2016.
 */
public class FetchQuoteResult {

    private final Exception exception;
    private final Quote quote;

    public static FetchQuoteResult success(Quote quote){
        return new FetchQuoteResult(quote, null);
    }

    public static FetchQuoteResult error(Page page, Exception exception){
        return new FetchQuoteResult(new Quote(null, page), exception);
    }

    private FetchQuoteResult(Quote quote, Exception exception) {
        this.quote = quote;
        this.exception = exception;
    }

    public boolean isSuccessful() {
        return quote.getText() != null;
    }

    public Quote getQuote() {
        return quote;
    }

    public Page getPage() {
        return quote.getPage();
    }

    public Exception getException() {
        return exception;
    }
}
