package com.amwa.custommapmarker.data.model;

import android.content.Context;

import com.amwa.custommapmarker.util.Util;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import org.json.JSONException;
import org.json.JSONObject;

public class History {
    private LatLng startPosition;
    private LatLng endPosition;
    private String startLocaction;
    private String endLocation;
    private String time;
    private int distance;
    private int duration;
    private int score;
    private Polyline polyline;

    public History(Context context, JSONObject object) {
        try {
            double startLat = object.getJSONObject("start").getDouble("latitude");
            double startLng = object.getJSONObject("start").getDouble("longitude");
            double endLat = object.getJSONObject("end").getDouble("latitude");
            double endLng = object.getJSONObject("end").getDouble("longitude");
            String dateTime = object.getJSONObject("start").getString("tracked_at");

            this.startPosition = new LatLng(startLat, startLng);
            this.endPosition = new LatLng(endLat, endLng);
            this.startLocaction = Util.getAddress(context, startLat, startLng);
            this.endLocation = Util.getAddress(context, endLat, endLng);
            this.time = Util.formatTime(dateTime);
            this.distance = object.getInt("distance");
            this.duration = object.getInt("duration");
            this.score = object.getInt("score");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public LatLng getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(LatLng startPosition) {
        this.startPosition = startPosition;
    }

    public LatLng getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(LatLng endPosition) {
        this.endPosition = endPosition;
    }

    public String getStartLocaction() {
        return startLocaction;
    }

    public void setStartLocaction(String startLocaction) {
        this.startLocaction = startLocaction;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }
}
