package com.forfun.paolosimone.wikuote.model;

/**
 * Created by Paolo Simone on 21/03/2016.
 */
public class Quote {

    private static String TEXT_LOADING = "Loading...";
    private static String TEXT_FAILURE = "Oops... Something went wrong!";

    private final String text;
    private final String author;

    public static Quote loading(){
        return new Quote(TEXT_LOADING, null);
    }

    public static Quote failure(){
        return new Quote(TEXT_FAILURE, null);
    }

    public Quote(String quote, String author) {
        if(quote==null){
            throw new IllegalArgumentException("The quote can't be null");
        }
        this.text = quote;
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }
}
