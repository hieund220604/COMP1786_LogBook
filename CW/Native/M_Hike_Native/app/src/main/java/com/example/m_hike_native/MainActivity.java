package com.example.m_hike_native;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.m_hike_native.util.DatabaseExportUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Setup simple title TextView (replace toolbar)
        android.widget.TextView tvTitle = findViewById(R.id.topAppBar);
        android.widget.ImageView ivBack = findViewById(R.id.topAppBarIcon);
        if (tvTitle != null) {
            tvTitle.setText(R.string.title_main);
        }
        if (ivBack != null) {
            // MainActivity is root; no back action. Hide icon if it was a back arrow.
            ivBack.setVisibility(android.view.View.GONE);
        }

        View topContainer = findViewById(R.id.topAppBarContainer);
        if (topContainer == null) topContainer = findViewById(R.id.topAppBar);
        if (topContainer != null) {
            ViewCompat.setOnApplyWindowInsetsListener(topContainer, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                return insets;
            });
        }

        // Card views
        View cardAdd = findViewById(R.id.cardAdd);
        View cardList = findViewById(R.id.cardList);
        View cardSearch = findViewById(R.id.cardSearch);
        View cardExport = findViewById(R.id.cardExport);

        // ensure intents point to the new MVVM view package
        cardAdd.setOnClickListener(v -> startActivity(new Intent(this, com.example.m_hike_native.ui.AddHikeActivity.class)));
        cardList.setOnClickListener(v -> startActivity(new Intent(this, com.example.m_hike_native.ui.ListHikeActivity.class)));
        cardSearch.setOnClickListener(v -> startActivity(new Intent(this, com.example.m_hike_native.ui.SearchActivity.class)));
        cardExport.setOnClickListener(v -> {
            try {
                File exported = DatabaseExportUtils.exportDatabase(this, "mhike.db");
                Log.d("MainActivity", "Database exported to: " + exported.getAbsolutePath());
                Toast.makeText(this, "DB exported: " + exported.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Add press scale animation for touch feedback
        View.OnTouchListener pressAnim = (v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.98f).scaleY(0.98f).setDuration(120).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    return true;
                case MotionEvent.ACTION_UP:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(120).setInterpolator(new AccelerateDecelerateInterpolator()).withEndAction(() -> v.performClick()).start();
                    return true;
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(120).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    return true;
            }
            return false;
        };

        cardAdd.setOnTouchListener(pressAnim);
        cardList.setOnTouchListener(pressAnim);
        cardSearch.setOnTouchListener(pressAnim);
        cardExport.setOnTouchListener(pressAnim);
    }
}