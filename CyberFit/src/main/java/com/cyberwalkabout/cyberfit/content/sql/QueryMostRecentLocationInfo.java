package com.cyberwalkabout.cyberfit.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.LocationInfoTable;

/**
 * @author Andrii Kovalov
 */
public class QueryMostRecentLocationInfo extends Query {
    public QueryMostRecentLocationInfo(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, db, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected Cursor query() {
        String exerciseSessionId = uri.getQueryParameter(LocationInfoTable.COLUMN_EXERCISE_SESSION_ID);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(LocationInfoTable.TABLE_NAME);

        return queryBuilder.query(db,
                projection,
                LocationInfoTable.COLUMN_EXERCISE_SESSION_ID + " = ?",
                new String[]{exerciseSessionId},
                null,
                null,
                LocationInfoTable.COLUMN_TIMESTAMP + " desc",
                "1");
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {

    }
}
