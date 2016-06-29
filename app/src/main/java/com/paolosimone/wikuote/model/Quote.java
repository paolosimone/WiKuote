package com.paolosimone.wikuote.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * A quote is a text taken from a page.
 */
@Table(name="Quotes")
public class Quote extends Model implements Parcelable{

    @Column(name="timestamp")
    Date timestamp;

    @Column(name="text", notNull = true, unique=true, onUniqueConflict=Column.ConflictAction.IGNORE)
    String text;

    @Column(name="page", notNull = true, onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    Page page;

    public Quote(){
        super();
    }

    /**
     * Create a quote.
     * @param quote the text of the quote
     * @param page the page from which the quote is taken
     */
    public Quote(String quote, Page page) {
        super();
        this.timestamp = Calendar.getInstance().getTime();
        this.text = quote;
        this.page = page;
    }

    protected Quote(Parcel in) {
        super();
        timestamp = new Date(in.readLong());
        text = in.readString();
        page = in.readParcelable(Page.class.getClassLoader());
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Page getPage() {
        return page;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Quote)) return false;
        Quote otherQuote = (Quote) other;
        return page.getName().equals(otherQuote.page.getName()) && text.equals(otherQuote.text);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(timestamp.getTime());
        dest.writeString(text);
        dest.writeParcelable(page, flags);
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
