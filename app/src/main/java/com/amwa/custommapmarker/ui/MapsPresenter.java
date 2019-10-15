package com.amwa.custommapmarker.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.amwa.custommapmarker.data.DataManager;
import com.amwa.custommapmarker.data.model.History;
import com.amwa.custommapmarker.data.model.Vehicle;
import com.amwa.custommapmarker.data.network.ApiInterface;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;

import java.util.ArrayList;
import java.util.List;

public class MapsPresenter {
    private DataManager mDataManager;
    private MapsInterface mView;

    public MapsPresenter(Context context, ApiInterface apiInterface) {
        mDataManager = new DataManager(context, apiInterface);
    }

    public void onAttach(MapsInterface view) {
        mView = view;
    }

    public DataManager getDataManager() {
        return mDataManager;
    }

    public MapsInterface getView() {
        return mView;
    }

    public void onViewInitialized() {
        new LoadVehicleTask(this).execute();
    }

    public void onHistoryContainerExpand() {
        mView.showHistories(
                mDataManager.loadHistoryData(),
                mDataManager.getDate()
        );
    }

    public void onHistoryItemClick(History history, GeoApiContext geoApiContext) {
        if (history.getPolyline() != null) {
            mView.showPolyline(history.getPolyline().getPoints());
        } else {
            mDataManager.getPolyline(history, geoApiContext, new PendingResult.Callback<DirectionsResult>() {
                @Override
                public void onResult(DirectionsResult result) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(result.routes[0].overviewPolyline.getEncodedPath());

                        List<LatLng> newDecodedPath = new ArrayList<>();
                        for (com.google.maps.model.LatLng latLng : decodedPath) {
                            newDecodedPath.add(new LatLng(latLng.lat, latLng.lng));
                        }

                        mView.addPolyline(history, newDecodedPath);
                    });
                }

                @Override
                public void onFailure(Throwable e) {
                    mView.showErrorMessage(e.getLocalizedMessage());
                }
            });
        }
    }

    private static class LoadVehicleTask extends AsyncTask<Void, Void, Vehicle> {

        MapsPresenter mapsPresenter;

        public LoadVehicleTask(MapsPresenter mapsPresenter) {
            this.mapsPresenter = mapsPresenter;
        }

        @Override
        protected Vehicle doInBackground(Void... voids) {
            return mapsPresenter.getDataManager().getData();
        }

        @Override
        protected void onPostExecute(Vehicle vehicle) {
            super.onPostExecute(vehicle);
            mapsPresenter.getView().showVehicle(vehicle);
        }
    }
}
