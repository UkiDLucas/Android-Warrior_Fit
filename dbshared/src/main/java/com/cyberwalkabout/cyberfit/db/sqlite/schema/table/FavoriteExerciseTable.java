package com.cyberwalkabout.cyberfit.db.sqlite.schema.table;

/**
 * @author Andrii Kovalov
 */
// TODO: consider to add user_id
public class FavoriteExerciseTable extends DBTable {
    public static final String TABLE_NAME = "favorite_exercise";
    public static final String COLUMN_EXERCISE_ID = "exercise_id";

    public static final String[] ALL_COLUMNS = new String[]{COLUMN_EXERCISE_ID};

    private static final FavoriteExerciseTable INSTANCE = new FavoriteExerciseTable();

    public static FavoriteExerciseTable instance() {
        return INSTANCE;
    }

    private FavoriteExerciseTable() {
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
                COLUMN_EXERCISE_ID + " text unique, " +
                foreignKeyConstraint(COLUMN_EXERCISE_ID, ExerciseTable.TABLE_NAME)
                + ")";
    }
}
