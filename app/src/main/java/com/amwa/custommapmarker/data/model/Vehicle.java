package com.amwa.custommapmarker.data.model;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import org.json.JSONException;
import org.json.JSONObject;

public class Vehicle implements ClusterItem {
    private LatLng position;
    private String title;
    private String snippet;
    private Bitmap vehicleImage;

    public Vehicle() {

    }

    public Vehicle(JSONObject object, Bitmap image) {
        try {
            double latitude = object.getJSONObject("current").getDouble("latitude");
            double longitude = object.getJSONObject("current").getDouble("longitude");

            this.position = new LatLng(latitude, longitude);
            this.title = object.getString("vehicle_number");
            this.snippet = object.getString("description");
            this.vehicleImage = image;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Vehicle(LatLng position, String title, String snippet, Bitmap vehicleImage) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.vehicleImage = vehicleImage;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public Bitmap getVehicleImage() {
        return vehicleImage;
    }

    public void setVehicleImage(Bitmap vehicleImage) {
        this.vehicleImage = vehicleImage;
    }
}
