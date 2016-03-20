package com.forfun.paolosimone.wikuote.api;

import com.forfun.paolosimone.wikuote.exceptions.MissingAuthorException;

import java.io.IOException;

/**
 * Created by Paolo Simone on 20/03/2016.
 */
public interface QuoteProvider {

    String getRandomQuoteFor(String author) throws IOException, MissingAuthorException;

}
