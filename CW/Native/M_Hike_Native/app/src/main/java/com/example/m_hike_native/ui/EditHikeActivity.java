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

public class EditHikeActivity extends AppCompatActivity {
    int hikeId; HikeDatabaseHelper db;
    com.google.android.material.textfield.TextInputEditText etName, etLocation, etDate, etLength, etDescription;
    Spinner spinnerDifficulty; SwitchMaterial switchParking; Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_hike);

        // Setup simple top TextView and back icon
        android.widget.TextView tvTitle = findViewById(R.id.topAppBar);
        android.widget.ImageView ivBack = findViewById(R.id.topAppBarIcon);
        if (tvTitle != null) tvTitle.setText(R.string.title_edit_hike);
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

        hikeId = getIntent().getIntExtra("hike_id", -1);
        db = new HikeDatabaseHelper(this);

        etName = findViewById(R.id.etName);
        etLocation = findViewById(R.id.etLocation);
        etDate = findViewById(R.id.etDate);
        etLength = findViewById(R.id.etLength);
        etDescription = findViewById(R.id.etDescription);
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
        h.setDifficulty(spinnerDifficulty.getSelectedItem().toString());
        h.setParking(switchParking!=null && switchParking.isChecked());
        h.setDescription((etDescription!=null && etDescription.getText()!=null) ? etDescription.getText().toString().trim() : "");

        int rows = db.updateHike(h);
        if (rows>0) {
            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
            finish();
        } else Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
    }
}
