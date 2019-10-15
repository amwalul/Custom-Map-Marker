package com.amwa.custommapmarker.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amwa.custommapmarker.R;
import com.amwa.custommapmarker.adapter.HistoryAdapter;
import com.amwa.custommapmarker.data.model.Vehicle;
import com.amwa.custommapmarker.data.model.History;
import com.amwa.custommapmarker.data.network.ApiInterface;
import com.amwa.custommapmarker.util.ClusterManagerRenderer;
import com.amwa.custommapmarker.util.ExpandAnimation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        ClusterManager.OnClusterItemClickListener<Vehicle>, HistoryAdapter.Interaction,
        MapsInterface, ApiInterface {

    private static final int MAP_LAYOUT_STATE_EXPANDED = 0;
    private static final int MAP_LAYOUT_STATE_CONTRACTED = 1;
    private int mMapLayoutState = 0;

    private GoogleMap mMap;
    private ClusterManager<Vehicle> mClusterManager;
    private ClusterManagerRenderer mClusterManagerRenderer;
    private GeoApiContext mGeoApiContext;
    private HistoryAdapter historyAdapter;
    private MapsPresenter mapsPresenter;

    private LinearLayout mHistoryContainer;
    private TextView mTvDate;
    private MapView mMapView;
    private ImageButton mBtnFullScreenMap;

    private Vehicle mCurrentVehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        RecyclerView rvHistory = findViewById(R.id.rvHistory);
        mHistoryContainer = findViewById(R.id.llHistory);
        mTvDate = findViewById(R.id.tvDate);
        mMapView = findViewById(R.id.mapView);
        mBtnFullScreenMap = findViewById(R.id.btnFullScreenMap);

        mBtnFullScreenMap.setOnClickListener( v -> expandOrContractMap(mCurrentVehicle));

        mapsPresenter = new MapsPresenter(this, this);
        mapsPresenter.onAttach(this);

        historyAdapter = new HistoryAdapter(this, this);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(historyAdapter);

        mGeoApiContext = new GeoApiContext.Builder()
                .apiKey(getString(R.string.google_maps_key))
                .build();

        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mClusterManager = new ClusterManager<>(this, mMap);
        mClusterManagerRenderer = new ClusterManagerRenderer(this, mMap, mClusterManager);
        mClusterManager.setRenderer(mClusterManagerRenderer);
        mClusterManager.setOnClusterItemClickListener(this);
        mMap.setOnMarkerClickListener(mClusterManager);

        mapsPresenter.onViewInitialized();
    }

    @Override
    public void showVehicle(Vehicle vehicle) {
        if (mMap != null && vehicle != null) {
            mCurrentVehicle = vehicle;
            addMapMarker(vehicle);

            mMap.setOnMarkerClickListener(mClusterManager);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(vehicle.getPosition(), 15), new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    mClusterManagerRenderer.getMarker(mCurrentVehicle).showInfoWindow();
                }

                @Override
                public void onCancel() {

                }
            });
        }
    }

    public void addMapMarker(Vehicle newVehicle) {
        mClusterManager.addItem(newVehicle);
        mClusterManager.cluster();
    }

    @Override
    public void showHistories(List<History> histories, String date) {
        mTvDate.setText(date);
        historyAdapter.setHistories(histories);
        animateHistoryContainer(true);
    }

    private void expandOrContractMap(Vehicle vehicle) {
        mClusterManagerRenderer.getMarker(vehicle).showInfoWindow();

        if(mMapLayoutState == MAP_LAYOUT_STATE_CONTRACTED){
            mMapLayoutState = MAP_LAYOUT_STATE_EXPANDED;
            animateHistoryContainer(false);
        }
        else if(mMapLayoutState == MAP_LAYOUT_STATE_EXPANDED){
            mMapLayoutState = MAP_LAYOUT_STATE_CONTRACTED;
            mapsPresenter.onHistoryContainerExpand();
        }
    }

    private void animateHistoryContainer(boolean isExpand) {
        int previousWeight;
        int currentWeight;

        if (isExpand) {
            previousWeight = 0;
            currentWeight = 40;
        } else {
            previousWeight = 40;
            currentWeight = 0;
        }

        Animation animation = new ExpandAnimation(mHistoryContainer, previousWeight, currentWeight);
        animation.setDuration(500);

        mHistoryContainer.startAnimation(animation);
    }

    @Override
    public boolean onClusterItemClick(Vehicle vehicle) {
        mCurrentVehicle = vehicle;
        expandOrContractMap(vehicle);

        return true;
    }

    public void zoomRoute(List<LatLng> route) {

        if (mMap == null || route == null || route.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : route)
            boundsBuilder.include(latLngPoint);

        int routePadding = 120;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }

    @Override
    public void onHistoryItemSelected(int position, History history) {
        mapsPresenter.onHistoryItemClick(history, mGeoApiContext);
    }

    @Override
    public void showPolyline(List<LatLng> route) {
        zoomRoute(route);
    }

    @Override
    public void addPolyline(History history, List<LatLng> route) {
        Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(route));
        polyline.setColor(ContextCompat.getColor(MapsActivity.this, R.color.red));
        history.setPolyline(polyline);

        mMap.addMarker(new MarkerOptions().position(route.get(0)).title("Start"));
        mMap.addMarker(new MarkerOptions().position(route.get(route.size() - 1)).title("End"));

        zoomRoute(route);
    }

    @Override
    public void showErrorMessage(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void showNetworkErrorMessage(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
