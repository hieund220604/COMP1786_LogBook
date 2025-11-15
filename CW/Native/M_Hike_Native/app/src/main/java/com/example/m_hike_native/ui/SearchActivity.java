package com.example.m_hike_native.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_hike_native.R;
import com.example.m_hike_native.adapter.HikeAdapter;
import com.example.m_hike_native.database.HikeDatabaseHelper;
import com.example.m_hike_native.model.Hike;

import java.util.ArrayList;

import android.util.Log;

public class SearchActivity extends AppCompatActivity {
    androidx.appcompat.widget.SearchView searchView;
    RecyclerView recyclerSearch; HikeDatabaseHelper db; ArrayList<Hike> list; HikeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge handling
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        // Setup simple top TextView and back icon
        android.widget.TextView tvTitle = findViewById(R.id.topAppBar);
        android.widget.ImageView ivBack = findViewById(R.id.topAppBarIcon);
        if (tvTitle != null) tvTitle.setText(R.string.title_search);
        if (ivBack != null) ivBack.setOnClickListener(v -> finish());

        // Apply top inset padding to toolbar or root
        View target = findViewById(R.id.topAppBarContainer);
        if (target == null) {
            ViewGroup content = findViewById(android.R.id.content);
            if (content != null && content.getChildCount() > 0) target = content.getChildAt(0);
        }
        if (target != null) {
            ViewCompat.setOnApplyWindowInsetsListener(target, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
                return insets;
            });
        }

        searchView = findViewById(R.id.searchView);
        recyclerSearch = findViewById(R.id.recyclerSearch);
        recyclerSearch.setLayoutManager(new LinearLayoutManager(this));
        db = new HikeDatabaseHelper(this);
        list = new ArrayList<>();
        adapter = new HikeAdapter(list, this);
        recyclerSearch.setAdapter(adapter);

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                doSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                doSearch(newText);
                return true;
            }
        });
    }

    private void doSearch(String q) {
        if (isFinishing()) return;

        list.clear();
        if (q==null || q.trim().isEmpty()) {
            if (adapter != null) adapter.notifyDataSetChanged();
            return;
        }

        Cursor c = null;
        try {
            c = db.searchHikes(q);
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

                    int difficultyIdx = c.getColumnIndex("difficulty");
                    if (difficultyIdx != -1) h.setDifficulty(c.getString(difficultyIdx));

                    int parkingIdx = c.getColumnIndex("parking");
                    if (parkingIdx != -1) h.setParking(c.getInt(parkingIdx) == 1);

                    list.add(h);
                } while (c.moveToNext());

                if (adapter != null) adapter.notifyDataSetChanged();
            } else {
                if (adapter != null) adapter.notifyDataSetChanged();
                if (!isFinishing()) {
                    Toast.makeText(this, "No results", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (searchView != null) {
            searchView.clearFocus();
        }
    }

    @Override
    protected void onDestroy() {
        if (searchView != null) {
            searchView.setOnQueryTextListener(null);
        }

        if (recyclerSearch != null) {
            recyclerSearch.setAdapter(null);
        }

        if (adapter != null) {
            adapter.cleanup();
            adapter = null;
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
