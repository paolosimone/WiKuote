package com.forfun.paolosimone.wikuote.model;


import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.forfun.paolosimone.wikuote.R;

/**
 * Created by Paolo Simone on 21/03/2016.
 */
public class Quote implements Parcelable{

    String text;
    String author;

    public Quote(String quote, String author) {
        if(quote==null || author==null){
            throw new IllegalArgumentException("The quote and author can't be null");
        }
        this.text = quote;
        this.author = author;
    }

    protected Quote(Parcel in) {
        text = in.readString();
        author = in.readString();
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(author);
    }

    public static final Creator<Quote> CREATOR = new Creator<Quote>() {
        @Override
        public Quote createFromParcel(Parcel in) {
            return new Quote(in);
        }

        @Override
        public Quote[] newArray(int size) {
            return new Quote[size];
        }
    };
}
