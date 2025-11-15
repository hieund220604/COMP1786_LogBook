package com.example.comp1786_logbook_ex3_connectdatabaseapplication.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp1786_logbook_ex3_connectdatabaseapplication.R;
import com.example.comp1786_logbook_ex3_connectdatabaseapplication.helper.ResUtils;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

public class AvatarPickerActivity extends AppCompatActivity {

    public static final String EXTRA_CHOSEN = "chosen";
    public static final String EXTRA_CURRENT_AVATAR = "current_avatar";
    private static final int REQ_PICK_IMAGE = 2002;
    private static final int REQ_PERMISSION = 2003;

    public static void startForResult(AppCompatActivity from, int req) {
        startForResult(from, req, null);
    }

    public static void startForResult(AppCompatActivity from, int req, @Nullable String currentAvatar) {
        Intent intent = new Intent(from, AvatarPickerActivity.class);
        if (currentAvatar != null) {
            intent.putExtra(EXTRA_CURRENT_AVATAR, currentAvatar);
        }
        from.startActivityForResult(intent, req);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_picker);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rvAvatars);
        rv.setLayoutManager(new GridLayoutManager(this, 4));
        List<String> names = ResUtils.avatarNamesFromArray(this, R.array.contact_avatars);
        AvatarGridAdapter adapter = new AvatarGridAdapter(names, this::onPicked);

        // Highlight current avatar if provided
        String current = getIntent().getStringExtra(EXTRA_CURRENT_AVATAR);
        if (current != null) {
            adapter.setSelectedName(current);
        }

        rv.setAdapter(adapter);
    }

    private void onPicked(String name) {
        if (name.equals("upload")) {
            // Check if we need to request permission (Android 13+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_MEDIA_IMAGES},
                            REQ_PERMISSION);
                    return;
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQ_PERMISSION);
                    return;
                }
            }

            // Permission granted or not needed, launch picker
            launchImagePicker();
        } else {
            Intent data = new Intent();
            data.putExtra(EXTRA_CHOSEN, name);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private void launchImagePicker() {
        // Use ACTION_OPEN_DOCUMENT for better permission handling
        // This grants persistent read access to the URI
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // This flag allows us to get persistent read permission
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

        android.util.Log.d("AvatarPicker", "Launching image picker with ACTION_OPEN_DOCUMENT");
        startActivityForResult(intent, REQ_PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, launch image picker
                launchImagePicker();
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied. Cannot access images.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                android.util.Log.d("AvatarPicker", "Received URI: " + uri.toString());

                // Copy the image to internal storage to ensure persistent access
                // This is necessary because Photo Picker URIs are temporary
                try {
                    String savedUri = copyImageToInternalStorage(uri);
                    if (savedUri != null) {
                        android.util.Log.d("AvatarPicker", "Saved URI: " + savedUri);
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(EXTRA_CHOSEN, savedUri);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
                    }
                } catch (SecurityException e) {
                    android.util.Log.e("AvatarPicker", "SecurityException: " + e.getMessage(), e);
                    Toast.makeText(this, "Permission error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    android.util.Log.e("AvatarPicker", "Error saving image: " + e.getMessage(), e);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Copy the selected image to app's internal storage for persistent access
     * @param sourceUri The URI from the image picker
     * @return The file:// URI string of the saved image, or null if failed
     */
    private String copyImageToInternalStorage(Uri sourceUri) {
        android.util.Log.d("AvatarPicker", "Starting image copy from: " + sourceUri);
        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            // Create avatars directory in internal storage
            File avatarsDir = new File(getFilesDir(), "avatars");
            if (!avatarsDir.exists()) {
                boolean created = avatarsDir.mkdirs();
                android.util.Log.d("AvatarPicker", "Created avatars dir: " + created + " at " + avatarsDir.getAbsolutePath());
            }

            // Generate unique filename
            String fileName = "avatar_" + System.currentTimeMillis() + ".jpg";
            File destFile = new File(avatarsDir, fileName);
            android.util.Log.d("AvatarPicker", "Destination file: " + destFile.getAbsolutePath());

            // Copy the image - THIS is where SecurityException might occur
            try {
                inputStream = getContentResolver().openInputStream(sourceUri);
            } catch (SecurityException e) {
                android.util.Log.e("AvatarPicker", "SecurityException opening input stream! This means we don't have permission to read the URI.", e);
                // Re-throw with more context
                throw new SecurityException("Cannot access selected image. The Photo Picker URI may have expired or permission was denied.", e);
            }

            if (inputStream == null) {
                android.util.Log.e("AvatarPicker", "Failed to open input stream for URI: " + sourceUri);
                return null;
            }

            android.util.Log.d("AvatarPicker", "Input stream opened successfully");

            outputStream = new FileOutputStream(destFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalBytes = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            android.util.Log.d("AvatarPicker", "Image copied successfully. Total bytes: " + totalBytes);

            // Verify file exists and has content
            if (destFile.exists() && destFile.length() > 0) {
                String fileUri = Uri.fromFile(destFile).toString();
                android.util.Log.d("AvatarPicker", "Returning file URI: " + fileUri);
                return fileUri;
            } else {
                android.util.Log.e("AvatarPicker", "Destination file is empty or doesn't exist");
                return null;
            }

        } catch (SecurityException e) {
            // Re-throw to be handled in onActivityResult
            android.util.Log.e("AvatarPicker", "SecurityException during image copy - rethrowing", e);
            throw e;
        } catch (Exception e) {
            android.util.Log.e("AvatarPicker", "Error copying image: " + e.getMessage(), e);
        } finally {
            // Clean up streams
            try {
                if (outputStream != null) outputStream.close();
                if (inputStream != null) inputStream.close();
            } catch (Exception e) {
                android.util.Log.e("AvatarPicker", "Error closing streams: " + e.getMessage());
            }
        }
        return null;
    }
}
