package com.warriorfitapp.mobile.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.warriorfitapp.mobile.util.Const;

/**
 * @author Andrii Kovalov
 */
public class QueryExerciseById extends Query {
    public QueryExerciseById(Context context, SQLiteDatabase db, Uri uri, String[] projection) {
        super(context, db, uri, projection, null, null, null);
    }

    @Override
    public Cursor query() {
        String exerciseId = uri.getQueryParameter(Const.EXERCISE_ID);
        return queryRecordById(com.warriorfitapp.db.sqlite.schema.table.ExerciseTable.instance(), projection, exerciseId);
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {

    }
}
