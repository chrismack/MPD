package com.sceneit.chris.sceneit.main.data.utils;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Chris on 11/03/2018.
 */

public class Utils {

    /*
     * FROM: https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude-what-am-i-doi
     */

    public static double distanceBetween(LatLng pos1, LatLng pos2) {
        final int earthRadius = 6371;

        double latDist = Math.toRadians(pos2.latitude - pos1.latitude);
        double lonDist = Math.toRadians(pos2.longitude - pos1.longitude);

        double a = Math.sin(latDist / 2) * Math.sin(latDist / 2)
                + Math.cos(Math.toRadians(pos1.latitude)) * Math.cos(Math.toRadians(pos2.latitude))
                * Math.sin(lonDist / 2) * Math.sin(lonDist / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Convert to meters
        double distance = earthRadius * c * 1000;

        return distance;
    }

    public static LatLng addMeters(double meters, LatLng loc) {


        double lat = loc.latitude;
        double lon = loc.longitude;

        double coef = meters * 0.0000089;

        double new_lat = lat + coef;
        double new_long = lon + coef / Math.cos(lat * 0.018);

        LatLng newLoc = new LatLng(new_lat, new_long);

        return newLoc;
    }

    public static Date getDateFrom(String str) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss:SSS");
        try {
            date = sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static boolean isSameDay(@NonNull Date d1, @NonNull Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        return isSameDay(c1, c2);
    }

    public static boolean isSameDay(@NonNull Calendar c1, @NonNull Calendar c2) {
        return c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR) &&
                c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) &&
                c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
    }
}
