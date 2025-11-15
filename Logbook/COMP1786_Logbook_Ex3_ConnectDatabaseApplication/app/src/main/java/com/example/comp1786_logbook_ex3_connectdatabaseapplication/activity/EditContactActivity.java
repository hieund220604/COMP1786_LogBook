package com.example.comp1786_logbook_ex3_connectdatabaseapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.comp1786_logbook_ex3_connectdatabaseapplication.R;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.helper.ResUtils;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.model.AppDatabase;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.model.Contact;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.model.ContactDao;
import com.google.android.material.appbar.MaterialToolbar;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.helper.Constants;
import android.app.DatePickerDialog;
import java.util.Calendar;

import java.util.concurrent.Executors;

public class EditContactActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etName, etDob, etEmail;
    private ImageView imgAvatar;
    private String avatarName = "avatar_1";
    private ContactDao dao;
    private Contact editingContact;
    private static final int REQ_PICK_AVATAR = 2001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        etName = findViewById(R.id.etName);
        etDob = findViewById(R.id.etDob);
        etEmail = findViewById(R.id.etEmail);
        imgAvatar = findViewById(R.id.imgPickAvatar);

        etDob.setOnClickListener(this);
        findViewById(R.id.btnChooseAvatar).setOnClickListener(this);
        findViewById(R.id.btnSave).setOnClickListener(this);

        dao = AppDatabase.getInstance(this).contactDao();

        updateAvatarImage();
        // If editing existing contact, load it
        long id = getIntent().getLongExtra("contact_id", -1);
        if (id != -1) {
            getSupportActionBar().setTitle("Edit Contact");
            Executors.newSingleThreadExecutor().execute(() -> {
                Contact found = dao.getById(id);
                if (found != null) {
                    editingContact = found;
                    runOnUiThread(() -> {
                        etName.setText(editingContact.name);
                        etDob.setText(editingContact.dob);
                        etEmail.setText(editingContact.email);
                        avatarName = editingContact.avatarName != null ? editingContact.avatarName : avatarName;
                        updateAvatarImage();
                    });
                }
            });
        } else {
            getSupportActionBar().setTitle("Add Contact");
        }
    }

    private void updateAvatarImage() {
        if (avatarName.startsWith("content://") || avatarName.startsWith("file://")) {
            try {
                imgAvatar.setImageURI(android.net.Uri.parse(avatarName));
            } catch (SecurityException e) {
                android.util.Log.e("EditContact", "SecurityException loading image: " + avatarName, e);
                // Fall back to default avatar
                imgAvatar.setImageResource(R.drawable.avatar_1);
                Toast.makeText(this, "Unable to load image. Please select a new one.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                android.util.Log.e("EditContact", "Error loading image: " + avatarName, e);
                imgAvatar.setImageResource(R.drawable.avatar_1);
            }
        } else {
            int res = ResUtils.nameToDrawable(this, avatarName);
            if (res != 0) imgAvatar.setImageResource(res);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnChooseAvatar) {
            AvatarPickerActivity.startForResult(this, REQ_PICK_AVATAR, avatarName);
        } else if (id == R.id.etDob) {
            showDatePickerDialog();
        } else if (id == R.id.btnSave) {
            final String name = etName.getText().toString().trim();
            final String dob = etDob.getText().toString().trim();
            final String email = etEmail.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Name required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dob.isEmpty()) {
                Toast.makeText(this, "Date of birth required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
                return;
            }

            final Contact c;
            if (editingContact != null) {
                // Update existing contact
                editingContact.name = name;
                editingContact.dob = dob;
                editingContact.email = email;
                editingContact.avatarName = avatarName;
                editingContact.updatedAt = System.currentTimeMillis();
                c = editingContact;
            } else {
                // Create new contact
                c = new Contact(name, dob, email, avatarName);
            }

            final Contact toSave = c;
            final boolean isNewContact = (editingContact == null);

            Executors.newSingleThreadExecutor().execute(() -> {
                dao.upsert(toSave);
                runOnUiThread(() -> {
                    // Send broadcast to update all screens
                    Intent intent = new Intent(Constants.ACTION_CONTACTS_CHANGED);
                    sendBroadcast(intent);

                    // Show appropriate message
                    String message = isNewContact ? "Contact added" : "Contact updated";
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                    setResult(RESULT_OK);
                    finish();
                });
            });
        }
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    // Format: dd/MM/yyyy
                    String dayStr = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                    String monthStr = (monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : String.valueOf(monthOfYear + 1);
                    String selectedDate = dayStr + "/" + monthStr + "/" + year1;
                    etDob.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PICK_AVATAR && resultCode == RESULT_OK && data != null) {
            String chosen = data.getStringExtra(AvatarPickerActivity.EXTRA_CHOSEN);
            if (chosen != null) {
                avatarName = chosen;
                updateAvatarImage();

                // If editing existing contact, update avatar immediately in database
                // This allows other screens to see the new avatar before saving
                if (editingContact != null) {
                    editingContact.avatarName = avatarName;
                    Executors.newSingleThreadExecutor().execute(() -> {
                        dao.upsert(editingContact);
                        runOnUiThread(() -> {
                            // Broadcast the change so DetailActivity and ListActivity update immediately
                            Intent intent = new Intent(Constants.ACTION_CONTACTS_CHANGED);
                            sendBroadcast(intent);
                            Toast.makeText(this, "Avatar updated", Toast.LENGTH_SHORT).show();
                        });
                    });
                }
            }
        }
    }
}
