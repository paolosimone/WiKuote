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

    @Column(name="page", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    Page page;

    public Quote(){
        super();
    }

    public Quote(String quote, Page page) {
        super();
        this.text = quote;
        this.page = page;
    }

    protected Quote(Parcel in) {
        super();
        text = in.readString();
        page = in.readParcelable(Page.class.getClassLoader());
    }

    public Page getPage() {
        return page;
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
