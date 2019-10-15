package com.amwa.custommapmarker.ui;

import com.amwa.custommapmarker.data.model.History;
import com.amwa.custommapmarker.data.model.Vehicle;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface MapsInterface {
    void showVehicle(Vehicle vehicle);
    void showHistories(List<History> histories, String date);
    void showPolyline(List<LatLng> route);
    void addPolyline(History history, List<LatLng> route);
    void showErrorMessage(String message);
}
