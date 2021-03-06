package com.paolosimone.wikuote.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * A category is a collection of saved pages, identified by a title.
 */
@Table(name = "Categories")
public class Category extends Model implements Parcelable, Comparable<Category> {

    @Column (name = "title", index = true, unique = true, onUniqueConflict = Column.ConflictAction.FAIL)
    String title;

    public Category(){
        super();
    }

    /**
     * Create a new empty category with the given title.
     * @param title the title of the category
     */
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

    /**
     * Retrieve all the pages contained in this category.
     * @return the pages contained in this category
     */
    public List<Page> getPages(){
        return new Select()
                .from(Page.class)
                .where("category=?",this.getId())
                .orderBy("name")
                .execute();
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
