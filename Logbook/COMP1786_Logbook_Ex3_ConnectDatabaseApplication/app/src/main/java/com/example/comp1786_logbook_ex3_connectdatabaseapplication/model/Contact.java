package com.example.comp1786_logbook_ex3_connectdatabaseapplication.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "contacts")
public class Contact {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;
    public String dob;
    public String email;
    public String avatarName;

    // New fields: favorite flag and timestamps
    public boolean isFavorite;
    public long createdAt;
    public long updatedAt;

    public Contact(String name, String dob, String email, String avatarName) {
        this.name = name;
        this.dob = dob;
        this.email = email;
        this.avatarName = avatarName;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
        this.isFavorite = false;
    }

    // Convenience constructor to explicitly set timestamps and favorite
    public Contact(String name, String dob, String email, String avatarName, boolean isFavorite, long createdAt, long updatedAt) {
        this.name = name;
        this.dob = dob;
        this.email = email;
        this.avatarName = avatarName;
        this.isFavorite = isFavorite;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Room requires a no-arg constructor for some uses
    public Contact() { }
}
