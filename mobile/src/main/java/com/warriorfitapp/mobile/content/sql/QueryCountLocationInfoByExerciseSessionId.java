package com.warriorfitapp.mobile.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.warriorfitapp.db.sqlite.schema.table.LocationInfoTable;

/**
 * @author Andrii Kovalov
 */
public class QueryCountLocationInfoByExerciseSessionId extends Query {
    public QueryCountLocationInfoByExerciseSessionId(Context context, SQLiteDatabase db, Uri uri) {
        super(context, db, uri, null, null, null, null);
    }

    @Override
    protected Cursor query() {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(LocationInfoTable.TABLE_NAME);

        return queryBuilder.query(db, new String[]{"count(*)"}, LocationInfoTable.COLUMN_EXERCISE_SESSION_ID + " = ?", new String[]{uri.getLastPathSegment()}, null, null, null);
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {

    }
}
