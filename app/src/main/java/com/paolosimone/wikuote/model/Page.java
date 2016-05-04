package com.paolosimone.wikuote.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

/**
 * A page is a source of quotes, identified by a name and a url.
 */
@Table(name="Pages")
public class Page extends Model implements Parcelable{

    @Column(name = "name", notNull = true, unique = true, onUniqueConflict = Column.ConflictAction.FAIL)
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

    /**
     * Create a new page associated to the given category.
     * @param name the name of the page
     * @param category the category associated to the page
     * @param description the description of the page
     * @param url the url of the page
     */
    public Page(String name, Category category, String description ,String url) {
        super();
        this.name = name;
        this.category = category;
        this.description = description;
        this.url = url;
    }

    /**
     * Create a new page that is not associated to any category.
     * @param name the name of the page
     * @param description the description of the page
     * @param url the url of the page
     */
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

    /**
     * Get all saved quotes belonging to the page.
     * @return the list of quotes belonging to the page
     */
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
