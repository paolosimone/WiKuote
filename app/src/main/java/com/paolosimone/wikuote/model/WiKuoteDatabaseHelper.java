package com.paolosimone.wikuote.model;

import android.util.Log;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Paolo Simone on 02/04/2016.
 */
public class WiKuoteDatabaseHelper {

    //TODO last query, caching

    private Set<DatabaseObserver> subscribers = new HashSet<>();

    private static WiKuoteDatabaseHelper ourInstance = new WiKuoteDatabaseHelper();

    public static WiKuoteDatabaseHelper getInstance() {
        return ourInstance;
    }

    private WiKuoteDatabaseHelper() {}

    public void attach(DatabaseObserver observer){
        subscribers.add(observer);
    }

    public void detach(DatabaseObserver observer){
        subscribers.remove(observer);
    }

    public Page getPageFromName(String name){
        return new Select()
                .from(Page.class)
                .where("name=?", name)
                .executeSingle();
    }

    public List<Category> getAllCategories(){
        return new Select()
                .all()
                .from(Category.class)
                .execute();
    }

    public Category getCategoryFromTitle(String title){
        return new Select()
                .from(Category.class)
                .where("title=?", title)
                .executeSingle();
    }

    public boolean existsPage(Page page){
        return getPageFromName(page.name) != null;
    }

    public void deletePage(Page page){
        Category pageCategory = page.category;
        page.delete();

        if (pageCategory.getPages().isEmpty()){
            pageCategory.delete();
        }

        notifySubscribers();
    }

    public boolean existsCategory(Category category){
        return getCategoryFromTitle(category.title) != null;
    }

    public void movePageToCategory(Page page, Category category){
        if (!existsCategory(category)) {
            category.save();
        }
        page.category = category;
        page.save();
        notifySubscribers();
    }

    public void deleteCategory(Category category){
        category.delete();
        notifySubscribers();
    }

    private void notifySubscribers(){
        for (DatabaseObserver o : subscribers){
            o.onDataChanged();
        }
    }

    public interface DatabaseObserver {
        void onDataChanged();
    }


}
