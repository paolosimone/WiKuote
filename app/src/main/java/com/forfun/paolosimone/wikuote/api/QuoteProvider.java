package com.forfun.paolosimone.wikuote.api;

import com.forfun.paolosimone.wikuote.exceptions.MissingAuthorException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Paolo Simone on 20/03/2016.
 */
public interface QuoteProvider {

    boolean isAvailableAuthor(String author) throws IOException;

    ArrayList<String> getSuggestedAuthors(String search) throws IOException;

    String getRandomQuoteFor(String author) throws IOException, MissingAuthorException;

}
