package com.example.comp1786_logbook_ex3_connectdatabaseapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.comp1786_logbook_ex3_connectdatabaseapplication.R;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.helper.ResUtils;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.model.AppDatabase;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.model.Contact;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.model.ContactDao;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.helper.Constants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.Executors;

public class DetailContactActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_ID = "contact_id";
    private long contactId = -1;
    private ContactDao dao;
    private Contact contact;
    private ShapeableImageView img;
    private TextView tvName, tvDob, tvEmail, tvLastUpdated;
    private static final int REQ_EDIT = 11;

    private final BroadcastReceiver contactsChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.ACTION_CONTACTS_CHANGED.equals(intent.getAction())) {
                loadContact();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        img = findViewById(R.id.imgAvatarDetail);
        tvName = findViewById(R.id.tvName);
        tvDob = findViewById(R.id.tvDob);
        tvEmail = findViewById(R.id.tvEmail);
        tvLastUpdated = findViewById(R.id.tvLastUpdated);
        findViewById(R.id.btnEdit).setOnClickListener(this);

        dao = AppDatabase.getInstance(this).contactDao();
        contactId = getIntent().getLongExtra(EXTRA_ID, -1);
        loadContact();

        // Register broadcast receiver with proper API level check
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(contactsChangedReceiver, new IntentFilter(Constants.ACTION_CONTACTS_CHANGED), Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(contactsChangedReceiver, new IntentFilter(Constants.ACTION_CONTACTS_CHANGED));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(contactsChangedReceiver);
    }

    private void loadContact() {
        Executors.newSingleThreadExecutor().execute(() -> {
            contact = dao.getById(contactId);
            if (contact != null) {
                runOnUiThread(this::showContact);
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Contact not found", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void showContact() {
        getSupportActionBar().setTitle(contact.name);

        // Handle uploaded images (file:// or content://) and built-in avatars
        if (contact.avatarName != null &&
            (contact.avatarName.startsWith("content://") || contact.avatarName.startsWith("file://"))) {
            try {
                img.setImageURI(android.net.Uri.parse(contact.avatarName));
            } catch (SecurityException e) {
                android.util.Log.e("DetailContact", "SecurityException loading image: " + contact.avatarName, e);
                // Fall back to default avatar
                img.setImageResource(R.drawable.avatar_1);
            } catch (Exception e) {
                android.util.Log.e("DetailContact", "Error loading image: " + contact.avatarName, e);
                img.setImageResource(R.drawable.avatar_1);
            }
        } else {
            int res = ResUtils.nameToDrawable(this, contact.avatarName);
            if (res != 0) img.setImageResource(res);
        }

        tvName.setText(contact.name);
        tvDob.setText(contact.dob);
        tvEmail.setText(contact.email);

        if (tvLastUpdated != null) {
            long ts = contact.updatedAt != 0L ? contact.updatedAt : contact.createdAt;
            if (ts != 0L) {
                String formatted = DateFormat.getDateTimeInstance().format(new Date(ts));
                tvLastUpdated.setText(getString(R.string.last_updated_format, formatted));
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(this, EditContactActivity.class);
        i.putExtra("contact_id", contactId);
        startActivityForResult(i, REQ_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_EDIT && resultCode == RESULT_OK) {
            // Reload contact immediately to show updated data
            loadContact();
            // Set result OK so ContactsListActivity knows to refresh
            setResult(RESULT_OK);
        }
    }
}
