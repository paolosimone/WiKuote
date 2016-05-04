package com.paolosimone.wikuote.api;

import com.paolosimone.wikuote.exceptions.MissingPageException;
import com.paolosimone.wikuote.model.Page;
import com.paolosimone.wikuote.model.Quote;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Provider that take care of retrieve new quotes for the application.
 * The methods of this interface can safely make synchronous calls to long-running tasks.
 */
public interface QuoteProvider {

    /**
     * Check if the the given page is available in the provider.
     * @param page the page to be checked
     * @return true if the page is present in the provider, false otherwise
     * @throws IOException
     */
    boolean isAvailablePage(String page) throws IOException;

    /**
     * Retrieve a possibly empty list of pages matching the given query.
     * @param query the query that searches for pages
     * @return the list of matching pages
     * @throws IOException
     */
    ArrayList<Page> getSuggestedPages(String query) throws IOException;

    /**
     * Retrieve a random page.
     * @return a random page from the provider
     * @throws IOException
     */
    Page getRandomPage() throws  IOException;

    /**
     * Retrieve a random quote from the given page.
     * @param page the page from which retrieve the quote
     * @return a random quote from the given page
     * @throws IOException
     * @throws MissingPageException if the requested page is not supported by the provider
     */
    Quote getRandomQuoteFor(Page page) throws IOException, MissingPageException;

    /**
     * Retrieve the quote of the day.
     * @return the quote of the day
     * @throws IOException
     */
    Quote getQuoteOfTheDay() throws IOException;

}
