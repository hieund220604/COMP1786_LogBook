package com.example.m_hike_native.model;

public class Observation {
    private int id;
    private int hikeId;
    private String observation;
    private String time;
    private String comments;

    public Observation() {}

    public Observation(int hikeId, String observation, String time, String comments) {
        this.hikeId = hikeId;
        this.observation = observation;
        this.time = time;
        this.comments = comments;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getHikeId() { return hikeId; }
    public void setHikeId(int hikeId) { this.hikeId = hikeId; }

    public String getObservation() { return observation; }
    public void setObservation(String observation) { this.observation = observation; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}

