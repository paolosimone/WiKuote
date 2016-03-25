package com.forfun.paolosimone.wikuote.activity;

import android.support.v7.widget.SearchView;
import android.view.MenuItem;

import com.forfun.paolosimone.wikuote.R;
import com.forfun.paolosimone.wikuote.fragment.DynamicQuoteFragment;
import com.forfun.paolosimone.wikuote.fragment.SearchFragment;
import com.forfun.paolosimone.wikuote.model.Subscription;

import java.util.ArrayList;

/**
 * Created by Paolo Simone on 25/03/2016.
 */
public abstract class WiKuoteNavUtils {


    public static void openQuoteFragment(MainActivity activity, String author){
        Subscription subscription = new Subscription(author,new ArrayList<String>());
        subscription.addAuthor(author);

        DynamicQuoteFragment quoteFragment = DynamicQuoteFragment.newInstance(subscription);
        activity.replaceContent(quoteFragment);
    }

    public static void openSearchFragmentWithQuery(MainActivity activity, String title, String query){
        SearchFragment searchFragment = SearchFragment.newInstance(title, query);
        activity.replaceContent(searchFragment);
    }

}
