package com.warriorfitapp.db.sqlite.schema.table;

/**
 * @author Andrii Kovalov
 */
public class ExerciseSessionTable extends DBTable {
    public static final String TABLE_NAME = "exercise_session";
    public static final String COLUMN_REPETITIONS = "repetitions";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_TIMESTAMP_COMPLETED = "timestamp_completed";
    public static final String COLUMN_TIMESTAMP_STARTED = "timestamp_started";
    public static final String COLUMN_LAST_TIMESTAMP_STARTED = "last_timestamp_started";
    public static final String COLUMN_STATE = "state";
    public static final String COLUMN_EXERCISE_ID = "exercise_id";
    // public static final String COLUMN_YOUTUBE_ID = "youtube_id";
    public static final String COLUMN_AVG_PACE = "avg_pace";
    public static final String COLUMN_AVG_SPEED = "avg_speed";
    public static final String COLUMN_AVG_ALTITUDE = "avg_altitude";
    public static final String COLUMN_TOP_ALTITUDE = "top_altitude";
    public static final String COLUMN_LOWEST_ALTITUDE = "lowest_altitude";
    public static final String COLUMN_TOP_SPEED = "top_speed";
    public static final String COLUMN_TOP_PACE = "top_pace";
    public static final String COLUMN_USER_NOTE = "user_note";
    public static final String COLUMN_USER_ID = "user_id";

    public static final String[] ALL_COLUMNS = new String[]
            {
                    COLUMN_ID,
                    COLUMN_REPETITIONS,
                    COLUMN_DISTANCE,
                    COLUMN_WEIGHT,
                    COLUMN_TIME,
                    COLUMN_EXERCISE_ID,
                    COLUMN_TIMESTAMP_COMPLETED,
                    COLUMN_STATE,
                    COLUMN_TIMESTAMP_STARTED,
                    COLUMN_LAST_TIMESTAMP_STARTED,
                    COLUMN_AVG_PACE,
                    COLUMN_AVG_SPEED,
                    COLUMN_AVG_ALTITUDE,
                    COLUMN_TOP_ALTITUDE,
                    COLUMN_TOP_PACE,
                    COLUMN_TOP_SPEED,
                    COLUMN_USER_NOTE,
                    COLUMN_USER_ID,
                    COLUMN_LOWEST_ALTITUDE
            };

    public static final String[] ALL_COLUMNS_QUALIFIED = new String[]
            {
                    TABLE_NAME + "." + COLUMN_ID,
                    TABLE_NAME + "." + COLUMN_REPETITIONS,
                    TABLE_NAME + "." + COLUMN_DISTANCE,
                    TABLE_NAME + "." + COLUMN_WEIGHT,
                    TABLE_NAME + "." + COLUMN_TIME,
                    TABLE_NAME + "." + COLUMN_EXERCISE_ID,
                    TABLE_NAME + "." + COLUMN_TIMESTAMP_COMPLETED,
                    TABLE_NAME + "." + COLUMN_STATE,
                    TABLE_NAME + "." + COLUMN_TIMESTAMP_STARTED,
                    TABLE_NAME + "." + COLUMN_LAST_TIMESTAMP_STARTED,
                    TABLE_NAME + "." + COLUMN_AVG_PACE,
                    TABLE_NAME + "." + COLUMN_AVG_SPEED,
                    TABLE_NAME + "." + COLUMN_AVG_ALTITUDE,
                    TABLE_NAME + "." + COLUMN_TOP_ALTITUDE,
                    TABLE_NAME + "." + COLUMN_TOP_PACE,
                    TABLE_NAME + "." + COLUMN_TOP_SPEED,
                    TABLE_NAME + "." + COLUMN_USER_NOTE,
                    TABLE_NAME + "." + COLUMN_USER_ID,
                    TABLE_NAME + "." + COLUMN_LOWEST_ALTITUDE,
            };

    private static final ExerciseSessionTable INSTANCE = new ExerciseSessionTable();

    public static ExerciseSessionTable instance() {
        return INSTANCE;
    }

    public ExerciseSessionTable() {
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
                COLUMN_REPETITIONS + " integer, " +
                COLUMN_DISTANCE + " real, " +
                COLUMN_WEIGHT + " real, " +
                COLUMN_TIME + " integer, " +
                COLUMN_TIMESTAMP_COMPLETED + " integer, " +
                COLUMN_STATE + " text, " +
                COLUMN_EXERCISE_ID + " text, " +
                COLUMN_TIMESTAMP_STARTED + " integer, " +
                COLUMN_LAST_TIMESTAMP_STARTED + " integer, " +
                COLUMN_AVG_PACE + " real, " +
                COLUMN_AVG_SPEED + " real, " +
                COLUMN_AVG_ALTITUDE + " real, " +
                COLUMN_TOP_ALTITUDE + " real, " +
                COLUMN_LOWEST_ALTITUDE + " real, " +
                COLUMN_TOP_PACE + " real, " +
                COLUMN_TOP_SPEED + " real, " +
                COLUMN_USER_NOTE + " text, " +
                COLUMN_USER_ID + " integer, " +
                foreignKeyConstraint(COLUMN_EXERCISE_ID, ExerciseTable.TABLE_NAME) + ", " +
                foreignKeyConstraint(COLUMN_USER_ID, UserTable.TABLE_NAME)
                + ")";
    }
}
