package com.warriorfitapp.mobile.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * @author Andrii Kovalov
 */
public class QueryAuthorById extends Query {
    public QueryAuthorById(Context context, SQLiteDatabase db, Uri uri, String[] projection) {
        super(context, db, uri, projection, null, null, null);
    }

    @Override
    public Cursor query() {
        return queryRecordById(com.warriorfitapp.db.sqlite.schema.table.AuthorTable.instance(), projection, uri.getLastPathSegment());
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {

    }
}
