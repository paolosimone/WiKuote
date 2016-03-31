package com.paolosimone.wikuote.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by psimo on 29/03/2016.
 */
@Table(name="Authors")
public class Author extends Model implements Parcelable{

    @Column(name = "name", index = true, unique = true, onUniqueConflict = Column.ConflictAction.IGNORE)
    String name;

    @Column(name = "category", index = true, onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.SET_NULL)
    Category category;

    @Column(name = "url", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    String url;

    public Author(){
        super();
    }

    public Author(String name, Category category, String url) {
        super();
        this.name = name;
        this.category = category;
        this.url = url;
    }

    public Author(String name){
        this(name, null, null);
    }

    protected Author(Parcel in) {
        super();
        name = in.readString();
        category = in.readParcelable(Category.class.getClassLoader());
        url = in.readString();
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeParcelable(category,flags);
        dest.writeString(url);
    }

    public static final Creator<Author> CREATOR = new Creator<Author>() {
        @Override
        public Author createFromParcel(Parcel in) {
            return new Author(in);
        }

        @Override
        public Author[] newArray(int size) {
            return new Author[size];
        }
    };
}
