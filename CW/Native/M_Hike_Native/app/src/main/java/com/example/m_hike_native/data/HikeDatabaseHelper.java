// ...existing code...
    public int updateHike(Hike hike) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_H_NAME, hike.getName());
        cv.put(COL_H_LOCATION, hike.getLocation());
        cv.put(COL_H_DATE, hike.getDate());
        cv.put(COL_H_LENGTH, hike.getLength());
        cv.put(COL_H_ELEVATION, hike.getElevation());
        cv.put(COL_H_DURATION, hike.getDurationMinutes());
        cv.put(COL_H_DIFFICULTY, hike.getDifficulty());
        cv.put(COL_H_PARKING, hike.isParking() ? 1 : 0);
        cv.put(COL_H_DESCRIPTION, hike.getDescription());
        return db.update(TABLE_HIKES, cv, COL_H_ID + "=?", new String[]{String.valueOf(hike.getId())});
    }

    // ...added methods for observations: update and delete
    public int updateObservation(com.example.m_hike_native.model.Observation obs) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_O_OBSERVATION, obs.getObservation());
        cv.put(COL_O_TIME, obs.getTime());
        cv.put(COL_O_COMMENTS, obs.getComments());
        return db.update(TABLE_OBS, cv, COL_O_ID + "=?", new String[]{String.valueOf(obs.getId())});
    }

    public int deleteObservation(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_OBS, COL_O_ID + "=?", new String[]{String.valueOf(id)});
    }

}

