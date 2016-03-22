package com.forfun.paolosimone.wikuote.api;

import com.forfun.paolosimone.wikuote.exceptions.MissingAuthorException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Paolo Simone on 20/03/2016.
 */
public class WikiQuoteProvider implements QuoteProvider{

    public static final String BASE_URL = "http://en.wikiquote.org/w/";

    private static WikiQuoteProvider ourInstance = new WikiQuoteProvider();
    private Retrofit retrofit;
    private WikiQuoteService wikiQuoteService;

    public static WikiQuoteProvider getInstance() {
        return ourInstance;
    }

    private WikiQuoteProvider() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        wikiQuoteService = retrofit.create(WikiQuoteService.class);
    }

    @Override
    public String getRandomQuoteFor(String author) throws IOException, MissingAuthorException {
        int maxTry = 10;
        int maxWords = 150;

        for (int i=0; i<maxTry; i++) {
            int pageid = getPageIndex(author);
            int sectionid = getRandomSection(pageid);
            String newQuote = getRandomQuoteFromSection(pageid, sectionid);

            int words = newQuote.split(" ").length;
            if (words<maxWords) return newQuote;
        }

        return null;
    }

    private int getPageIndex(String author) throws IOException, MissingAuthorException {
        Call<JsonElement> call = wikiQuoteService.getPageFromTitle(author);
        JsonObject response = (JsonObject) call.execute().body();

        int index = Utils.extractPageIndex(response);

        if (index == -1){
            throw new MissingAuthorException();
        }

        return index;
    }

    private int getRandomSection(int pageid) throws IOException {
        Call<JsonElement> call = wikiQuoteService.getTocFromPage(pageid);
        JsonObject response = (JsonObject) call.execute().body();

        List<Integer> indexes = Utils.extractSectionIndexList(response);


        if (indexes.isEmpty()){
            throw new IOException();
        }

        Random rand = new Random();
        return indexes.get(rand.nextInt(indexes.size()));
    }

    private String getRandomQuoteFromSection(int pageid, int sectionid) throws IOException {
        Call<JsonElement> call = wikiQuoteService.getSectionFrom(pageid, sectionid);
        JsonObject response = (JsonObject) call.execute().body();

        List<String> quotes = Utils.extractQuoteList(response);

        if (quotes.isEmpty()){
            throw new IOException();
        }

        Random rand = new Random();
        return quotes.get(rand.nextInt(quotes.size()));
    }
}
