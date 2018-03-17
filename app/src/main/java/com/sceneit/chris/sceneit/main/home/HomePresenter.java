package com.sceneit.chris.sceneit.main.home;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.sceneit.chris.sceneit.MainModel;
import com.sceneit.chris.sceneit.R;
import com.sceneit.chris.sceneit.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 05/03/2018.
 */

public class HomePresenter implements HomeContract.IHomePresenter, LocationListener {

    private MainModel mainModel = MainModel.getInstance();

    private HomeFragment view;
    private HomeModel model;
    private LocationManager locManager;

    private HeatmapTileProvider heatMap = null;
    private TileOverlay tileOverlay = null;

    private SharedPreferences sharedPreferences;
    private boolean showHeatMap = true;

    public HomePresenter(HomeFragment view) {
        this.view = view;
        this.model = new HomeModel(this);

        sharedPreferences = this.view.getActivity().getApplicationContext().getSharedPreferences(mainModel.getAuth().getCurrentUser().getUid(), Context.MODE_PRIVATE);
        showHeatMap = this.sharedPreferences.getBoolean(MainModel.USE_HEATMAP, true);
    }

    @Override
    public void mapSetup(final GoogleMap map) {
        MapsInitializer.initialize(view.getContext());
        this.model.setRangeColour(this.view.getResources().getColor(R.color.colorAccent, null));
        boolean suc = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(
            view.getActivity().getApplicationContext(), R.raw.style_json
        ));

        this.model.setGoogleMap(map);

        CameraUpdate c = CameraUpdateFactory.zoomTo(this.model.getInitalZoomLevel());
        map.moveCamera(c);

        // Check for a location manager
        if (mainModel.getLocationManager() != null) {
            this.locManager = mainModel.getLocationManager();
        } else {
            this.locManager = (LocationManager) this.view.getActivity().getSystemService(Context.LOCATION_SERVICE);
            mainModel.setLocationManager(this.locManager);
        }

        // Ensure location permission have been allowed
        if (ActivityCompat.checkSelfPermission(this.view.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.view.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        map.setMyLocationEnabled(true);
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        Location location = this.locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            LatLng lastKnowLoc = new LatLng(location.getLatitude(), location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLng(lastKnowLoc));

            this.model.setRangeCirlce(map.addCircle(new CircleOptions()
                    .center(lastKnowLoc)
                    .radius(this.model.getRangeMeters())
                    .strokeColor(this.model.getRangeColour())
                    .strokeWidth(this.model.getRangeWidth())
            ));
        }

        if(this.sharedPreferences.getBoolean(MainModel.USE_HEATMAP, true)) {
            map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    mapCameraChange(map);
                }
            });
        }

    }

    @Override
    public void updateRangeCircle(Location location) {
        GoogleMap map = this.model.getGoogleMap();
        Circle range = this.model.getRangeCirlce();
        if (map != null && range != null) {
            LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
            range.setCenter(currentLoc);
        }

    }

    @Override
    public void mapCameraChange(final GoogleMap map) {
        VisibleRegion visibleRegion = map.getProjection().getVisibleRegion();
        final LatLng topLeft = visibleRegion.farLeft;
        final LatLng topRight = visibleRegion.farRight;
        LatLng bottomLeft = visibleRegion.nearLeft;
        LatLng bottomRight = visibleRegion.nearRight;

        Query lat = mainModel.getDataStore().collection(MainModel.IMAGE_COLLECTION)
                .whereGreaterThan("lat", bottomLeft.latitude)
                .whereLessThan("lat", topLeft.latitude)
                .orderBy("lat")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(100);

        final ArrayList<LatLng> coords = new ArrayList<>();

        lat.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        for (DocumentSnapshot doc : documentSnapshots.getDocuments()) {
                            double lon = (double) doc.get("lon");
                            if (lon > topLeft.longitude && lon < topRight.longitude) {
                                double lat = (double) doc.get("lat");
                                coords.add(new LatLng(lat, lon));
                            }
                        }
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (coords.size() > 0) {
                            if(heatMap == null) {
                                initHeatMap(map, coords);
                            } else {
                                heatMap.setData(coords);
                                tileOverlay.clearTileCache();
                            }
                        }
                    }
                });
    }

    @Override
    public void initHeatMap(GoogleMap map, List<LatLng> coords) {
        this.heatMap = new HeatmapTileProvider.Builder()
                .data(coords)
                .opacity(0.7)
                .build();
        this.tileOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(heatMap));
    }


    @Override
    public void onLocationChanged(Location location) {
        this.updateRangeCircle(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        System.out.println(s);
    }

    @Override
    public void onProviderEnabled(String s) {
        System.out.println(s);
    }

    @Override
    public void onProviderDisabled(String s) {
        System.out.println(s);
    }

    public LocationManager getLocManager() {
        return locManager;
    }
}
