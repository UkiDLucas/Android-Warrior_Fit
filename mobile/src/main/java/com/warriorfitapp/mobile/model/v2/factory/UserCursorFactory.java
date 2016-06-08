package com.warriorfitapp.mobile.model.v2.factory;

import android.database.Cursor;

import com.warriorfitapp.model.v2.User;

/**
 * @author Andrii Kovalov
 */
public class UserCursorFactory implements com.warriorfitapp.model.v2.ModelFactory<User, Cursor> {
    private static final UserCursorFactory INSTANCE = new UserCursorFactory();

    public static UserCursorFactory getInstance() {
        return INSTANCE;
    }

    private UserCursorFactory() {
    }

    @Override
    public com.warriorfitapp.model.v2.User create(Cursor cursor) {
        com.warriorfitapp.model.v2.User user = new com.warriorfitapp.model.v2.User();
        user.setId(cursor.getLong(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_ID)));
        user.setUsername(cursor.getString(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_USERNAME)));
        user.setAccountType(com.warriorfitapp.model.v2.AccountType.valueOf(cursor.getString(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_ACCOUNT_TYPE))));
        user.setDisplayName(cursor.getString(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_DISPLAY_NAME)));

        if (!cursor.isNull(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_BIRTHDAY))) {
            user.setBirthday(cursor.getLong(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_BIRTHDAY)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_HEIGHT))) {
            user.setHeight(cursor.getDouble(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_HEIGHT)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_WEIGHT))) {
            user.setWeight(cursor.getDouble(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_WEIGHT)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_IS_MALE))) {
            user.setIsMale(cursor.getInt(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_IS_MALE)) == 1);
        } else {
            user.setIsMale(true);
        }

        if (!cursor.isNull(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_AGE))) {
            user.setAge(cursor.getInt(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_AGE)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_WAIST))) {
            user.setWaist(cursor.getDouble(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_WAIST)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_BUTTOCKS))) {
            user.setButtocks(cursor.getDouble(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_BUTTOCKS)));
        }

        user.setActive(cursor.getInt(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_ACTIVE)) == 1);
        user.setImageUri(cursor.getString(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_IMAGE_URI)));
        user.setCurrentBodyFat(cursor.getFloat(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_CURRENT_BODY_FAT)));
        user.setDesiredBodyFat(cursor.getFloat(cursor.getColumnIndex(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_DESIRED_BODY_FAT)));
        return user;
    }
}
