package com.example.m_hike_native.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_hike_native.R;
import com.example.m_hike_native.adapter.HikeAdapter;
import com.example.m_hike_native.data.HikeDatabaseHelper;
import com.example.m_hike_native.model.Hike;
import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;

import android.util.Log;

public class SearchActivity extends AppCompatActivity {
    androidx.appcompat.widget.SearchView searchView;
    RecyclerView recycler;
    HikeDatabaseHelper db;
    ArrayList<Hike> list;
    HikeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge handling
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        db = new HikeDatabaseHelper(this);
        list = new ArrayList<>();

        searchView = findViewById(R.id.searchView);
        searchView.setIconified(false); // Expand the SearchView by default
        recycler = findViewById(R.id.recyclerSearch);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HikeAdapter(list, this);
        recycler.setAdapter(adapter);

        loadAllHikes();

        // Wire SearchView to perform searches
        if (searchView != null) {
            searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    doSearch(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // Live search as user types
                    doSearch(newText);
                    return true;
                }
            });
        }
    }

    private void loadAllHikes() {
        list.clear();
        Cursor c = db.getAllHikes();
        if (c != null && c.moveToFirst()) {
            do {
                Hike h = new Hike();
                h.setId(c.getInt(c.getColumnIndexOrThrow("id")));
                h.setName(c.getString(c.getColumnIndexOrThrow("name")));
                h.setLocation(c.getString(c.getColumnIndexOrThrow("location")));

                int dateIdx = c.getColumnIndex("date");
                if (dateIdx != -1) h.setDate(c.getString(dateIdx));

                int lengthIdx = c.getColumnIndex("length");
                if (lengthIdx != -1) h.setLength(c.getDouble(lengthIdx));

                int elevIdx = c.getColumnIndex("elevation");
                if (elevIdx != -1) h.setElevation(c.getDouble(elevIdx));
                int durIdx = c.getColumnIndex("duration_minutes");
                if (durIdx != -1) h.setDurationMinutes(c.getInt(durIdx));

                int difficultyIdx = c.getColumnIndex("difficulty");
                if (difficultyIdx != -1) h.setDifficulty(c.getString(difficultyIdx));

                int parkingIdx = c.getColumnIndex("parking");
                if (parkingIdx != -1) h.setParking(c.getInt(parkingIdx) == 1);

                list.add(h);
            } while (c.moveToNext());

            c.close();
        }
        adapter.notifyDataSetChanged();
    }

    private void doSearch(String q) {
        if (q == null || q.trim().isEmpty()) {
            loadAllHikes();
            return;
        }
        list.clear();
        Cursor c = db.searchHikes(q);
        if (c != null && c.moveToFirst()) {
            do {
                Hike h = new Hike();
                h.setId(c.getInt(c.getColumnIndexOrThrow("id")));
                h.setName(c.getString(c.getColumnIndexOrThrow("name")));
                h.setLocation(c.getString(c.getColumnIndexOrThrow("location")));

                int dateIdx = c.getColumnIndex("date");
                if (dateIdx != -1) h.setDate(c.getString(dateIdx));

                int lengthIdx = c.getColumnIndex("length");
                if (lengthIdx != -1) h.setLength(c.getDouble(lengthIdx));

                int elevIdx = c.getColumnIndex("elevation");
                if (elevIdx != -1) h.setElevation(c.getDouble(elevIdx));
                int durIdx = c.getColumnIndex("duration_minutes");
                if (durIdx != -1) h.setDurationMinutes(c.getInt(durIdx));

                int difficultyIdx = c.getColumnIndex("difficulty");
                if (difficultyIdx != -1) h.setDifficulty(c.getString(difficultyIdx));

                int parkingIdx = c.getColumnIndex("parking");
                if (parkingIdx != -1) h.setParking(c.getInt(parkingIdx) == 1);

                list.add(h);
            } while (c.moveToNext());

            c.close();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        if (adapter != null) {
            adapter.cleanup();
            adapter = null;
        }

        if (recycler != null) {
            recycler.setAdapter(null);
        }

        if (list != null) {
            list.clear();
        }

        if (db != null) {
            try {
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            db = null;
        }

        super.onDestroy();
    }
}
