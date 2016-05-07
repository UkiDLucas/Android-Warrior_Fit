package com.cyberwalkabout.cyberfit.model.v2.factory;

import android.database.Cursor;

import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.UserTable;
import com.cyberwalkabout.cyberfit.model.v2.AccountType;
import com.cyberwalkabout.cyberfit.model.v2.ModelFactory;
import com.cyberwalkabout.cyberfit.model.v2.User;

/**
 * @author Andrii Kovalov
 */
public class UserCursorFactory implements ModelFactory<User, Cursor> {
    private static final UserCursorFactory INSTANCE = new UserCursorFactory();

    public static UserCursorFactory getInstance() {
        return INSTANCE;
    }

    private UserCursorFactory() {
    }

    @Override
    public User create(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getLong(cursor.getColumnIndex(UserTable.COLUMN_ID)));
        user.setUsername(cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_USERNAME)));
        user.setAccountType(AccountType.valueOf(cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_ACCOUNT_TYPE))));
        user.setDisplayName(cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_DISPLAY_NAME)));

        if (!cursor.isNull(cursor.getColumnIndex(UserTable.COLUMN_BIRTHDAY))) {
            user.setBirthday(cursor.getLong(cursor.getColumnIndex(UserTable.COLUMN_BIRTHDAY)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(UserTable.COLUMN_HEIGHT))) {
            user.setHeight(cursor.getDouble(cursor.getColumnIndex(UserTable.COLUMN_HEIGHT)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(UserTable.COLUMN_WEIGHT))) {
            user.setWeight(cursor.getDouble(cursor.getColumnIndex(UserTable.COLUMN_WEIGHT)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(UserTable.COLUMN_IS_MALE))) {
            user.setIsMale(cursor.getInt(cursor.getColumnIndex(UserTable.COLUMN_IS_MALE)) == 1);
        } else {
            user.setIsMale(true);
        }

        if (!cursor.isNull(cursor.getColumnIndex(UserTable.COLUMN_AGE))) {
            user.setAge(cursor.getInt(cursor.getColumnIndex(UserTable.COLUMN_AGE)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(UserTable.COLUMN_WAIST))) {
            user.setWaist(cursor.getDouble(cursor.getColumnIndex(UserTable.COLUMN_WAIST)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(UserTable.COLUMN_BUTTOCKS))) {
            user.setButtocks(cursor.getDouble(cursor.getColumnIndex(UserTable.COLUMN_BUTTOCKS)));
        }

        user.setActive(cursor.getInt(cursor.getColumnIndex(UserTable.COLUMN_ACTIVE)) == 1);
        user.setImageUri(cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_IMAGE_URI)));
        user.setCurrentBodyFat(cursor.getFloat(cursor.getColumnIndex(UserTable.COLUMN_CURRENT_BODY_FAT)));
        user.setDesiredBodyFat(cursor.getFloat(cursor.getColumnIndex(UserTable.COLUMN_DESIRED_BODY_FAT)));
        return user;
    }
}
