package com.cyberwalkabout.cyberfit.db.sqlite.schema.table;

/**
 * @author Andrii Kovalov
 */
public class ExerciseToProgramTable extends DBTable {
    public static final String TABLE_NAME = "exercise_to_program";
    public static final String COLUMN_EXERCISE_ID = "exercise_id";
    public static final String COLUMN_PROGRAM_ID = "program_id";

    private static final ExerciseToProgramTable INSTANCE = new ExerciseToProgramTable();

    public static ExerciseToProgramTable instance() {
        return INSTANCE;
    }

    private ExerciseToProgramTable() {
        super(TABLE_NAME);
    }

    @Override
    public String getCreateSqlStatement() {
        return "create table " + TABLE_NAME
                + "(" +
                COLUMN_ID + " integer primary key autoincrement, " +
                COLUMN_EXERCISE_ID + " text, " +
                COLUMN_PROGRAM_ID + " integer, " +
                foreignKeyConstraint(COLUMN_EXERCISE_ID, ExerciseTable.TABLE_NAME) + ", " +
                foreignKeyConstraint(COLUMN_PROGRAM_ID, ProgramTable.TABLE_NAME)
                + ")";
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{COLUMN_ID, COLUMN_EXERCISE_ID, COLUMN_PROGRAM_ID};
    }
}
