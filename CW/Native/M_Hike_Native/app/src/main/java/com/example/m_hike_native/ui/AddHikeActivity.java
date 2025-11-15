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
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.m_hike_native.R;
import com.example.m_hike_native.database.HikeDatabaseHelper;
import com.example.m_hike_native.model.Hike;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Calendar;
import java.util.Locale;

public class AddHikeActivity extends AppCompatActivity {
    EditText etName, etLocation, etDate, etLength, etDescription;
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

        // Setup simple top TextView and back icon
        android.widget.TextView tvTitle = findViewById(R.id.topAppBar);
        android.widget.ImageView ivBack = findViewById(R.id.topAppBarIcon);
        if (tvTitle != null) tvTitle.setText(R.string.title_add_hike);
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

        db = new HikeDatabaseHelper(this);

        etName = findViewById(R.id.etName);
        etLocation = findViewById(R.id.etLocation);
        etDate = findViewById(R.id.etDate);
        spinnerDifficulty = findViewById(R.id.spinnerDifficulty);
        etLength = findViewById(R.id.etLength);
        etDescription = findViewById(R.id.etDescription);
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
        String desc = etDescription.getText().toString().trim();
        double length = 0;
        try {
            length = Double.parseDouble(etLength.getText().toString().trim());
        } catch (Exception ignored) {
        }
        boolean parking = switchParking.isChecked();

        Hike hike = new Hike(name, location, date, length, difficulty, parking, desc);
        boolean ok = db.insertHike(hike);
        if (ok) {
            Toast.makeText(this, "Hike saved", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save hike", Toast.LENGTH_SHORT).show();
        }
    }
}
