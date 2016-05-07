package com.cyberwalkabout.cyberfit.db.sqlite.schema.table;

/**
 * @author Andrii Kovalov
 */
public class AuthorTable extends DBTable {
    public static final String TABLE_NAME = "author";

    public static final String COLUMN_NAME = "name";

    public static final String[] ALL_COLUMNS = new String[]
            {
                    COLUMN_ID,
                    COLUMN_NAME
            };

    public static final String[] ALL_COLUMNS_QUALIFIED = new String[]
            {
                    TABLE_NAME + "." + COLUMN_ID,
                    TABLE_NAME + "." + COLUMN_NAME
            };

    private static final AuthorTable INSTANCE = new AuthorTable();

    public static AuthorTable instance() {
        return INSTANCE;
    }

    private AuthorTable() {
        super(TABLE_NAME);
    }

    @Override
    public String getCreateSqlStatement() {
        return "create table " + TABLE_NAME
                + "(" +
                COLUMN_ID + " integer primary key, " +
                COLUMN_NAME + " text"
                + ")";
    }

    @Override
    public String[] getColumnNames() {
        return ALL_COLUMNS;
    }
}
