package com.cyberwalkabout.cyberfit.db.sqlite.schema.table;

/**
 * @author Andrii Kovalov
 */
// TODO: consider to add user_id
public class SelectedProgramTable extends DBTable {
    public static final String TABLE_NAME = "selected_program";
    public static final String COLUMN_PROGRAM_ID = "program_id";

    public static final String[] ALL_COLUMNS = new String[]{COLUMN_PROGRAM_ID};

    private static final SelectedProgramTable INSTANCE = new SelectedProgramTable();

    public static SelectedProgramTable instance() {
        return INSTANCE;
    }

    private SelectedProgramTable() {
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
                COLUMN_PROGRAM_ID + " integer unique, " +
                foreignKeyConstraint(COLUMN_PROGRAM_ID, ProgramTable.TABLE_NAME)
                + ")";
    }
}
