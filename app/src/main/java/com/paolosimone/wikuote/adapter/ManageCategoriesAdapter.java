package com.paolosimone.wikuote.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.model.Category;
import com.paolosimone.wikuote.model.Page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter that holds the pages associated to each category and provides them to the RecyclerView in ManageCategoriesFragment.
 * It also setup the view in order to respond to user interaction, such as drag and drop.
 */
public class ManageCategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CATEGORY = 0;
    private static final int TYPE_NEW_CATEGORY = 1;

    private Context context;
    private ManageCategoryListener listener;

    private Map<Category, List<Page>> pagesByCategory;
    private Map<Integer,Category> categoriesByIndex;

    private NewCategoryViewHolder newCategoryHolder;

    /**
     * Creates a new empty adapter.
     * @param context the context in which the adapter is run
     */
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

        final View container = holder.container;
        container.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View target, DragEvent event) {
                final View draggedView = (View) event.getLocalState();

                switch (event.getAction()){
                    case DragEvent.ACTION_DRAG_ENTERED:
                        container.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        container.setBackgroundResource(R.drawable.border_container);
                        break;
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
                        container.setBackgroundResource(R.drawable.border_container);
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

    /**
     * Set the listener that handles the events generated by the user interaction.
     * @param listener the listener
     */
    public void setManageCategoryListener(ManageCategoryListener listener){
        this.listener = listener;
    }

    /**
     * Set the list of pages for each category.
     * @param pagesByCategory pages associated to each category
     */
    public void setCategories(Map<Category, List<Page>> pagesByCategory){
        this.pagesByCategory = pagesByCategory;
        categoriesByIndex = new HashMap<>();

        int i = 0;
        for(Category c : pagesByCategory.keySet()){
            categoriesByIndex.put(i++,c);
        }

        notifyDataSetChanged();
    }

    /**
     * View holder that keeps a reference to the views inside an item representing a category and his pages.
     */
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

    /**
     * View holder that keeps a reference to the views inside the special item representing a new category.
     */
    public class NewCategoryViewHolder extends RecyclerView.ViewHolder {

        public View container;

        public NewCategoryViewHolder(View itemView){
            super(itemView);
            container = itemView;
            setupOnDragListener();
        }

        /**
         * Hide the view item.
         */
        public void hide() {
            container.setVisibility(View.INVISIBLE);
        }

        /**
         * Show the view item.
         */
        public void show(){
            container.setVisibility(View.VISIBLE);
        }

        private void setupOnDragListener() {
            container.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    final View draggedView = (View) event.getLocalState();

                    switch (event.getAction()){
                        case DragEvent.ACTION_DRAG_ENTERED:
                            container.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));
                            break;
                        case DragEvent.ACTION_DRAG_EXITED:
                            container.setBackgroundResource(R.drawable.border_container);
                            break;
                        case DragEvent.ACTION_DROP:
                            Page page = (Page) draggedView.getTag();
                            if (listener!= null) {
                                listener.onDragPage(page, null);
                            }
                            break;
                        case DragEvent.ACTION_DRAG_ENDED:
                            draggedView.setVisibility(View.VISIBLE);
                            container.setBackgroundResource(R.drawable.border_container);
                            hide();
                            break;
                    }
                    return true;
                }
            });
        }
    }

    /**
     * Listener that handles the events generated by the user interaction in category management.
     */
    public interface ManageCategoryListener {

        /**
         * Handles the event when the user drag a page to a different category.
         * @param page the page that has been dragged
         * @param toCategory the category where the page has been dropped
         */
        void onDragPage(Page page, Category toCategory);

        /**
         * Handles the event when the user click on the rename button of a category.
         * @param category the category that the user wants to rename
         */
        void onCategoryRename(Category category);

        /**
         * Handles the event when the user click the delete button of a category.
         * @param category the category that the user wants to delete
         */
        void onCategoryDelete(Category category);
    }
}
