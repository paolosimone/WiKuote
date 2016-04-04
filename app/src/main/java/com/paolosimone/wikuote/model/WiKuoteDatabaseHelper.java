package com.paolosimone.wikuote.model;

import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by Paolo Simone on 02/04/2016.
 */
public class WiKuoteDatabaseHelper {

    //TODO last query, caching

    private static WiKuoteDatabaseHelper ourInstance = new WiKuoteDatabaseHelper();

    public static WiKuoteDatabaseHelper getInstance() {
        return ourInstance;
    }

    private WiKuoteDatabaseHelper() {}

    public List<Page> getPagesFromCategory(Category category){
        return new Select()
                .from(Page.class)
                .where("category=",category.getId())
                .execute();
    }

    public Page getPageFromName(String name){
        return new Select()
                .from(Page.class)
                .where("name=?", name)
                .executeSingle();
    }

    //TODO getPageFromId

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

    public boolean pageAlreadyEsists(Page page){
        return getPageFromName(page.name) != null;
    }

    public boolean categoryAlreadyExists(Category category){
        return getCategoryFromTitle(category.title) != null;
    }

    public void movePageToCategory(Page page, Category category){
        if (!categoryAlreadyExists(category)) {
            category.save();
        }
        page.category = category;
        page.save();
    }


}
