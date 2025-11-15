package com.example.m_hike_native.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

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

public class ListHikeActivity extends AppCompatActivity {
    RecyclerView recyclerView; HikeAdapter adapter; ArrayList<Hike> hikeList; HikeDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge and apply status bar inset as padding to top app bar (or root view)
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_hike);

        // Setup simple top TextView and back icon
        android.widget.TextView tvTitle = findViewById(R.id.topAppBar);
        android.widget.ImageView ivBack = findViewById(R.id.topAppBarIcon);
        if (tvTitle != null) tvTitle.setText(R.string.title_list_hikes);
        if (ivBack != null) ivBack.setOnClickListener(v -> finish());

        // Apply top inset padding to toolbar if present, otherwise to root content view
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

        recyclerView = findViewById(R.id.recyclerHike);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbHelper = new HikeDatabaseHelper(this);
        hikeList = new ArrayList<>();

        loadData();
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
