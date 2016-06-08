package com.warriorfitapp.mobile.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * @author Andrii Kovalov
 */
public class QueryExerciseToProgramAssociations extends Query {
    public QueryExerciseToProgramAssociations(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, db, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public Cursor query() {
        return queryAllRecords(uri, com.warriorfitapp.db.sqlite.schema.table.ExerciseToProgramTable.instance(), projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {

    }
}
