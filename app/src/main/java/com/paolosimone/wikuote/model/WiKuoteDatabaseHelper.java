package com.paolosimone.wikuote.model;

import com.activeandroid.query.Select;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The database helper offers method to perform queries and store data on the database.
 * Every action on the database should be done by invoking methods of this class.
 */
public class WiKuoteDatabaseHelper {

    //TODO last query, caching

    private Set<DatabaseObserver> subscribers = new HashSet<>();

    private static WiKuoteDatabaseHelper ourInstance = new WiKuoteDatabaseHelper();

    /**
     * Retrieve the instance of the database helper.
     * @return the instance of the database
     */
    public static WiKuoteDatabaseHelper getInstance() {
        return ourInstance;
    }

    private WiKuoteDatabaseHelper() {}

    /** Quote **/

    /**
     * Retrieve the quote having the given text.
     * @param text the text of the quote
     * @return the quote if found, null otherwise
     */
    public Quote getQuoteFromText(String text){
        return new Select()
                .from(Quote.class)
                .where("text=?", text)
                .executeSingle();
    }

    /**
     * Check if the given quote is already present in the database
     * @param quote the quote to be searched
     * @return true is the quote is already present, false otherwise
     */
    public boolean existsQuote(Quote quote){
        return getQuoteFromText(quote.text) != null;
    }

    /**
     * Retrieve all the quotes stored in the database, ordered by page name.
     * @return the list of all quotes in the database
     */
    public List<Quote> getAllQuotes(){
        return new Select()
                .from(Quote.class)
                .join(Page.class).on("Quotes.page = Pages.id")
                .orderBy("Pages.name")
                .execute();
    }

    /**
     * Store the given quote in the database.
     * @param quote the quote to be stored
     */
    public void saveFavorite(Quote quote){
        Page page = quote.getPage();
        if (!existsPage(page)){
            page.save();
        }

        quote.save();
        notifySubscribers();
    }

    /**
     * Remove the given quote from the database, if present.
     * @param toDelete the quote to be deleted
     */
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

    /** Page **/

    /**
     * Retrieve the page having the given name.
     * @param name the name of the page
     * @return the page if found, null otherwise
     */
    public Page getPageFromName(String name){
        return new Select()
                .from(Page.class)
                .where("lower(name)=lower(?)", name)
                .executeSingle();
    }

    /**
     * Check if the given page already exists.
     * @param page the page to be searched
     * @return true if the page already exists, false otherwise
     */
    public boolean existsPage(Page page){
        return getPageFromName(page.name) != null;
    }

    /**
     * Retrieve all the pages stored in the database, ordered by name.
     * @return the list of all pages in the database
     */
    public List<Page> getAllPages(){
        return new Select()
                .all()
                .from(Page.class)
                .orderBy("name")
                .execute();
    }

    /**
     * Delete the given page from the database, if present.
     * If the page was the only page associated to a category, it deletes also the category.
     * @param page the page to be deleted
     */
    public void deletePage(Page page){
        Category pageCategory = page.category;
        page.delete();

        if (pageCategory.getPages().isEmpty()){
            pageCategory.delete();
        }

        notifySubscribers();
    }

    /**
     * Assign the given page to the given category.
     * If the page had already a category, and it was the only page associated to that category, the old category is deleted.
     * @param page the page which category must be assigned
     * @param category the category that must be assigned to the page
     */
    public void movePageToCategory(Page page, Category category){
        Category oldCategory = page.getCategory();
        if (!existsCategory(category)) {
            category.title = validateTitle(category.title);
            category.save();
        }
        page.category = category;
        page.save();

        if (oldCategory!=null && oldCategory.getPages().isEmpty()){
            oldCategory.delete();
        }

        notifySubscribers();
    }

    /** Category **/

    /**
     * Check if the given category is already present in the database.
     * @param category the category to be searched
     * @return true if the category is already present, false otherwise
     */
    public boolean existsCategory(Category category){
        return getCategoryFromTitle(category.title) != null;
    }

    /**
     * Retrieves all the categories stored in the database, ordered by title.
     * @return all the categories stored in the database
     */
    public List<Category> getAllCategories(){
        return new Select()
                .all()
                .from(Category.class)
                .orderBy("title")
                .execute();
    }

    /**
     * Retrieve the category having the given title.
     * @param title the title of the category
     * @return the categoy if found, null otherwise
     */
    public Category getCategoryFromTitle(String title){
        return new Select()
                .from(Category.class)
                .where("lower(title)=lower(?)", title)
                .executeSingle();
    }

    /**
     * Delete the given category, if present in the database.
     * @param category the category to be deleted
     */
    public void deleteCategory(Category category){
        category.delete();
        notifySubscribers();
    }

    /**
     * Check if the given string is a valid title for a category.
     * @param title the string to be checked
     * @return true is the string represents a valid title, false otherwise
     */
    public boolean isValidCategoryTitle(String title){
        // TODO more complex checking
        return !title.equals("");
    }

    private String validateTitle(String title){
        return title.toUpperCase().charAt(0) + ((title.length()>1) ? title.substring(1) : "");
    }

    /**
     * Rename the given category with a new title.
     * @param category the category to be renamed
     * @param newTitle the new title of the category
     */
    public void renameCategory(Category category, String newTitle){
        category.title = validateTitle(newTitle);
        category.save();
        notifySubscribers();
    }

    /** Subscribers **/

    /**
     * Attach an observer to the database, that will be notified at any change.
     * @param observer the observer to be attached.
     */
    public void attach(DatabaseObserver observer){
        subscribers.add(observer);
    }

    /**
     * Detach an observer from the database.
     * @param observer the observer to be detached
     */
    public void detach(DatabaseObserver observer){
        subscribers.remove(observer);
    }

    private void notifySubscribers(){
        for (DatabaseObserver o : subscribers){
            o.onDataChanged();
        }
    }

    /**
     * Observer that will be notified when occurs a change in the database.
     */
    public interface DatabaseObserver {
        /**
         * Responds to a data change in the database.
         */
        void onDataChanged();
    }


}
