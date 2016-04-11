package com.paolosimone.wikuote.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.activity.FavoritesActivity;
import com.paolosimone.wikuote.adapter.FavoritesListAdapter;
import com.paolosimone.wikuote.model.Quote;
import com.paolosimone.wikuote.model.WiKuoteDatabaseHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Paolo Simone on 11/04/2016.
 */
public class FavoritesListFragment extends Fragment implements WiKuoteDatabaseHelper.DatabaseObserver{

    private WiKuoteDatabaseHelper db;

    private FavoritesListAdapter favoritesListAdapter;
    private ListView listView;

    private Set<Quote> selected;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        db = WiKuoteDatabaseHelper.getInstance();
        db.attach(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites_list,container,false);

        listView = (ListView) view.findViewById(R.id.list_favorites);

        updateQuotes();
        setupActionMode();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((FavoritesActivity) getActivity()).onFavoriteClick(position);
            }
        });

        return view;
    }

    @Override
    public void onDestroy(){
        db.detach(this);
        super.onDestroy();
    }

    @Override
    public void onDataChanged() {
        updateQuotes();
    }

    private void updateQuotes(){
        List<Quote> favorites = db.getAllQuotes();
        favoritesListAdapter = new FavoritesListAdapter(getActivity());
        favoritesListAdapter.addAll(favorites);
        listView.setAdapter(favoritesListAdapter);
    }

    private void deleteItems(){
        WiKuoteDatabaseHelper db = WiKuoteDatabaseHelper.getInstance();
        for (Quote q : selected){
            db.deleteFavorite(q);
        }
    }

    private void setupActionMode(){
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (checked){
                    selected.add(favoritesListAdapter.getItem(position));
                }
                else {
                    selected.remove(favoritesListAdapter.getItem(position));
                }
                int descId =  selected.size()>1 ? R.string.desc_selected_plural : R.string.desc_selected_singular;
                mode.setSubtitle(selected.size() + " " +getString(descId));
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_favorites_action_mode,menu);
                mode.setTitle(R.string.action_delete);
                selected = new HashSet<>();
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        deleteItems();
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                selected = null;
            }
        });
    }
}
