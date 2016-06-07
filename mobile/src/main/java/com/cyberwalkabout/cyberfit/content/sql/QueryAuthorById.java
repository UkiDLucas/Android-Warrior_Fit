package com.cyberwalkabout.cyberfit.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.AuthorTable;

/**
 * @author Andrii Kovalov
 */
public class QueryAuthorById extends Query {
    public QueryAuthorById(Context context, SQLiteDatabase db, Uri uri, String[] projection) {
        super(context, db, uri, projection, null, null, null);
    }

    @Override
    public Cursor query() {
        return queryRecordById(AuthorTable.instance(), projection, uri.getLastPathSegment());
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {

    }
}
