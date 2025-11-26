package com.example.m_hike_native.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
import com.example.m_hike_native.data.HikeDatabaseHelper;
import com.example.m_hike_native.model.Hike;

import java.util.ArrayList;

import android.util.Log;

public class ListHikeActivity extends AppCompatActivity {
    RecyclerView recyclerView; HikeAdapter adapter; ArrayList<Hike> hikeList; HikeDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge and apply status bar inset as padding to top app bar (or root view)
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_hike);

        recyclerView = findViewById(R.id.recyclerHike);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbHelper = new HikeDatabaseHelper(this);
        hikeList = new ArrayList<>();

        loadData();

        Button btnAddHike = findViewById(R.id.btnAddHike);
        btnAddHike.setOnClickListener(v -> {
            Intent intent = new Intent(ListHikeActivity.this, com.example.m_hike_native.ui.AddHikeActivity.class);
            startActivity(intent);
        });

        Button btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> showSearchDialog());
    }

    private void loadData() {
        if (isFinishing()) return;

        hikeList.clear();
        Cursor cursor = null;
        try {
            cursor = dbHelper.getAllHikes();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Hike h = new Hike();
                    h.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    h.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                    h.setLocation(cursor.getString(cursor.getColumnIndexOrThrow("location")));
                    h.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                    h.setLength(cursor.getDouble(cursor.getColumnIndexOrThrow("length")));
                    // read new fields if present
                    int elevIdx = cursor.getColumnIndex("elevation");
                    if (elevIdx != -1) h.setElevation(cursor.getDouble(elevIdx));
                    int durIdx = cursor.getColumnIndex("duration_minutes");
                    if (durIdx != -1) h.setDurationMinutes(cursor.getInt(durIdx));
                    h.setDifficulty(cursor.getString(cursor.getColumnIndexOrThrow("difficulty")));
                    h.setParking(cursor.getInt(cursor.getColumnIndexOrThrow("parking"))==1);
                    h.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                    hikeList.add(h);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (adapter == null) {
            adapter = new HikeAdapter(hikeList, this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Search Hikes");

        // Set up the input
        final EditText input = new EditText(this);
        input.setHint("Enter hike name or location");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Search", (dialog, which) -> {
            String query = input.getText().toString().trim();
            searchHikes(query);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void searchHikes(String query) {
        if (query.isEmpty()) {
            loadData();
            return;
        }

        if (isFinishing()) return;

        hikeList.clear();
        Cursor cursor = null;
        try {
            cursor = dbHelper.searchHikes(query);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Hike h = new Hike();
                    h.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    h.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                    h.setLocation(cursor.getString(cursor.getColumnIndexOrThrow("location")));
                    h.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                    h.setLength(cursor.getDouble(cursor.getColumnIndexOrThrow("length")));
                    // read new fields if present
                    int elevIdx = cursor.getColumnIndex("elevation");
                    if (elevIdx != -1) h.setElevation(cursor.getDouble(elevIdx));
                    int durIdx = cursor.getColumnIndex("duration_minutes");
                    if (durIdx != -1) h.setDurationMinutes(cursor.getInt(durIdx));
                    h.setDifficulty(cursor.getString(cursor.getColumnIndexOrThrow("difficulty")));
                    h.setParking(cursor.getInt(cursor.getColumnIndexOrThrow("parking"))==1);
                    h.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                    hikeList.add(h);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (adapter == null) {
            adapter = new HikeAdapter(hikeList, this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected void onDestroy() {
        if (adapter != null) {
            adapter.cleanup();
            adapter = null;
        }

        if (recyclerView != null) {
            recyclerView.setAdapter(null);
        }

        if (hikeList != null) {
            hikeList.clear();
        }

        if (dbHelper != null) {
            try {
                dbHelper.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            dbHelper = null;
        }

        super.onDestroy();
    }
}
