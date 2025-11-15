package com.example.m_hike_native.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_hike_native.R;
import com.example.m_hike_native.adapter.ObservationAdapter;
import com.example.m_hike_native.database.HikeDatabaseHelper;
import com.example.m_hike_native.model.Observation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AddObservationActivity extends AppCompatActivity {
    int hikeId;
    String hikeName;
    TextView tvHikeTitle;
    com.google.android.material.textfield.TextInputEditText etObservation, etComments;
    Button btnSave;
    RecyclerView recyclerObs;
    HikeDatabaseHelper db;
    ObservationAdapter adapter;
    ArrayList<Observation> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_observation);

        // Setup simple top TextView and back icon
        android.widget.TextView tvTitle = findViewById(R.id.topAppBar);
        android.widget.ImageView ivBack = findViewById(R.id.topAppBarIcon);
        if (tvTitle != null) tvTitle.setText(R.string.add_observation);
        if (ivBack != null) ivBack.setOnClickListener(v -> finish());

        // Apply top inset padding to root if there's no toolbar in this layout
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

        hikeId = getIntent().getIntExtra("hike_id", -1);
        hikeName = getIntent().getStringExtra("hike_name");

        db = new HikeDatabaseHelper(this);

        tvHikeTitle = findViewById(R.id.tvHikeTitle);
        tvHikeTitle.setText(hikeName != null ? hikeName : "");
        etObservation = findViewById(R.id.etObservation);
        etComments = findViewById(R.id.etComments);
        btnSave = findViewById(R.id.btnSaveObs);
        recyclerObs = findViewById(R.id.recyclerObs);
        recyclerObs.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ObservationAdapter(list, this);
        recyclerObs.setAdapter(adapter);

        loadObservations();

        btnSave.setOnClickListener(v -> saveObservation());
    }

    private void loadObservations() {
        list.clear();
        android.database.Cursor c = db.getObservationsForHike(hikeId);
        if (c != null && c.moveToFirst()) {
            do {
                Observation o = new Observation();
                o.setId(c.getInt(c.getColumnIndexOrThrow("id")));
                o.setHikeId(c.getInt(c.getColumnIndexOrThrow("hike_id")));
                o.setObservation(c.getString(c.getColumnIndexOrThrow("observation")));
                o.setTime(c.getString(c.getColumnIndexOrThrow("time")));
                o.setComments(c.getString(c.getColumnIndexOrThrow("comments")));
                list.add(o);
            } while (c.moveToNext());
            c.close();
        }
        adapter.notifyDataSetChanged();
    }

    private void saveObservation() {
        if (TextUtils.isEmpty(etObservation.getText())) {
            Toast.makeText(this, "Observation is required", Toast.LENGTH_SHORT).show();
            return;
        }
        String obsText = etObservation.getText().toString().trim();
        CharSequence cs = (etComments != null) ? etComments.getText() : null;
        String comments = (cs != null) ? cs.toString().trim() : "";
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        Observation o = new Observation(hikeId, obsText, time, comments);
        boolean ok = db.insertObservation(o);
        if (ok) {
            Toast.makeText(this, "Observation saved", Toast.LENGTH_SHORT).show();
            etObservation.setText("");
            if (etComments != null) etComments.setText("");
            loadObservations();
        } else Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
    }
}
