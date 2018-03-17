package com.sceneit.chris.sceneit.main.home;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.sceneit.chris.sceneit.R;

/**
 * Created by Chris on 05/03/2018.
 */

public class HomeModel {

    private HomePresenter presenter;
    private Circle rangeCirlce;

    private final int rangeMeters = 5000;
    private int rangeColour = Color.RED;
    private final int rangeWidth = 5;

    private final float initalZoomLevel = 11.8f;

    GoogleMap googleMap;

    public HomeModel(HomePresenter presenter) {
        this.presenter = presenter;
        this.rangeCirlce = null;
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public Circle getRangeCirlce() {
        return rangeCirlce;
    }

    public void setRangeCirlce(Circle rangeCirlce) {
        this.rangeCirlce = rangeCirlce;
    }

    public int getRangeMeters() {
        return rangeMeters;
    }

    public int getRangeColour() {
        return rangeColour;
    }

    public int getRangeWidth() {
        return rangeWidth;
    }

    public float getInitalZoomLevel() {
        return initalZoomLevel;
    }

    public void setRangeColour(int rangeColour) {
        this.rangeColour = rangeColour;
    }
}
