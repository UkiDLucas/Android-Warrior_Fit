package com.cyberwalkabout.cyberfit.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.SubscribedProgramTable;

/**
 * @author Andrii Kovalov
 */
public class QueryCountSubscribedProgramById extends Query {
    public QueryCountSubscribedProgramById(Context context, SQLiteDatabase db, Uri uri) {
        super(context, db, uri, null, null, null, null);
    }

    @Override
    public Cursor query() {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(SubscribedProgramTable.TABLE_NAME);

        return queryBuilder.query(db, new String[]{"count(*)"}, SubscribedProgramTable.COLUMN_PROGRAM_ID + " = ?", new String[]{uri.getLastPathSegment()}, null, null, null);
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {

    }
}
