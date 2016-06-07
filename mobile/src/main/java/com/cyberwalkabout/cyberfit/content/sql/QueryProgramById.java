package com.cyberwalkabout.cyberfit.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.ProgramTable;

/**
 * @author Andrii Kovalov
 */
public class QueryProgramById extends Query {
    public QueryProgramById(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, db, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public Cursor query() {
        return queryRecordById(ProgramTable.instance(), projection, uri.getLastPathSegment());
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {

    }
}
