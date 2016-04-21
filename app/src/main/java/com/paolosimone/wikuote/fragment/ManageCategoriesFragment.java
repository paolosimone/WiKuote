package com.paolosimone.wikuote.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.adapter.ManageCategoriesAdapter;
import com.paolosimone.wikuote.model.Category;
import com.paolosimone.wikuote.model.Page;
import com.paolosimone.wikuote.model.WiKuoteDatabaseHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Paolo Simone on 21/04/2016.
 */
public class ManageCategoriesFragment extends Fragment implements Titled, ManageCategoriesAdapter.ManageCategoryListener {

    private WiKuoteDatabaseHelper db = WiKuoteDatabaseHelper.getInstance();

    private RecyclerView recyclerView;
    private ManageCategoriesAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_category, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_categories);
        adapter = new ManageCategoriesAdapter(getActivity());
        adapter.setManageCategoryListener(this);
        recyclerView.setAdapter(adapter);
        //TODO select #columns based on screen size
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));

        updateViewContent();

        return view;
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.tab_manage_categories);
    }

    @Override
    public void onDraggedPage(Page page, Category toCategory) {
        db.movePageToCategory(page,toCategory);
        updateViewContent();
    }

    @Override
    public void onCategoryRenamed(String newName) {
        // TODO
    }

    private void updateViewContent(){
        List<Category> categories = db.getAllCategories();
        if (categories.isEmpty()){
            //TODO error message
            adapter.setCategories(new HashMap<Category, List<Page>>());
            return;
        }

        Map<Category, List<Page>> pagesByCategory = new HashMap<>();
        for(Category c : categories){
            pagesByCategory.put(c,c.getPages());
        }

        adapter.setCategories(pagesByCategory);
    }
}
