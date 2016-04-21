package com.paolosimone.wikuote.adapter;

import android.content.Context;
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
 * Created by Paolo Simone on 21/04/2016.
 */
public class ManageCategoriesAdapter extends RecyclerView.Adapter<ManageCategoriesAdapter.ViewHolder> {

    private Context context;
    private ManageCategoryListener listener;

    private Map<Category, List<Page>> pagesByCategory;
    private Map<Integer,Category> categoriesByIndex;

    public ManageCategoriesAdapter(Context context){
        this.context = context;
        this.pagesByCategory = new HashMap<>();
        this.categoriesByIndex = new HashMap<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_container_category,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Category category = categoriesByIndex.get(position);
        List<Page> pages = category.getPages();

        holder.tvCategory.setText(category.getTitle());

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
                            listener.onDraggedPage(page, category);
                        }
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        if (!event.getResult()){
                            draggedView.setVisibility(View.VISIBLE);
                        }
                        break;
                    default:
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
        return pagesByCategory.size();
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

    public class ViewHolder extends RecyclerView.ViewHolder{

        public View container;
        public TextView tvCategory;
        public LinearLayout llPages;

        public ViewHolder(View itemView){
            super(itemView);
            container = itemView;
            tvCategory = (TextView) itemView.findViewById(R.id.item_category_name);
            llPages = (LinearLayout) itemView.findViewById(R.id.ll_pages);
        }
    }

    public interface ManageCategoryListener {

        void onDraggedPage(Page page, Category toCategory);

        void onCategoryRenamed(String newName);
    }
}
