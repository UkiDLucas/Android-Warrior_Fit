package com.cyberwalkabout.cyberfit.model.v2.factory;

import android.location.Location;

import com.cyberwalkabout.cyberfit.model.v2.LocationInfo;
import com.cyberwalkabout.cyberfit.model.v2.ModelFactory;
import com.cyberwalkabout.cyberfit.util.ConvertUtils;

/**
 * @author Andrii Kovalov
 */
public class LocationInfoAndroidLocationFactory implements ModelFactory<LocationInfo, Location> {

    private static final LocationInfoAndroidLocationFactory INSTANCE = new LocationInfoAndroidLocationFactory();

    public static LocationInfoAndroidLocationFactory getInstance() {
        return INSTANCE;
    }

    private LocationInfoAndroidLocationFactory() {
    }

    @Override
    public LocationInfo create(Location location) {
        LocationInfo locationInfo = new LocationInfo();
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
