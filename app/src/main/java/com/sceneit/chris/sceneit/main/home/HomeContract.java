package com.sceneit.chris.sceneit.main.home;

import android.location.Location;
import android.location.LocationListener;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Chris on 05/03/2018.
 */

public interface HomeContract {

    interface IHomePresenter extends LocationListener {
        void mapSetup(GoogleMap map);
        void updateRangeCircle(Location location);
        void mapCameraChange(GoogleMap map);
        void initHeatMap(GoogleMap map, List<LatLng> points);
    }

    interface IHomeView {

    }
}
