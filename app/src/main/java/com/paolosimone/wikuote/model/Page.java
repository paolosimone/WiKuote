package com.paolosimone.wikuote.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

/**
 * Created by psimo on 29/03/2016.
 */
@Table(name="Pages")
public class Page extends Model implements Parcelable{

    @Column(name = "name", unique = true, onUniqueConflict = Column.ConflictAction.FAIL)
    String name;

    @Column(name = "category", index = true, onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    Category category;

    @Column(name = "description")
    String description;

    @Column(name = "url", unique = true, onUniqueConflict = Column.ConflictAction.FAIL)
    String url;

    public Page(){
        super();
    }

    public Page(String name, Category category, String description ,String url) {
        super();
        this.name = name;
        this.category = category;
        this.description = description;
        this.url = url;
    }

    public Page(String name, String description ,String url) {
        this(name, null, description, url);
    }

    protected Page(Parcel in) {
        super();
        name = in.readString();
        category = in.readParcelable(Category.class.getClassLoader());
        description = in.readString();
        url = in.readString();
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public List<Quote> getQuotes() {
        return getMany(Quote.class, "page");
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeParcelable(category,flags);
        dest.writeString(description);
        dest.writeString(url);
    }

    public static final Creator<Page> CREATOR = new Creator<Page>() {
        @Override
        public Page createFromParcel(Parcel in) {
            return new Page(in);
        }

        @Override
        public Page[] newArray(int size) {
            return new Page[size];
        }
    };
}
