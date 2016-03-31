package com.paolosimone.wikuote.activity;

import com.paolosimone.wikuote.fragment.DynamicQuoteFragment;
import com.paolosimone.wikuote.fragment.SearchFragment;
import com.paolosimone.wikuote.model.Category;

import java.util.ArrayList;

/**
 * Created by Paolo Simone on 25/03/2016.
 */
public abstract class WiKuoteNavUtils {


    public static void openQuoteFragment(MainActivity activity, String author){
        Category category = new Category(author,new ArrayList<String>());
        category.addAuthor(author);

        DynamicQuoteFragment quoteFragment = DynamicQuoteFragment.newInstance(category);
        activity.replaceContent(quoteFragment);
    }

    public static void openSearchFragmentWithQuery(MainActivity activity, String title, String query){
        SearchFragment searchFragment = SearchFragment.newInstance(title, query);
        activity.replaceContent(searchFragment);
    }

}
