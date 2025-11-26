package com.example.m_hike_native.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.util.Log;

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

public class AddHikeActivity extends AppCompatActivity {
    EditText etName, etLocation, etDate, etLength, etDescription;
    EditText etElevation, etDuration;
    Spinner spinnerDifficulty;
    SwitchMaterial switchParking;
    Button btnSave;
    HikeDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge and apply status bar inset padding
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_hike);

        db = new HikeDatabaseHelper(this);

        etName = findViewById(R.id.etName);
        etLocation = findViewById(R.id.etLocation);
        etDate = findViewById(R.id.etDate);
        spinnerDifficulty = findViewById(R.id.spinnerDifficulty);
        etLength = findViewById(R.id.etLength);
        etDescription = findViewById(R.id.etDescription);
        etElevation = findViewById(R.id.etElevation);
        etDuration = findViewById(R.id.etDuration);
        switchParking = findViewById(R.id.switchParking);
        btnSave = findViewById(R.id.btnSave);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(adapter);

        etDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog dpd = new DatePickerDialog(this,
                    (view, year, month, day) -> etDate.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day)),
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        });

        btnSave.setOnClickListener(v -> validateInput());
    }

    private void validateInput() {
        if (TextUtils.isEmpty(etName.getText()) || TextUtils.isEmpty(etLocation.getText())) {
            Toast.makeText(this, "Name and Location are required", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = etName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String difficulty = spinnerDifficulty.getSelectedItem().toString();
        String description = etDescription.getText().toString().trim();
        double length = 0;
        try {
            length = Double.parseDouble(etLength.getText().toString().trim());
        } catch (Exception ignored) {
        }
        double elevation = 0;
        try {
            elevation = Double.parseDouble(etElevation.getText().toString().trim());
        } catch (Exception ignored) {
        }
        int durationMinutes = 0;
        try {
            durationMinutes = Integer.parseInt(etDuration.getText().toString().trim());
        } catch (Exception ignored) {
        }
        boolean parking = switchParking.isChecked();

        Hike hike = new Hike(name, location, date, length, difficulty, parking, description, elevation, durationMinutes);
        boolean ok = db.insertHike(hike);
        if (ok) {
            Toast.makeText(this, "Hike saved", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save hike", Toast.LENGTH_SHORT).show();
        }
    }
}
