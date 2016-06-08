package com.warriorfitapp.mobile.content.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.warriorfitapp.db.sqlite.schema.table.UserTable;

/**
 * @author Andrii Kovalov
 */
public class InsertUser extends Insert {
    private long id;

    public InsertUser(Context context, SQLiteDatabase db, Uri uri, ContentValues values) {
        super(context, db, uri, values);
    }

    @Override
    protected Uri insert() {
        if (values.containsKey(UserTable.COLUMN_ACTIVE) && values.getAsBoolean(UserTable.COLUMN_ACTIVE)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(UserTable.COLUMN_ACTIVE, 0);
            // deactivate all accounts because this updated one will be activated
            db.update(UserTable.TABLE_NAME, contentValues, null, null);
        }

        values.put(UserTable.COLUMN_DATE_CREATED, System.currentTimeMillis());
        id = db.insert(UserTable.TABLE_NAME, null, values);
        uri = uri.buildUpon().appendPath(Long.toString(id)).build();
        return uri;
    }

    @Override
    protected void notifyChange() {
        context.getContentResolver().notifyChange(uri, null);
    }
}
