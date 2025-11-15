package com.example.m_hike_native.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.m_hike_native.model.Hike;
import com.example.m_hike_native.model.Observation;

public class HikeDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "mhike.db";
    private static final int DB_VERSION = 2;

    public static final String TABLE_HIKES = "hikes";
    public static final String COL_H_ID = "id";
    public static final String COL_H_NAME = "name";
    public static final String COL_H_LOCATION = "location";
    public static final String COL_H_DATE = "date";
    public static final String COL_H_LENGTH = "length";
    public static final String COL_H_DIFFICULTY = "difficulty";
    public static final String COL_H_PARKING = "parking";
    public static final String COL_H_DESCRIPTION = "description";

    public static final String TABLE_OBS = "observations";
    public static final String COL_O_ID = "id";
    public static final String COL_O_HIKE_ID = "hike_id";
    public static final String COL_O_OBSERVATION = "observation";
    public static final String COL_O_TIME = "time";
    public static final String COL_O_COMMENTS = "comments";

    public HikeDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_HIKES + " (" +
                COL_H_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_H_NAME + " TEXT NOT NULL, " +
                COL_H_LOCATION + " TEXT NOT NULL, " +
                COL_H_DATE + " TEXT, " +
                COL_H_LENGTH + " REAL, " +
                COL_H_DIFFICULTY + " TEXT, " +
                COL_H_PARKING + " INTEGER, " +
                COL_H_DESCRIPTION + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_OBS + " (" +
                COL_O_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_O_HIKE_ID + " INTEGER, " +
                COL_O_OBSERVATION + " TEXT, " +
                COL_O_TIME + " TEXT, " +
                COL_O_COMMENTS + " TEXT, " +
                "FOREIGN KEY(" + COL_O_HIKE_ID + ") REFERENCES " + TABLE_HIKES + "(" + COL_H_ID + ") ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OBS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIKES);
        onCreate(db);
    }

    public boolean insertHike(Hike hike) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_H_NAME, hike.getName());
        cv.put(COL_H_LOCATION, hike.getLocation());
        cv.put(COL_H_DATE, hike.getDate());
        cv.put(COL_H_LENGTH, hike.getLength());
        cv.put(COL_H_DIFFICULTY, hike.getDifficulty());
        cv.put(COL_H_PARKING, hike.isParking() ? 1 : 0);
        cv.put(COL_H_DESCRIPTION, hike.getDescription());
        long id = db.insert(TABLE_HIKES, null, cv);
        return id != -1;

    }

    public Cursor getAllHikes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_HIKES + " ORDER BY " + COL_H_NAME + " COLLATE NOCASE", null);
    }

    public Cursor getHikeById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_HIKES + " WHERE " + COL_H_ID + "=?", new String[]{String.valueOf(id)});
    }

    public int deleteHike(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_HIKES, COL_H_ID + "=?", new String[]{String.valueOf(id)});
    }

    public boolean insertObservation(Observation obs) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_O_HIKE_ID, obs.getHikeId());
        cv.put(COL_O_OBSERVATION, obs.getObservation());
        cv.put(COL_O_TIME, obs.getTime());
        cv.put(COL_O_COMMENTS, obs.getComments());
        long id = db.insert(TABLE_OBS, null, cv);
        return id != -1;
    }

    public Cursor getObservationsForHike(int hikeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_OBS + " WHERE " + COL_O_HIKE_ID + "=? ORDER BY id DESC", new String[]{String.valueOf(hikeId)});
    }

    public Cursor searchHikes(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        String q = "%" + query + "%";
        return db.rawQuery("SELECT * FROM " + TABLE_HIKES + " WHERE " + COL_H_NAME + " LIKE ? OR " + COL_H_LOCATION + " LIKE ?", new String[]{q, q});
    }

    public int updateHike(Hike hike) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_H_NAME, hike.getName());
        cv.put(COL_H_LOCATION, hike.getLocation());
        cv.put(COL_H_DATE, hike.getDate());
        cv.put(COL_H_LENGTH, hike.getLength());
        cv.put(COL_H_DIFFICULTY, hike.getDifficulty());
        cv.put(COL_H_PARKING, hike.isParking() ? 1 : 0);
        cv.put(COL_H_DESCRIPTION, hike.getDescription());
        return db.update(TABLE_HIKES, cv, COL_H_ID + "=?", new String[]{String.valueOf(hike.getId())});
    }

}
