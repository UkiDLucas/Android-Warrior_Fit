package com.warriorfitapp.db.sqlite.schema.table;

/**
 * @author Andrii Kovalov
 */
public class AndroidMetadataTable extends DBTable {
    public static final String TABLE_NAME = "android_metadata";
    public static final String COLUMN_LOCALE = "locale";

    private static final AndroidMetadataTable INSTANCE = new AndroidMetadataTable();

    public static AndroidMetadataTable instance() {
        return INSTANCE;
    }

    private AndroidMetadataTable() {
        super(TABLE_NAME);
    }

    @Override
    public String getCreateSqlStatement() {
        return "create table " + TABLE_NAME + " (" + COLUMN_LOCALE + " TEXT DEFAULT 'en_US')";
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{COLUMN_LOCALE};
    }
}
