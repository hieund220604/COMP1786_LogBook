package com.example.m_hike_native.util;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class DatabaseExportUtils {
    private static final String TAG = "DatabaseExportUtils";

    public static File exportDatabase(Context context, String dbName) throws IOException {
        // Source: /data/data/<package>/databases/<dbName>
        File dbFile = context.getDatabasePath(dbName);
        if (dbFile == null || !dbFile.exists()) {
            throw new IOException("Database file not found: " + (dbFile != null ? dbFile.getAbsolutePath() : dbName));
        }

        File exportsDir = new File(context.getExternalFilesDir(null), "exports");
        if (!exportsDir.exists()) {
            boolean ok = exportsDir.mkdirs();
            if (!ok) Log.w(TAG, "Could not create exports dir: " + exportsDir.getAbsolutePath());
        }

        File outFile = new File(exportsDir, dbName);

        try (FileInputStream in = new FileInputStream(dbFile);
             FileOutputStream out = new FileOutputStream(outFile)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.flush();
        }

        return outFile;
    }
}
