package com.forfun.paolosimone.wikuote.model;

import android.content.Context;

import com.forfun.paolosimone.wikuote.R;

/**
 * Created by Paolo Simone on 22/03/2016.
 */
public class FetchQuoteResult {

    public static final String SUCCESS = "ok";
    public static final String GENERIC_ERROR = "error";
    public static final String MISSING_AUTHOR = "missing";
    public static final String NETWORK_ERROR = "network_error";

    private final String status;
    private final String message;
    private final Quote quote;

    public static FetchQuoteResult success(Quote quote, Context context){
        String message = context.getString(R.string.msg_fetch_success);
        return new FetchQuoteResult(SUCCESS,message,quote);
    }

    public static FetchQuoteResult error(Context context){
        String message = context.getString(R.string.msg_generic_error);
        return new FetchQuoteResult(GENERIC_ERROR,message,null);
    }

    public static FetchQuoteResult missingAuthor(Context context){
        String message = context.getString(R.string.msg_missing_author);
        return new FetchQuoteResult(MISSING_AUTHOR,message,null);
    }

    public static FetchQuoteResult networkError(Context context){
        String message = context.getString(R.string.msg_network_error);
        return new FetchQuoteResult(NETWORK_ERROR,message,null);
    }

    private FetchQuoteResult(String status, String message, Quote quote) {
        this.status = status;
        this.message = message;
        this.quote = quote;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Quote getQuote() {
        return quote;
    }
}
