package com.cyberwalkabout.cyberfit.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.LocationInfoTable;

/**
 * @author Andrii Kovalov
 */
public class QueryLocationInfoById extends Query {
    public QueryLocationInfoById(Context context, SQLiteDatabase db, Uri uri, String[] projection) {
        super(context, db, uri, projection, null, null, null);
    }

    @Override
    public Cursor query() {
        return queryRecordById(LocationInfoTable.instance(), projection, uri.getLastPathSegment());
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {

    }
}
