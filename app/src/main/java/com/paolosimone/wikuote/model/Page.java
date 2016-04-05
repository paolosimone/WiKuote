package com.paolosimone.wikuote.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by psimo on 29/03/2016.
 */
@Table(name="Pages")
public class Page extends Model implements Parcelable{

    //TODO remoteId

    @Column(name = "name", index = true, unique = true, onUniqueConflict = Column.ConflictAction.IGNORE)
    String name;

    @Column(name = "category", notNull = true, onNullConflict = Column.ConflictAction.FAIL, index = true, onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    Category category;

    //TODO description

    @Column(name = "url", unique = true, onUniqueConflict = Column.ConflictAction.IGNORE)
    String url;

    public Page(){
        super();
    }

    public Page(String name, Category category, String url) {
        super();
        this.name = name;
        this.category = category;
        this.url = url;
    }

    public Page(String name){
        this(name, null, null);
    }

    protected Page(Parcel in) {
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
