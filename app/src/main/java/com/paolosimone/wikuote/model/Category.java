package com.paolosimone.wikuote.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Paolo Simone on 25/03/2016.
 */
public class Category implements Parcelable {

    String title;
    ArrayList<String> authors;

    public Category(String title, ArrayList<String> authors) {
        if(title==null || authors==null){
            throw new IllegalArgumentException("The title and author can't be null");
        }
        this.title = title;
        this.authors = authors;
    }

    protected Category(Parcel in) {
        title = in.readString();
        authors = new ArrayList<>();
        in.readStringList(authors);
    }

    public ArrayList<String> getAuthors() {
        return authors;
    }

    public String getTitle() {
        return title;
    }

    public void addAuthor(String author){
        authors.add(author);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeStringList(authors);
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
