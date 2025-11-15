package com.example.m_hike_native.model;

public class Hike {
    private int id;
    private String name;
    private String location;
    private String date;
    private String difficulty;
    private String description;
    private double length;
    private boolean parking;

    public Hike() {}

    public Hike(String name, String location, String date, double length, String difficulty, boolean parking, String description) {
        this.name = name;
        this.location = location;
        this.date = date;
        this.length = length;
        this.difficulty = difficulty;
        this.parking = parking;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getLength() { return length; }
    public void setLength(double length) { this.length = length; }

    public boolean isParking() { return parking; }
    public void setParking(boolean parking) { this.parking = parking; }

    @Override
    public String toString() {
        return name + " — " + location;
    }
}

