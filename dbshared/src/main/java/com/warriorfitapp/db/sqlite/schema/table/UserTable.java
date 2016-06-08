package com.warriorfitapp.db.sqlite.schema.table;

/**
 * @author Andrii Kovalov
 */
public class UserTable extends DBTable {
    public static final String TABLE_NAME = "user";

    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_DISPLAY_NAME = "display_name";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_HEIGHT = "height";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_IS_MALE = "is_male";
    public static final String COLUMN_BIRTHDAY = "birthday";
    public static final String COLUMN_ACCOUNT_TYPE = "user_type";
    public static final String COLUMN_ACTIVE = "is_active";
    public static final String COLUMN_IMAGE_URI = "image_uri";
    public static final String COLUMN_CURRENT_BODY_FAT = "current_body_fat";
    public static final String COLUMN_DESIRED_BODY_FAT = "desired_body_fat";
    public static final String COLUMN_WAIST = "waist";
    public static final String COLUMN_BUTTOCKS = "buttocks";

    public static final String COLUMN_UNITS_OF_MEASUREMENT = "units_of_measurement";
    public static final String COLUMN_DATE_FORMAT = "date_format";

    public static final String COLUMN_DATE_CREATED = "date_created";

    public static final String[] ALL_COLUMNS = new String[]{
            COLUMN_ID,
            COLUMN_USERNAME,
            COLUMN_DISPLAY_NAME,
            COLUMN_WEIGHT,
            COLUMN_HEIGHT,
            COLUMN_AGE,
            COLUMN_IS_MALE,
            COLUMN_BIRTHDAY,
            COLUMN_ACCOUNT_TYPE,
            COLUMN_ACTIVE,
            COLUMN_IMAGE_URI,
            COLUMN_CURRENT_BODY_FAT,
            COLUMN_DESIRED_BODY_FAT,
            COLUMN_DATE_CREATED,
            COLUMN_WAIST,
            COLUMN_BUTTOCKS,
            COLUMN_UNITS_OF_MEASUREMENT,
            COLUMN_DATE_FORMAT
    };

    private static final UserTable INSTANCE = new UserTable();

    public static UserTable instance() {
        return INSTANCE;
    }

    private UserTable() {
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
                COLUMN_USERNAME + " text, " +
                COLUMN_DISPLAY_NAME + " text, " +
                COLUMN_WEIGHT + " real, " +
                COLUMN_HEIGHT + " real, " +
                COLUMN_AGE + " integer, " +
                COLUMN_IS_MALE + " integer, " +
                COLUMN_BIRTHDAY + " integer, " +
                COLUMN_ACCOUNT_TYPE + " text, " +
                COLUMN_CURRENT_BODY_FAT + " real, " +
                COLUMN_DESIRED_BODY_FAT + " real, " +
                COLUMN_ACTIVE + " integer, " +
                COLUMN_DATE_CREATED + " integer, " +
                COLUMN_IMAGE_URI + " text, " +
                COLUMN_WAIST + " real, " +
                COLUMN_BUTTOCKS + " real, " +
                COLUMN_UNITS_OF_MEASUREMENT + " integer, " +
                COLUMN_DATE_FORMAT + " integer"
                + ")";
    }
}
