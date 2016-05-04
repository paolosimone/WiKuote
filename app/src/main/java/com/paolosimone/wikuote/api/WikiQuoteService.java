package com.paolosimone.wikuote.api;

import com.google.gson.JsonElement;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit description of the web requests that are sent to WikiQuote.org.
 */
public interface WikiQuoteService {

    /**
     * Requests for suggested pages, matching the given query.
     * @param search the search query
     * @return the server response, in form of JSON
     */
    @GET("w/api.php?action=opensearch&format=json&suggest&redirects=resolve")
    Call<JsonElement> getSuggestionsFromSearch(@Query("search") String search);

    /**
     * Requests for a page having the given title.
     * @param titles the title of the page
     * @return the server response, in form of JSON
     */
    @GET("w/api.php?action=query&format=json&redirects")
    Call<JsonElement> getPageFromTitle(@Query("titles") String titles);

    /**
     * Requests for the table of content of the page having the given id.
     * @param pageid the id of the page
     * @return the server response, in form of JSON
     */
    @GET("w/api.php?action=parse&format=json&prop=sections")
    Call<JsonElement> getTocFromPage(@Query("pageid") long pageid);

    /**
     * Requests for a specific section of the page having the given id.
     * @param pageid the id of the page
     * @param section the id of the section
     * @return the server response, in form of JSON
     */
    @GET("w/api.php?action=parse&format=json&noimage")
    Call<JsonElement> getSectionFrom(@Query("pageid") long pageid,@Query("section") long section);
}
