package com.warriorfitapp.db.sqlite.schema.table;

/**
 * @author Andrii Kovalov
 */
public class ExerciseTable extends DBTable {
    public static final String TABLE_NAME = "exercise";
    public static final String COLUMN_ACTIVE = "active";
    public static final String COLUMN_DISPLAY_ORDER = "display_order";
    public static final String COLUMN_TRACK_DISTANCE = "track_distance";
    public static final String COLUMN_TRACK_REPETITIONS = "track_repetitions";
    public static final String COLUMN_TRACK_TIME = "track_time";
    public static final String COLUMN_TRACK_WEIGHT = "track_weight";
    public static final String COLUMN_YOUTUBE_ID = "youtube_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_IGNORE_YOUTUBE_TEXT = "ignore_youtube_text";
    public static final String COLUMN_MAP_REQUIRED = "map_required";
    public static final String COLUMN_TRACK_CALORIES = "track_calories";

    public static final String ALIAS_FAVORITE_EXERCISE_ID = "favorite_exercise_id";
    public static final String ALIAS_PROGRAM_NAMES = "program_names";
    public static final String ALIAS_LAST_ACTIVITY_TIMESTAMP = "last_activity_timestamp";
    public static final String ALIAS_EXERCISE_IN_PROGRESS = "exercise_in_progress";

    public static final String[] ALL_COLUMNS = new String[]
            {
                    COLUMN_ID,
                    COLUMN_ACTIVE,
                    COLUMN_DISPLAY_ORDER,
                    COLUMN_TRACK_DISTANCE,
                    COLUMN_TRACK_REPETITIONS,
                    COLUMN_TRACK_TIME,
                    COLUMN_TRACK_WEIGHT,
                    COLUMN_YOUTUBE_ID,
                    COLUMN_NAME,
                    COLUMN_DESCRIPTION,
                    COLUMN_IGNORE_YOUTUBE_TEXT,
                    COLUMN_MAP_REQUIRED,
                    COLUMN_TRACK_CALORIES
            };

    public static final String[] ALL_COLUMNS_QUALIFIED = new String[]
            {
                    TABLE_NAME + "." + COLUMN_ID,
                    TABLE_NAME + "." + COLUMN_ACTIVE,
                    TABLE_NAME + "." + COLUMN_DISPLAY_ORDER,
                    TABLE_NAME + "." + COLUMN_TRACK_DISTANCE,
                    TABLE_NAME + "." + COLUMN_TRACK_REPETITIONS,
                    TABLE_NAME + "." + COLUMN_TRACK_TIME,
                    TABLE_NAME + "." + COLUMN_TRACK_WEIGHT,
                    TABLE_NAME + "." + COLUMN_YOUTUBE_ID,
                    TABLE_NAME + "." + COLUMN_NAME,
                    TABLE_NAME + "." + COLUMN_DESCRIPTION,
                    TABLE_NAME + "." + COLUMN_IGNORE_YOUTUBE_TEXT,
                    TABLE_NAME + "." + COLUMN_MAP_REQUIRED,
                    TABLE_NAME + "." + COLUMN_TRACK_CALORIES
            };

    private static final ExerciseTable INSTANCE = new ExerciseTable();

    public static ExerciseTable instance() {
        return INSTANCE;
    }

    private ExerciseTable() {
        super(TABLE_NAME);
    }

    @Override
    public String getCreateSqlStatement() {
        return "create table " + TABLE_NAME
                + "(" +
                COLUMN_ID + " text primary key, " +
                COLUMN_ACTIVE + " integer, " +
                COLUMN_DISPLAY_ORDER + " integer, " +
                COLUMN_TRACK_DISTANCE + " integer, " +
                COLUMN_TRACK_REPETITIONS + " integer, " +
                COLUMN_TRACK_TIME + " integer, " +
                COLUMN_TRACK_WEIGHT + " integer, " +
                COLUMN_YOUTUBE_ID + " text, " +
                COLUMN_NAME + " text, " +
                COLUMN_DESCRIPTION + " text, " +
                COLUMN_IGNORE_YOUTUBE_TEXT + " integer, " +
                COLUMN_MAP_REQUIRED + " integer, " +
                COLUMN_TRACK_CALORIES + " integer"
                + ")";
    }

    @Override
    public String[] getColumnNames() {
        return ALL_COLUMNS;
    }
}
