package com.sceneit.chris.sceneit.main.data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Chris on 09/03/2018.
 */

public class Image {
    private String uid;
    private double lat;
    private double lon;
    private String fileName;
    private Date timestamp;
    private int points;
    private List<Comments> comments;
    private Map<String, String> votes;

    public Image() {
    }

    public Image(String uid, double lat, double lon, String fileName, Date timestamp, int points, List<Comments> comments, Map<String, String> votes) {
        this.uid = uid;
        this.lat = lat;
        this.lon = lon;
        this.fileName = fileName;
        this.timestamp = timestamp;
        this.points = points;
        this.comments = comments;
        this.votes = votes;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public List<Comments> getComments() {
        return comments;
    }

    public void setComments(List<Comments> comments) {
        this.comments = comments;
    }

    public Map<String, String> getVotes() {
        return votes;
    }

    public void setVotes(Map<String, String> votes) {
        this.votes = votes;
    }
}
