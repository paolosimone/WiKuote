package com.paolosimone.wikuote.api;

import com.paolosimone.wikuote.exceptions.MissingAuthorException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.paolosimone.wikuote.exceptions.ParserException;
import com.paolosimone.wikuote.model.Page;
import com.paolosimone.wikuote.model.Quote;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
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

    public static final String HOST = "en.m.wikiquote.org";
    public static final String BASE_URL = "https://" + HOST + "/";
    public static final String RANDOM_URL = BASE_URL + "wiki/Special:Random";

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
        return getPageIndex(author) != WikiQuoteUtils.INVALID_INDEX;
    }

    @Override
    public ArrayList<Page> getSuggestedPages(String query) throws IOException {
        Call<JsonElement> call = wikiQuoteService.getSuggestionsFromSearch(query);
        JsonArray response = (JsonArray) call.execute().body();

        return WikiQuoteUtils.extractSuggestions(response);
    }

    @Override
    public Page getRandomPage() throws IOException {
        URL randomUrl = new URL(RANDOM_URL);
        HttpURLConnection conn = (HttpURLConnection) randomUrl.openConnection();
        conn.setInstanceFollowRedirects(false);
        URL redirectUrl = new URL(conn.getHeaderField("Location"));

        String pageUrl = redirectUrl.toString();
        String pageName = WikiQuoteUtils.extractPageNameFromUrl(pageUrl);

        List<Page> matches = getSuggestedPages(pageName);
        if (matches.isEmpty()) {
            // The page name was extracted incorrectly
            throw new ParserException();
        }

        return matches.get(0);
    }

    @Override
    public Quote getRandomQuoteFor(Page page) throws IOException, MissingAuthorException {
        long pageId = getPageIndex(page.getName());
        if (pageId == WikiQuoteUtils.INVALID_INDEX) throw new MissingAuthorException();

        int sectionId = getRandomSection(pageId);
        String quoteText = getRandomQuoteFromSection(pageId, sectionId);
        return new Quote(quoteText,page);
    }

    @Override
    public Quote getQuoteOfTheDay() throws IOException {
        Document mainPage = Jsoup.connect(BASE_URL).get();

        String[] result = WikiQuoteUtils.extractQuoteOfTheDay(mainPage);
        String quoteText = result[WikiQuoteUtils.QUOTE];
        String pageName = result[WikiQuoteUtils.PAGE_NAME];

        List<Page> matches = getSuggestedPages(pageName);
        if (matches.isEmpty()) {
            // The page name was extracted incorrectly
            throw new ParserException();
        }

        return new Quote(quoteText,matches.get(0));
    }

    private long getPageIndex(String author) throws IOException{
        Call<JsonElement> call = wikiQuoteService.getPageFromTitle(author);
        JsonObject response = (JsonObject) call.execute().body();

        long index = WikiQuoteUtils.extractPageIndex(response);

        return index;
    }

    private int getRandomSection(long pageid) throws IOException {
        Call<JsonElement> call = wikiQuoteService.getTocFromPage(pageid);
        JsonObject response = (JsonObject) call.execute().body();

        List<Integer> indexes = WikiQuoteUtils.extractSectionIndexList(response);
        if (indexes.isEmpty()){
            // The requested page doesn't have a section list
            throw new ParserException();
        }

        Random rand = new Random();
        return indexes.get(rand.nextInt(indexes.size()));
    }

    private String getRandomQuoteFromSection(long pageid, int sectionid) throws IOException {
        Call<JsonElement> call = wikiQuoteService.getSectionFrom(pageid, sectionid);
        JsonObject response = (JsonObject) call.execute().body();

        List<String> quotes = WikiQuoteUtils.extractQuoteList(response);
        if (quotes.isEmpty()){
            // The given section doesn't contain quotes in the standard format
            throw new ParserException();
        }

        Random rand = new Random();
        return quotes.get(rand.nextInt(quotes.size()));
    }
}
