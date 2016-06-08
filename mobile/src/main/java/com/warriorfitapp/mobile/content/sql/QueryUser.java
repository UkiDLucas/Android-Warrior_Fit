package com.warriorfitapp.mobile.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.database.DatabaseUtilsCompat;

import com.warriorfitapp.mobile.content.UriHelper;

/**
 * @author Andrii Kovalov
 */
public class QueryUser extends Query {
    public QueryUser(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, db, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public Cursor query() {
        selection = DatabaseUtilsCompat.concatenateWhere(selection, com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_ACTIVE + "=1");
        return queryAllRecords(uri, com.warriorfitapp.db.sqlite.schema.table.UserTable.instance(), projection, selection, selectionArgs, null, null, sortOrder, "1");
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {
        cursor.setNotificationUri(context.getContentResolver(), UriHelper.getInstance().user());
    }
}
