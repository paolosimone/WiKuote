package com.paolosimone.wikuote.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.paolosimone.wikuote.R;

import java.util.List;

/**
 * Created by Paolo Simone on 25/03/2016.
 */
@Table(name = "Categories")
public class Category extends Model implements Parcelable, Comparable<Category> {

    @Column (name = "title", index = true, unique = true, onUniqueConflict = Column.ConflictAction.FAIL)
    String title;

    public Category(){
        super();
    }

    public Category(String title) {
        super();
        this.title = title;
    }

    protected Category(Parcel in) {
        super();
        title = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public List<Page> getPages(){
        return getMany(Page.class, "category");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
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

    @Override
    public boolean equals(Object other){
        return (other instanceof Category) && title.equals(((Category) other).title);
    }

    @Override
    public int compareTo(Category other) {
        return this.title.compareTo(other.title);
    }
}
