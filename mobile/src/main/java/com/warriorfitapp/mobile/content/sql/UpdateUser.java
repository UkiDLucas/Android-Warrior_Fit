package com.warriorfitapp.mobile.content.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.warriorfitapp.db.sqlite.schema.table.UserTable;

/**
 * @author Andrii Kovalov
 */
public class UpdateUser extends Update {
    public UpdateUser(Context context, SQLiteDatabase db, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        super(context, db, uri, values, selection, selectionArgs);
    }

    @Override
    protected int update() {
        String id = uri.getLastPathSegment();

        values.remove(UserTable.COLUMN_ID);

        if (values.containsKey(UserTable.COLUMN_ACTIVE) && values.getAsBoolean(UserTable.COLUMN_ACTIVE)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(UserTable.COLUMN_ACTIVE, 0);
            // deactivate all accounts because this updated one will be activated
            db.update(UserTable.TABLE_NAME, contentValues, null, null);
        }

        return db.update(UserTable.TABLE_NAME, values, UserTable.COLUMN_ID + " = ?", new String[]{id});
    }

    @Override
    protected void notifyChange() {
        context.getContentResolver().notifyChange(uri, null);
    }
}