package com.paolosimone.wikuote.api;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Paolo Simone on 20/03/2016.
 */
public interface WikiQuoteService {

    @GET("api.php?action=opensearch&format=json&suggest&redirects=resolve")
    Call<JsonElement> getSuggestionsFromSearch(@Query("search") String search);

    @GET("api.php?action=query&format=json&redirects")
    Call<JsonElement> getPageFromTitle(@Query("titles") String titles);

    @GET("api.php?action=parse&format=json&prop=sections")
    Call<JsonElement> getTocFromPage(@Query("pageid") long pageid);

    @GET("api.php?action=parse&format=json&noimage")
    Call<JsonElement> getSectionFrom(@Query("pageid") long pageid,@Query("section") long section);
}
