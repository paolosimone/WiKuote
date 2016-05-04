package com.paolosimone.wikuote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.activity.MainActivity;
import com.paolosimone.wikuote.activity.WiKuoteNavUtils;
import com.paolosimone.wikuote.model.Category;
import com.paolosimone.wikuote.model.Page;

import java.util.List;
import java.util.Map;

/**
 * Adapter that holds a list of categories, and a list of pages for each category.
 * It provides them to the Drawer in the MainActivity.
 */
public class CategoriesDrawerAdapter extends BaseExpandableListAdapter {

    private static final int MAX_PAGES = 1000;

    private MainActivity activity; //TODO refactor this in context
    private List<Category> categories;
    private Map<Category, List<Page>> pagesByCategory;

    /**
     * Creates a new adapter.
     * @param context the context in which the adapter is run
     * @param categories the categories to be shown
     * @param pagesByCategory the pages in each category
     */
    public CategoriesDrawerAdapter(MainActivity context, List<Category> categories,
                                   Map<Category, List<Page>> pagesByCategory) {
        this.activity = context;
        this.categories = categories;
        this.pagesByCategory = pagesByCategory;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return categories.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return categories.size();
    }

    @Override
    public View getGroupView(int groupPosition, final boolean isExpanded, View convertView, ViewGroup parent) {
        final Category category = categories.get(groupPosition);

        if (convertView==null){
            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_drawer_category, parent, false);
        }

        TextView titleText = (TextView) convertView.findViewById(R.id.item_category_name);
        titleText.setText(category.getTitle());
        titleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WiKuoteNavUtils.openQuoteFragmentCategory(activity, category);
            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return pagesByCategory.get(categories.get(groupPosition)).size();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return pagesByCategory.get(categories.get(groupPosition)).get(childPosition);
    }


    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return (long) MAX_PAGES*groupPosition + childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Page page = pagesByCategory.get(categories.get(groupPosition)).get(childPosition);

        if (convertView==null){
            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_drawer_page, parent, false);
        }

        TextView titleText = (TextView) convertView.findViewById(R.id.item_page_name);
        titleText.setText(page.getName());
        titleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WiKuoteNavUtils.openQuoteFragmentSinglePage(activity, page);
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
