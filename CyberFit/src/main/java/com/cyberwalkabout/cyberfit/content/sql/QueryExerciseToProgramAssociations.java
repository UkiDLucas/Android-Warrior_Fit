package com.cyberwalkabout.cyberfit.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.ExerciseToProgramTable;

/**
 * @author Andrii Kovalov
 */
public class QueryExerciseToProgramAssociations extends Query {
    public QueryExerciseToProgramAssociations(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, db, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public Cursor query() {
        return queryAllRecords(uri, ExerciseToProgramTable.instance(), projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {

    }
}
