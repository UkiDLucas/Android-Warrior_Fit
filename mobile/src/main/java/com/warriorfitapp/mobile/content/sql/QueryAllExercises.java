package com.warriorfitapp.mobile.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.warriorfitapp.db.sqlite.schema.table.ExerciseTable;

/**
 * @author Andrii Kovalov
 */
// TODO: revise it, probably should be removed (no in use by any component)
public class QueryAllExercises extends Query {
    public QueryAllExercises(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, db, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public Cursor query() {
        return queryAllRecords(uri, ExerciseTable.instance(), projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {

    }
}
