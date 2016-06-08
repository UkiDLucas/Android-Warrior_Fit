package com.warriorfitapp.db.sqlite.schema.table;

/**
 * @author Andrii Kovalov
 */
public class LocationInfoTable extends DBTable {
    public static final String TABLE_NAME = "location_info";
    public static final String COLUMN_LATITUDE = "lat";
    public static final String COLUMN_LONGITUDE = "lon";
    public static final String COLUMN_ALTITUDE = "altitude";
    public static final String COLUMN_ACCURACY = "accuracy";
    public static final String COLUMN_BEARING = "bearing";
    public static final String COLUMN_SPEED = "speed";
    public static final String COLUMN_PACE = "pace";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_CURRENT_DISTANCE = "current_distance";
    public static final String COLUMN_EXERCISE_SESSION_ID = "exercise_session_id";

    public static final String[] ALL_COLUMNS = new String[]
            {
                    COLUMN_ID,
                    COLUMN_LATITUDE,
                    COLUMN_LONGITUDE,
                    COLUMN_ALTITUDE,
                    COLUMN_ACCURACY,
                    COLUMN_BEARING,
                    COLUMN_SPEED,
                    COLUMN_PACE,
                    COLUMN_TIMESTAMP,
                    COLUMN_TYPE,
                    COLUMN_CURRENT_DISTANCE,
                    COLUMN_EXERCISE_SESSION_ID
            };

    private static final LocationInfoTable INSTANCE = new LocationInfoTable();

    public static LocationInfoTable instance() {
        return INSTANCE;
    }

    public LocationInfoTable() {
        super(TABLE_NAME);
    }

    @Override
    public String[] getColumnNames() {
        return ALL_COLUMNS;
    }

    @Override
    public String getCreateSqlStatement() {
        return "create table " + TABLE_NAME
                + "(" +
                COLUMN_ID + " integer primary key autoincrement, " +
                COLUMN_LATITUDE + " real, " +
                COLUMN_LONGITUDE + " real, " +
                COLUMN_ALTITUDE + " real, " +
                COLUMN_ACCURACY + " real, " +
                COLUMN_BEARING + " real, " +
                COLUMN_SPEED + " real, " +
                COLUMN_PACE + " real, " +
                COLUMN_TIMESTAMP + " integer, " +
                COLUMN_TYPE + " text, " +
                COLUMN_CURRENT_DISTANCE + " real, " +
                COLUMN_EXERCISE_SESSION_ID + " integer, " +
                foreignKeyConstraint(COLUMN_EXERCISE_SESSION_ID, ExerciseSessionTable.TABLE_NAME)
                + ")";
    }
}
