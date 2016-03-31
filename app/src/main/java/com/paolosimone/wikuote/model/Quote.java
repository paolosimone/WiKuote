package com.paolosimone.wikuote.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Paolo Simone on 21/03/2016.
 */
@Table(name="Quotes")
public class Quote extends Model implements Parcelable{

    @Column(name="text", unique=true, onUniqueConflict=Column.ConflictAction.IGNORE)
    String text;

    @Column(name="author", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    Author author;

    @Column(name="category", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.SET_NULL)
    Category category;

    public Quote(){
        super();
    }

    public Quote(String quote, Author author, Category category) {
        super();
        this.text = quote;
        this.author = author;
        this.category = category;
    }

    public Quote(String quote, Author author){
        this(quote, author, null);
    }

    protected Quote(Parcel in) {
        super();
        text = in.readString();
        author = in.readParcelable(Author.class.getClassLoader());
        category = in.readParcelable(Category.class.getClassLoader());
    }

    public Author getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeParcelable(author, flags);
        dest.writeParcelable(category,flags);
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
