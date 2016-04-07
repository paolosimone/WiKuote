package com.paolosimone.wikuote.api;

import com.paolosimone.wikuote.exceptions.MissingAuthorException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.paolosimone.wikuote.model.Page;
import com.paolosimone.wikuote.model.Quote;

import java.io.IOException;
import java.util.ArrayList;
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
    public boolean isAvailableAuthor(String author) throws IOException {
        return getPageIndex(author) != Utils.INVALID_INDEX;
    }

    @Override
    public ArrayList<Page> getSuggestedAuthors(String query) throws IOException {
        Call<JsonElement> call = wikiQuoteService.getSuggestionsFromSearch(query);
        JsonArray response = (JsonArray) call.execute().body();

        return Utils.extractSuggestions(response);
    }

    @Override
    public Quote getRandomQuoteFor(Page page) throws IOException, MissingAuthorException {
        long pageId = getPageIndex(page.getName());
        if (pageId == Utils.INVALID_INDEX) throw new MissingAuthorException();

        int sectionId = getRandomSection(pageId);
        String quoteText = getRandomQuoteFromSection(pageId, sectionId);
        return new Quote(quoteText,page);
    }

    private long getPageIndex(String author) throws IOException{
        Call<JsonElement> call = wikiQuoteService.getPageFromTitle(author);
        JsonObject response = (JsonObject) call.execute().body();

        long index = Utils.extractPageIndex(response);

        return index;
    }

    private int getRandomSection(long pageid) throws IOException {
        Call<JsonElement> call = wikiQuoteService.getTocFromPage(pageid);
        JsonObject response = (JsonObject) call.execute().body();

        List<Integer> indexes = Utils.extractSectionIndexList(response);


        if (indexes.isEmpty()){
            throw new IOException();
        }

        Random rand = new Random();
        return indexes.get(rand.nextInt(indexes.size()));
    }

    private String getRandomQuoteFromSection(long pageid, int sectionid) throws IOException {
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
