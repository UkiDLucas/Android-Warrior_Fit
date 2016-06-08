package com.warriorfitapp.db.sqlite.schema.table;

/**
 * @author Andrii Kovalov
 */
public class SocialProfileTable extends DBTable {
    public static final String TABLE_NAME = "social_profile";

    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_SOCIAL_ID = "social_id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_IS_PRIMARY = "is_primary";
    public static final String COLUMN_TOKEN = "token";
    public static final String COLUMN_DATE_CREATED = "date_created";

    public static final String[] ALL_COLUMNS = new String[]{
            COLUMN_ID,
            COLUMN_USER_ID,
            COLUMN_SOCIAL_ID,
            COLUMN_TYPE,
            COLUMN_URL,
            COLUMN_EMAIL,
            COLUMN_IS_PRIMARY,
            COLUMN_TOKEN,
            COLUMN_DATE_CREATED
    };

    private static final SocialProfileTable INSTANCE = new SocialProfileTable();

    public static SocialProfileTable instance() {
        return INSTANCE;
    }

    private SocialProfileTable() {
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
                COLUMN_USER_ID + " integer, " +
                COLUMN_SOCIAL_ID + " text, " +
                COLUMN_TYPE + " text, " +
                COLUMN_URL + " text, " +
                COLUMN_EMAIL + " text, " +
                COLUMN_IS_PRIMARY + " integer, " +
                COLUMN_TOKEN + " text, " +
                COLUMN_DATE_CREATED + " integer, " +
                foreignKeyConstraint(COLUMN_USER_ID, com.warriorfitapp.db.sqlite.schema.table.UserTable.TABLE_NAME)
                + ")";
    }
}
