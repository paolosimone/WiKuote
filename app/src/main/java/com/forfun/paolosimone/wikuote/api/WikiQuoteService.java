package com.forfun.paolosimone.wikuote.api;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Paolo Simone on 20/03/2016.
 */
public interface WikiQuoteService {

    @GET("api.php?action=query&format=json")
    Call<JsonElement> getPageFromTitle(@Query("titles") String titles);

    @GET("api.php?action=parse&format=json&prop=sections")
    Call<JsonElement> getTocFromPage(@Query("pageid") int pageid);

    @GET("api.php?action=parse&format=json&noimage")
    Call<JsonElement> getSectionFrom(@Query("pageid") int pageid,@Query("section") int section);
}
