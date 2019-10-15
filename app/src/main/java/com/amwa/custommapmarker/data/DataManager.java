package com.amwa.custommapmarker.data;

import android.content.Context;

import com.amwa.custommapmarker.data.model.Vehicle;
import com.amwa.custommapmarker.data.model.History;
import com.amwa.custommapmarker.data.network.ApiHelper;
import com.amwa.custommapmarker.data.network.ApiInterface;
import com.amwa.custommapmarker.util.Util;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private ApiHelper apiHelper;
    private Context context;

    public DataManager(Context context, ApiInterface apiInterface) {
        this.context = context;
        apiHelper = new ApiHelper(apiInterface);
    }

    public Vehicle getData() {
        return apiHelper.getData();
    }

    public List<History> loadHistoryData() {
        ArrayList<History> histories = new ArrayList<>();

        try {
            JSONArray historyObjectList = new JSONObject(
                    Util.loadJSONFromAsset(context, "vehicle-trip.json"))
                    .getJSONArray("trips");

            for (int i = 0; i < historyObjectList.length(); i++) {
                JSONObject historyObject = historyObjectList.getJSONObject(i);
                History history = new History(context, historyObject);
                histories.add(history);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return histories;
    }

    public String getDate() {
        String date;

        try {
            JSONObject historyObject = new JSONObject(
                    Util.loadJSONFromAsset(context, "vehicle-trip.json"))
                    .getJSONArray("trips").getJSONObject(0);

            String dateTime = historyObject.getJSONObject("start").getString("tracked_at");
            date = Util.formatDate(dateTime);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return date;
    }

    public void getPolyline(History history, GeoApiContext geoApiContext, PendingResult.Callback<DirectionsResult> resultCallback) {
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);
        directions.origin(
                new com.google.maps.model.LatLng(
                        history.getStartPosition().latitude,
                        history.getEndPosition().longitude
                )
        );

        directions.destination(
                new com.google.maps.model.LatLng(
                        history.getEndPosition().latitude,
                        history.getEndPosition().longitude
        ));

        directions.setCallback(resultCallback);
    }
}
