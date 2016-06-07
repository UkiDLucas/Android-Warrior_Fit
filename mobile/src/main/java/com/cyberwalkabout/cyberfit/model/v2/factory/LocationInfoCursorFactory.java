package com.cyberwalkabout.cyberfit.model.v2.factory;

import android.database.Cursor;
import android.text.TextUtils;

import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.LocationInfoTable;
import com.cyberwalkabout.cyberfit.model.v2.LocationInfo;
import com.cyberwalkabout.cyberfit.model.v2.ModelFactory;

/**
 * @author Andrii Kovalov
 */
public class LocationInfoCursorFactory implements ModelFactory<LocationInfo, Cursor> {

    private static final LocationInfoCursorFactory INSTANCE = new LocationInfoCursorFactory();

    public static LocationInfoCursorFactory getInstance() {
        return INSTANCE;
    }

    private LocationInfoCursorFactory() {
    }

    @Override
    public LocationInfo create(Cursor cursor) {
        LocationInfo locationInfo = new LocationInfo();
        locationInfo.setId(cursor.getLong(cursor.getColumnIndex(LocationInfoTable.COLUMN_ID)));
        locationInfo.setExerciseHistoryRecordId(cursor.getLong(cursor.getColumnIndex(LocationInfoTable.COLUMN_EXERCISE_SESSION_ID)));

        String type = cursor.getString(cursor.getColumnIndex(LocationInfoTable.COLUMN_TYPE));
        if (!TextUtils.isEmpty(type)) {
            locationInfo.setType(LocationInfo.LocationType.valueOf(type));
        }

        locationInfo.setSpeed(cursor.getDouble(cursor.getColumnIndex(LocationInfoTable.COLUMN_SPEED)));
        locationInfo.setAccuracy(cursor.getFloat(cursor.getColumnIndex(LocationInfoTable.COLUMN_ACCURACY)));
        locationInfo.setAltitude(cursor.getDouble(cursor.getColumnIndex(LocationInfoTable.COLUMN_ALTITUDE)));
        locationInfo.setBearing(cursor.getFloat(cursor.getColumnIndex(LocationInfoTable.COLUMN_BEARING)));
        locationInfo.setCurrentDistance(cursor.getDouble(cursor.getColumnIndex(LocationInfoTable.COLUMN_CURRENT_DISTANCE)));
        locationInfo.setLatitude(cursor.getDouble(cursor.getColumnIndex(LocationInfoTable.COLUMN_LATITUDE)));
        locationInfo.setLongitude(cursor.getDouble(cursor.getColumnIndex(LocationInfoTable.COLUMN_LONGITUDE)));
        locationInfo.setPace(cursor.getDouble(cursor.getColumnIndex(LocationInfoTable.COLUMN_PACE)));
        locationInfo.setTimestamp(cursor.getLong(cursor.getColumnIndex(LocationInfoTable.COLUMN_TIMESTAMP)));

        return locationInfo;
    }
}
