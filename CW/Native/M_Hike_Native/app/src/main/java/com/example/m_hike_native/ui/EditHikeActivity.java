package com.example.m_hike_native.ui;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.m_hike_native.R;
import com.example.m_hike_native.data.HikeDatabaseHelper;
import com.example.m_hike_native.model.Hike;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Calendar;
import java.util.Locale;

public class EditHikeActivity extends AppCompatActivity {
    int hikeId; HikeDatabaseHelper db;
    // preserve loaded elevation/duration for update
    double loadedElevation = 0.0;
    int loadedDuration = 0;
    com.google.android.material.textfield.TextInputEditText etName, etLocation, etDate, etLength, etDescription;
    com.google.android.material.textfield.TextInputEditText etElevation, etDuration;
    Spinner spinnerDifficulty; SwitchMaterial switchParking; Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_hike);

        hikeId = getIntent().getIntExtra("hike_id", -1);
        db = new HikeDatabaseHelper(this);

        etName = findViewById(R.id.etName);
        etLocation = findViewById(R.id.etLocation);
        etDate = findViewById(R.id.etDate);
        etLength = findViewById(R.id.etLength);
        etDescription = findViewById(R.id.etDescription);
        etElevation = findViewById(R.id.etElevation);
        etDuration = findViewById(R.id.etDuration);
        spinnerDifficulty = findViewById(R.id.spinnerDifficulty);
        switchParking = findViewById(R.id.switchParking);
        btnUpdate = findViewById(R.id.btnUpdate);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(adapter);

        etDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog dpd = new DatePickerDialog(this,
                    (view, year, month, day) -> {
                        if (etDate != null) etDate.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day));
                    },
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        });

        loadHike();

        btnUpdate.setOnClickListener(v -> doUpdate());
    }

    private void loadHike() {
        if (hikeId==-1) return;
        Cursor c = db.getHikeById(hikeId);
        if (c!=null && c.moveToFirst()) {
            if (etName!=null) etName.setText(c.getString(c.getColumnIndexOrThrow("name")));
            if (etLocation!=null) etLocation.setText(c.getString(c.getColumnIndexOrThrow("location")));
            if (etDate!=null) etDate.setText(c.getString(c.getColumnIndexOrThrow("date")));
            if (etLength!=null) etLength.setText(String.valueOf(c.getDouble(c.getColumnIndexOrThrow("length"))));
            // read new fields
            int elevIdx = c.getColumnIndex("elevation");
            if (elevIdx != -1) {
                loadedElevation = c.getDouble(elevIdx);
                if (etElevation != null) etElevation.setText(String.valueOf(loadedElevation));
            }
            int durIdx = c.getColumnIndex("duration_minutes");
            if (durIdx != -1) {
                loadedDuration = c.getInt(durIdx);
                if (etDuration != null) etDuration.setText(String.valueOf(loadedDuration));
            }
            String diff = c.getString(c.getColumnIndexOrThrow("difficulty"));
            if (diff!=null) {
                for (int i=0;i<spinnerDifficulty.getCount();i++) {
                    if (spinnerDifficulty.getItemAtPosition(i).toString().equalsIgnoreCase(diff)) {
                        spinnerDifficulty.setSelection(i); break;
                    }
                }
            }
            if (switchParking!=null) switchParking.setChecked(c.getInt(c.getColumnIndexOrThrow("parking"))==1);
            if (etDescription!=null) etDescription.setText(c.getString(c.getColumnIndexOrThrow("description")));
            c.close();
        }
    }

    private void doUpdate() {
        if (etName==null || etLocation==null || TextUtils.isEmpty(etName.getText()) || TextUtils.isEmpty(etLocation.getText())) {
            Toast.makeText(this, "Name and Location are required", Toast.LENGTH_SHORT).show();
            return;
        }
        Hike h = new Hike();
        h.setId(hikeId);
        h.setName(etName.getText().toString().trim());
        h.setLocation(etLocation.getText().toString().trim());
        h.setDate((etDate!=null && etDate.getText()!=null) ? etDate.getText().toString().trim() : "");
        try { if (etLength!=null && etLength.getText()!=null) h.setLength(Double.parseDouble(etLength.getText().toString().trim())); } catch (Exception ignored) {}
        double elevation = 0;
        try { if (etElevation != null && etElevation.getText() != null) elevation = Double.parseDouble(etElevation.getText().toString().trim()); } catch (Exception ignored) {}
        int duration = 0;
        try { if (etDuration != null && etDuration.getText() != null) duration = Integer.parseInt(etDuration.getText().toString().trim()); } catch (Exception ignored) {}
        h.setDifficulty(spinnerDifficulty.getSelectedItem().toString());
        h.setParking(switchParking!=null && switchParking.isChecked());
        h.setDescription((etDescription!=null && etDescription.getText()!=null) ? etDescription.getText().toString().trim() : "");
        h.setElevation(elevation);
        h.setDurationMinutes(duration);

        int rows = db.updateHike(h);
        if (rows>0) {
            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
            finish();
        } else Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
    }
}
