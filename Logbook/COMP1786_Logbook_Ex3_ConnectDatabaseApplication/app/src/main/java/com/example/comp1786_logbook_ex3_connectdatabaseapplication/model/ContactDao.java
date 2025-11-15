package com.example.comp1786_logbook_ex3_connectdatabaseapplication.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long upsert(Contact contact);

    @Query("SELECT * FROM contacts ORDER BY name COLLATE NOCASE")
    List<Contact> getAll();

    @Query("SELECT * FROM contacts WHERE id = :id LIMIT 1")
    Contact getById(long id);

    @Delete
    void delete(Contact contact);

    // New: clear all contacts
    @Query("DELETE FROM contacts")
    void clearAll();

    // New: delete a list of contacts by IDs
    @Query("DELETE FROM contacts WHERE id IN (:ids)")
    void deleteByIds(List<Long> ids);

    // New: different sort options
    @Query("SELECT * FROM contacts ORDER BY name COLLATE NOCASE ASC")
    List<Contact> getAllSortedByNameAsc();

    @Query("SELECT * FROM contacts ORDER BY name COLLATE NOCASE DESC")
    List<Contact> getAllSortedByNameDesc();

    @Query("SELECT * FROM contacts ORDER BY createdAt DESC")
    List<Contact> getAllSortedByCreatedAtDesc();

    @Query("SELECT * FROM contacts ORDER BY createdAt ASC")
    List<Contact> getAllSortedByCreatedAtAsc();

    // Birthday is stored as dd/MM/yyyy string; extract month for filter
    @Query("SELECT * FROM contacts WHERE substr(dob, 4, 2) = :monthStr")
    List<Contact> getByBirthMonth(String monthStr);

    // Favorites
    @Query("SELECT * FROM contacts WHERE isFavorite = 1 ORDER BY name COLLATE NOCASE")
    List<Contact> getFavorites();

    // Simple search by name or email (case-insensitive)
    @Query("SELECT * FROM contacts WHERE name LIKE :pattern OR email LIKE :pattern ORDER BY name COLLATE NOCASE")
    List<Contact> searchByNameOrEmail(String pattern);

    // Update favorite flag in-place (avoids replacing the whole row)
    @Query("UPDATE contacts SET isFavorite = :fav, updatedAt = :ts WHERE id = :id")
    void updateFavorite(long id, int fav, long ts);
}
