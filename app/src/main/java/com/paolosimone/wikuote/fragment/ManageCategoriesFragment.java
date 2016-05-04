package com.paolosimone.wikuote.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.adapter.ManageCategoriesAdapter;
import com.paolosimone.wikuote.fragment.dialog.SimpleTextInputDialogFragment;
import com.paolosimone.wikuote.model.Category;
import com.paolosimone.wikuote.model.Page;
import com.paolosimone.wikuote.model.WiKuoteDatabaseHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Allow the user to manage the saved categories and pages.
 * The user can delete or rename each category, and can also move a page to another category or to a new one.
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
    public void onDragPage(Page page, Category toCategory) {
        if (toCategory==null){
            openAddToNewCategoryDialog(page);
        }
        else {
            db.movePageToCategory(page, toCategory);
            updateViewContent();
        }
    }

    @Override
    public void onCategoryRename(Category category) {
        openRenameCategoryDialog(category);
    }

    @Override
    public void onCategoryDelete(Category category) {
        db.deleteCategory(category);
        updateViewContent();
    }

    private void openRenameCategoryDialog(final Category category){
        String dialogTitle = getString(R.string.msg_rename_request);
        String positive = getString(R.string.msg_rename);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        SimpleTextInputDialogFragment dialog = SimpleTextInputDialogFragment.newInstance(dialogTitle,positive);
        dialog.setOnInputSubmitListener(new SimpleTextInputDialogFragment.OnInputSubmitListener() {
            @Override
            public void onInputSubmit(String newTitle) {
                WiKuoteDatabaseHelper db = WiKuoteDatabaseHelper.getInstance();
                if (!db.isValidCategoryTitle(newTitle) || db.getCategoryFromTitle(newTitle)!=null){
                    Toast.makeText(getActivity(),R.string.err_invalid_input,Toast.LENGTH_SHORT).show();
                    return;
                }
                db.renameCategory(category, newTitle);
                updateViewContent();
            }
        });
        dialog.show(fm, "fragment_rename_category");
    }

    private void openAddToNewCategoryDialog(final Page page) {
        String dialogTitle = getString(R.string.msg_new_category);
        String positive = getString(R.string.msg_add);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        SimpleTextInputDialogFragment dialog = SimpleTextInputDialogFragment.newInstance(dialogTitle,positive);
        dialog.setOnInputSubmitListener(new SimpleTextInputDialogFragment.OnInputSubmitListener() {
            @Override
            public void onInputSubmit(String newTitle) {
                WiKuoteDatabaseHelper db = WiKuoteDatabaseHelper.getInstance();
                if (!db.isValidCategoryTitle(newTitle) || db.getCategoryFromTitle(newTitle)!=null){
                    Toast.makeText(getActivity(),R.string.err_invalid_input,Toast.LENGTH_SHORT).show();
                    return;
                }
                Category category = new Category(newTitle);
                db.movePageToCategory(page, category);
                updateViewContent();
            }
        });
        dialog.show(fm, "fragment_new_category");
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
