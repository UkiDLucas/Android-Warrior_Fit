package com.warriorfitapp.mobile.model.v2.factory;

import android.location.Location;

import com.warriorfitapp.mobile.util.ConvertUtils;
import com.warriorfitapp.model.v2.ModelFactory;

/**
 * @author Andrii Kovalov
 */
public class LocationInfoAndroidLocationFactory implements ModelFactory<com.warriorfitapp.model.v2.LocationInfo, Location> {

    private static final LocationInfoAndroidLocationFactory INSTANCE = new LocationInfoAndroidLocationFactory();

    public static LocationInfoAndroidLocationFactory getInstance() {
        return INSTANCE;
    }

    private LocationInfoAndroidLocationFactory() {
    }

    @Override
    public com.warriorfitapp.model.v2.LocationInfo create(Location location) {
        com.warriorfitapp.model.v2.LocationInfo locationInfo = new com.warriorfitapp.model.v2.LocationInfo();
        locationInfo.setLatitude(location.getLatitude());
        locationInfo.setLongitude(location.getLongitude());
        locationInfo.setAltitude(location.getAltitude());
        locationInfo.setAccuracy(location.getAccuracy());
        locationInfo.setBearing(location.getBearing());
        locationInfo.setTimestamp(location.getTime());
        if (location.hasSpeed()) {
            locationInfo.setSpeed(ConvertUtils.metersPerSecToKmPerHour(location.getSpeed()));
        }
        return locationInfo;
    }
}
