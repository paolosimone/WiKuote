package com.paolosimone.wikuote.api;

import com.paolosimone.wikuote.exceptions.MissingAuthorException;
import com.paolosimone.wikuote.model.Page;
import com.paolosimone.wikuote.model.Quote;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Paolo Simone on 20/03/2016.
 */
public interface QuoteProvider {

    boolean isAvailableAuthor(String author) throws IOException;

    ArrayList<Page> getSuggestedPages(String query) throws IOException;

    Page getRandomPage() throws  IOException;

    Quote getRandomQuoteFor(Page page) throws IOException, MissingAuthorException;

    Quote getQuoteOfTheDay() throws IOException;

}
