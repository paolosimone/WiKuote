package com.paolosimone.wikuote.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.fragment.SimpleTextInputDialogFragment;
import com.paolosimone.wikuote.model.Category;
import com.paolosimone.wikuote.model.Page;
import com.paolosimone.wikuote.model.WiKuoteDatabaseHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Paolo Simone on 21/04/2016.
 */
public class ManageCategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CATEGORY = 0;
    private static final int TYPE_NEW_CATEGORY = 1;

    private Context context;
    private ManageCategoryListener listener;

    private Map<Category, List<Page>> pagesByCategory;
    private Map<Integer,Category> categoriesByIndex;

    private NewCategoryViewHolder newCategoryHolder;

    public ManageCategoriesAdapter(Context context){
        this.context = context;
        this.pagesByCategory = new HashMap<>();
        this.categoriesByIndex = new HashMap<>();
    }

    @Override
    public int getItemViewType(int position){
        return (position < pagesByCategory.size()) ? TYPE_CATEGORY : TYPE_NEW_CATEGORY;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        switch (viewType) {
            case TYPE_CATEGORY:
                view = inflater.inflate(R.layout.item_container_category, parent, false);
                return new CategoryViewHolder(view);
            case TYPE_NEW_CATEGORY:
                view = inflater.inflate(R.layout.item_container_newcategory, parent, false);
                return new NewCategoryViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_NEW_CATEGORY){
            newCategoryHolder = (NewCategoryViewHolder) viewHolder;
            return;
        }

        final Category category = categoriesByIndex.get(position);
        List<Page> pages = category.getPages();

        CategoryViewHolder holder = (CategoryViewHolder) viewHolder;
        holder.tvCategory.setText(category.getTitle());

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCategoryRename(category);
                }
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null){
                    //TODO open "are you sure?" dialog
                    listener.onCategoryDelete(category);
                }
            }
        });

        holder.container.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View target, DragEvent event) {
                final View draggedView = (View) event.getLocalState();

                switch (event.getAction()){
                    case DragEvent.ACTION_DROP:
                        Page page = (Page) draggedView.getTag();
                        if (page.getCategory().equals(category)) {
                            return false;
                        }
                        if (listener!= null) {
                            listener.onDragPage(page, category);
                        }
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        if (!event.getResult()){
                            draggedView.setVisibility(View.VISIBLE);
                        }
                        break;
                }
                return true;
            }
        });

        holder.llPages.removeAllViews();
        for (Page p : pages){
            final TextView tvPage = new TextView(context);
            tvPage.setText(p.getName());
            tvPage.setTag(p);
            tvPage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                        view.startDrag(null, shadowBuilder, view, 0);
                        view.setVisibility(View.INVISIBLE);
                        if (newCategoryHolder!=null) newCategoryHolder.show();
                        return true;
                    }
                    return false;
                }
            });
            holder.llPages.addView(tvPage);
        }
    }

    @Override
    public int getItemCount() {
        return pagesByCategory.size() + 1;
    }

    public void setManageCategoryListener(ManageCategoryListener listener){
        this.listener = listener;
    }

    public void setCategories(Map<Category, List<Page>> pagesByCategory){
        this.pagesByCategory = pagesByCategory;
        categoriesByIndex = new HashMap<>();

        int i = 0;
        for(Category c : pagesByCategory.keySet()){
            categoriesByIndex.put(i++,c);
        }

        notifyDataSetChanged();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        public View container;
        public TextView tvCategory;
        public TextView btnEdit;
        public TextView btnDelete;
        public LinearLayout llPages;

        public CategoryViewHolder(View itemView){
            super(itemView);
            container = itemView;
            tvCategory = (TextView) itemView.findViewById(R.id.item_category_name);
            btnEdit = (TextView) itemView.findViewById(R.id.btn_category_edit);
            btnDelete = (TextView) itemView.findViewById(R.id.btn_category_delete);
            llPages = (LinearLayout) itemView.findViewById(R.id.ll_pages);
        }
    }

    public class NewCategoryViewHolder extends RecyclerView.ViewHolder {

        public View container;
        public TextView tvAddCategory;

        public NewCategoryViewHolder(View itemView){
            super(itemView);
            container = itemView;
            tvAddCategory = (TextView) itemView.findViewById(R.id.add_new_category);
            setupOnDragListener();
        }

        public void hide() {
            tvAddCategory.setVisibility(View.INVISIBLE);
        }

        public void show(){
            tvAddCategory.setVisibility(View.VISIBLE);
        }

        private void setupOnDragListener() {
            container.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    final View draggedView = (View) event.getLocalState();

                    switch (event.getAction()){
                        case DragEvent.ACTION_DROP:
                            Page page = (Page) draggedView.getTag();
                            if (listener!= null) {
                                listener.onDragPage(page, null);
                            }
                            break;
                        case DragEvent.ACTION_DRAG_ENDED:
                            draggedView.setVisibility(View.VISIBLE);
                            hide();
                            break;
                    }
                    return true;
                }
            });
        }
    }

    public interface ManageCategoryListener {

        void onDragPage(Page page, Category toCategory);

        void onCategoryRename(Category category);

        void onCategoryDelete(Category category);
    }
}
