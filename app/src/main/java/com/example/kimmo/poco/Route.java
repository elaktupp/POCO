package com.example.kimmo.poco;
/*
    TODO: This is very bad database, loads of duplicate information! Rework needed!
    long   long      String long      long    long       double    double     double    String String
	pk_id, route_id, title, start_time, end_time, timestamp, latitude, longitude, altitude, ssid, rddi
 */
public class Route {
    private long id;        // Primary Key
    private long route_id;
    private String title;
    private long start_time;
    private long end_time;
    private long timestamp;
    private double latitude;
    private double longitude;
    private double altitude;
    private String ssid;
    private String rddi;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRouteId() {
        return route_id;
    }

    public void setRouteId(long route_id) {
        this.route_id = route_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getStartTime() {
        return start_time;
    }

    public void setStartTime(long start_time) {
        this.start_time = start_time;
    }

    public long getEndTime() {
        return end_time;
    }

    public void setEndTime(long end_time) {
        this.end_time = end_time;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getRddi() {
        return rddi;
    }

    public void setRddi(String rddi) {
        this.rddi = rddi;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return title;
    }
}
