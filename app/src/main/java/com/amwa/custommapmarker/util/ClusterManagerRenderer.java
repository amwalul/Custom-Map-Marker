package com.amwa.custommapmarker.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amwa.custommapmarker.R;
import com.amwa.custommapmarker.data.model.Vehicle;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class ClusterManagerRenderer extends DefaultClusterRenderer<Vehicle> {
    private final IconGenerator iconGenerator;
    private final ImageView imageView;
    private final int markerWidth;
    private final int markerHeight;

    public ClusterManagerRenderer(Context context, GoogleMap map, ClusterManager<Vehicle> clusterManager) {
        super(context, map, clusterManager);

        iconGenerator = new IconGenerator(context);
        imageView = new ImageView(context);
        markerWidth = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        markerHeight = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth, markerHeight));
        int padding = (int) context.getResources().getDimension(R.dimen.custom_marker_padding);
        imageView.setPadding(padding, padding, padding, padding);
        iconGenerator.setContentView(imageView);
    }

    @Override
    protected void onBeforeClusterItemRendered(Vehicle item, MarkerOptions markerOptions) {
        imageView.setImageBitmap(item.getVehicleImage());
        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<Vehicle> cluster) {
        return false;
    }

    @Override
    protected void onClusterItemRendered(Vehicle clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
    }

    @Override
    public void setOnClusterItemClickListener(ClusterManager.OnClusterItemClickListener<Vehicle> listener) {
        super.setOnClusterItemClickListener(listener);
    }
}
