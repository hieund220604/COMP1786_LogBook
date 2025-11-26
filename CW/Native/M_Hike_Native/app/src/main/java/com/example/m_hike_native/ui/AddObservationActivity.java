package com.example.m_hike_native.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_hike_native.R;
import com.example.m_hike_native.adapter.ObservationAdapter;
import com.example.m_hike_native.data.HikeDatabaseHelper;
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

        // set listener to handle edit/delete
        adapter.setListener(new ObservationAdapter.ObservationListener() {
            @Override
            public void onEdit(Observation obs, int position) {
                showEditDialog(obs, position);
            }

            @Override
            public void onDelete(Observation obs, int position) {
                confirmDelete(obs, position);
            }
        });

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

    private void showEditDialog(Observation obs, int position) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_edit_observation, null);
        com.google.android.material.textfield.TextInputEditText etEditObs = v.findViewById(R.id.etEditObservation);
        com.google.android.material.textfield.TextInputEditText etEditComments = v.findViewById(R.id.etEditComments);
        etEditObs.setText(obs.getObservation());
        etEditComments.setText(obs.getComments());
        b.setView(v);
        b.setTitle("Edit Observation");
        b.setPositiveButton("Save", (dialog, which) -> {
            String newObs = etEditObs.getText() != null ? etEditObs.getText().toString().trim() : "";
            String newComments = etEditComments.getText() != null ? etEditComments.getText().toString().trim() : "";
            if (newObs.isEmpty()) {
                Toast.makeText(this, "Observation cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            obs.setObservation(newObs);
            obs.setComments(newComments);
            obs.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date()));
            int updated = db.updateObservation(obs);
            if (updated > 0) {
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                loadObservations();
            } else Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        });
        b.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        b.show();
    }

    private void confirmDelete(Observation obs, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Observation")
                .setMessage("Are you sure you want to delete this observation?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    int del = db.deleteObservation(obs.getId());
                    if (del > 0) {
                        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                        loadObservations();
                    } else Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (d, w) -> d.dismiss())
                .show();
    }
}
