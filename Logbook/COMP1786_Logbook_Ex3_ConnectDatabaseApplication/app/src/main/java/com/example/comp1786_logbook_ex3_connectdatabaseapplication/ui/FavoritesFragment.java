package com.example.comp1786_logbook_ex3_connectdatabaseapplication.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp1786_logbook_ex3_connectdatabaseapplication.R;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.activity.ContactsAdapter;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.activity.DetailContactActivity;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.helper.Constants;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.model.AppDatabase;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.model.Contact;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.model.ContactDao;

import java.util.List;
import java.util.concurrent.Executors;

public class FavoritesFragment extends Fragment {

    private RecyclerView rv;
    private ContactsAdapter adapter;
    private ContactDao dao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favorites, container, false);
        dao = AppDatabase.getInstance(requireContext()).contactDao();

        rv = v.findViewById(R.id.rvFavorites);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ContactsAdapter(this::onItemClick, this::onItemDelete, this::onFavoriteToggle);
        rv.setAdapter(adapter);

        Button btnBrowse = v.findViewById(R.id.btnBrowseContactsFav);
        if (btnBrowse != null) btnBrowse.setOnClickListener(item -> {
            // switch to contacts tab in host activity
            if (getActivity() instanceof com.example.comp1786_logbook_ex3_connectdatabaseapplication.ui.MainNavActivity) {
                com.google.android.material.bottomnavigation.BottomNavigationView bn =
                        getActivity().findViewById(R.id.bottom_navigation);
                if (bn != null) bn.setSelectedItemId(R.id.nav_contacts);
            }
        });

        loadFavorites();
        return v;
    }

    private void loadFavorites() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Contact> favs = dao.getFavorites();
            if (getActivity() == null) return;
            getActivity().runOnUiThread(() -> {
                adapter.setItems(favs);
                View empty = getView() != null ? getView().findViewById(R.id.empty_container_favorites) : null;
                if (empty != null) {
                    empty.setVisibility((favs == null || favs.isEmpty()) ? View.VISIBLE : View.GONE);
                }
            });
        });
    }

    private void onItemClick(Contact c) {
        Intent i = new Intent(requireContext(), DetailContactActivity.class);
        i.putExtra(DetailContactActivity.EXTRA_ID, c.id);
        startActivity(i);
    }

    private void onItemDelete(Contact c) {
        Executors.newSingleThreadExecutor().execute(() -> {
            dao.delete(c);
            if (getActivity() == null) return;
            getActivity().runOnUiThread(() -> {
                Intent intent = new Intent(Constants.ACTION_CONTACTS_CHANGED);
                requireActivity().sendBroadcast(intent);
                Toast.makeText(requireContext(), "Deleted: " + c.name, Toast.LENGTH_SHORT).show();
                loadFavorites();
            });
        });
    }

    private void onFavoriteToggle(Contact c) {
        Executors.newSingleThreadExecutor().execute(() -> {
            boolean newFav = !c.isFavorite;
            long ts = System.currentTimeMillis();
            if (c.id > 0) {
                dao.updateFavorite(c.id, newFav ? 1 : 0, ts);
            } else {
                c.isFavorite = newFav;
                c.updatedAt = ts;
                dao.upsert(c);
            }
            if (getActivity() == null) return;
            getActivity().runOnUiThread(() -> {
                Intent intent = new Intent(Constants.ACTION_CONTACTS_CHANGED);
                requireActivity().sendBroadcast(intent);
                Toast.makeText(requireContext(), c.name + (newFav ? " added to favorites" : " removed from favorites"), Toast.LENGTH_SHORT).show();
                loadFavorites();
            });
        });
    }
}
