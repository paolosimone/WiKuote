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

    public Quote getQuoteFromText(String text){
        return new Select()
                .from(Quote.class)
                .where("text=?", text)
                .executeSingle();
    }

    public boolean existsQuote(Quote quote){
        return getQuoteFromText(quote.text) != null;
    }

    public List<Quote> getAllQuotes(){
        return new Select()
                .from(Quote.class)
                .join(Page.class).on("Quotes.page = Pages.id")
                .orderBy("Pages.name")
                .execute();
    }

    public void saveFavorite(Quote quote){
        Page page = quote.getPage();
        if (!existsPage(page)){
            page.save();
        }

        quote.save();
        notifySubscribers();
    }

    public void deleteFavorite(Quote toDelete){
        Quote quote = getQuoteFromText(toDelete.getText());
        if (quote==null) return;

        Page page = quote.getPage();
        quote.delete();

        if (page.getCategory()==null && page.getQuotes().isEmpty()){
            page.delete();
        }
        notifySubscribers();
    }

    public Page getPageFromName(String name){
        return new Select()
                .from(Page.class)
                .where("name=?", name)
                .executeSingle();
    }

    public boolean existsPage(Page page){
        return getPageFromName(page.name) != null;
    }

    public List<Page> getAllPages(){
        return new Select()
                .all()
                .from(Page.class)
                .orderBy("name")
                .execute();
    }

    public void deletePage(Page page){
        Category pageCategory = page.category;
        page.delete();

        if (pageCategory.getPages().isEmpty()){
            pageCategory.delete();
        }

        notifySubscribers();
    }

    public void movePageToCategory(Page page, Category category){
        Category oldCategory = page.getCategory();
        if (!existsCategory(category)) {
            category.save();
        }
        page.category = category;
        page.save();

        if (oldCategory!=null && oldCategory.getPages().isEmpty()){
            oldCategory.delete();
        }

        notifySubscribers();
    }

    public boolean existsCategory(Category category){
        return getCategoryFromTitle(category.title) != null;
    }

    public List<Category> getAllCategories(){
        return new Select()
                .all()
                .from(Category.class)
                .orderBy("title")
                .execute();
    }

    public Category getCategoryFromTitle(String title){
        return new Select()
                .from(Category.class)
                .where("title=?", title)
                .executeSingle();
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
