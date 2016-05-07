package com.cyberwalkabout.cyberfit.db.sqlite.schema.table;

/**
 * @author Andrii Kovalov
 */
public class ProgramTable extends DBTable {
    public static final String TABLE_NAME = "program";

    public static final String COLUMN_ACTIVE = "active";
    public static final String COLUMN_PREMIUM = "premium";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_AUTHOR_ID = "author_id";

    public static final String ALIAS_AUTHOR_NAME = "author_name";
    public static final String ALIAS_SELECTED_PROGRAM_ID = "selected_program_id";
    public static final String ALIAS_SUBSCRIBED_PROGRAM_ID = "subscribed_program_id";
    public static final String ALIAS_EXERCISES_COMPLETED_PER_PROGRAM = "exercises_completed_per_program";

    public static final String[] ALL_COLUMNS = new String[]
            {
                    COLUMN_ID,
                    COLUMN_ACTIVE,
                    COLUMN_PREMIUM,
                    COLUMN_NAME,
                    COLUMN_DESCRIPTION,
                    COLUMN_AUTHOR_ID
            };
    public static final String[] ALL_COLUMNS_JOIN_AUTHOR_NAME = new String[]
            {
                    ProgramTable.TABLE_NAME + "." + ProgramTable.COLUMN_ID,
                    AuthorTable.TABLE_NAME + "." + AuthorTable.COLUMN_NAME + " as " + ALIAS_AUTHOR_NAME,
                    ProgramTable.TABLE_NAME + "." + ProgramTable.COLUMN_NAME,
                    ProgramTable.COLUMN_AUTHOR_ID,
                    ProgramTable.COLUMN_DESCRIPTION,
                    ProgramTable.COLUMN_PREMIUM,
                    ProgramTable.COLUMN_ACTIVE
            };

    private static final ProgramTable INSTANCE = new ProgramTable();

    public static ProgramTable instance() {
        return INSTANCE;
    }

    private ProgramTable() {
        super(TABLE_NAME);
    }

    @Override
    public String getCreateSqlStatement() {
        return "create table " + TABLE_NAME
                + "(" +
                COLUMN_ID + " integer primary key, " +
                COLUMN_ACTIVE + " integer, " +
                COLUMN_PREMIUM + " integer, " +
                COLUMN_NAME + " text, " +
                COLUMN_DESCRIPTION + " text, " +
                COLUMN_AUTHOR_ID + " integer, " +
                foreignKeyConstraint(COLUMN_AUTHOR_ID, AuthorTable.TABLE_NAME)
                + ")";
    }

    // COLUMN_DATE_MODIFIED + " integer, " + // Unix Time, the number of seconds since 1970-01-01 00:00:00 UTC.

    @Override
    public String[] getColumnNames() {
        return ALL_COLUMNS;
    }
}
