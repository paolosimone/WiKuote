package com.paolosimone.wikuote.api;

import com.paolosimone.wikuote.model.Page;
import com.paolosimone.wikuote.model.Quote;

/**
 * Contains the result of a fetching task.
 * The task could be successful, in which case it contains the retrieved quote, of can be failed,
 * in which case it contains the exception that has caused the failure and the page whose quote has been attempted to retrieve.
 */
public class FetchQuoteResult {

    private final Exception exception;
    private final Quote quote;

    /**
     * Create a successful result.
     * @param quote the quote that has been retrieved
     * @return the new successful result
     */
    public static FetchQuoteResult success(Quote quote){
        return new FetchQuoteResult(quote, null);
    }

    /**
     * Create a failed result.
     * @param page the page whose quote has been attempted to retrieve
     * @param exception the exception that has caused the failure
     * @return the new failed result
     */
    public static FetchQuoteResult error(Page page, Exception exception){
        return new FetchQuoteResult(new Quote(null, page), exception);
    }

    private FetchQuoteResult(Quote quote, Exception exception) {
        this.quote = quote;
        this.exception = exception;
    }

    /**
     * Check if the result is successful or not.
     * @return true if successful, false otherwise
     */
    public boolean isSuccessful() {
        return quote.getText() != null;
    }

    /**
     * The quote that has been retrieved.
     * @return the retrieved quote if any, null otherwise
     */
    public Quote getQuote() {
        return quote;
    }

    /**
     * The page that whose quote has been requested.
     * @return the page whose quote has been requested
     */
    public Page getPage() {
        return quote.getPage();
    }

    /**
     * The exception that has caused tha failure.
     * @return the exception that has caused tha failure if any, null otherwise
     */
    public Exception getException() {
        return exception;
    }
}
