package com.cyberwalkabout.cyberfit.content.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.cyberwalkabout.cyberfit.content.UriHelper;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.ExerciseSessionTable;

/**
 * @author Andrii Kovalov
 */
public class InsertExerciseSession extends Insert {
    private long id;

    public InsertExerciseSession(Context context, SQLiteDatabase db, Uri uri, ContentValues values) {
        super(context, db, uri, values);
    }

    @Override
    protected Uri insert() {
        values.put(ExerciseSessionTable.COLUMN_TIMESTAMP_COMPLETED, System.currentTimeMillis());
        id = db.insert(ExerciseSessionTable.TABLE_NAME, null, values);
        return uri.buildUpon().appendPath(Long.toString(id)).build();
    }

    @Override
    protected void notifyChange() {
        context.getContentResolver().notifyChange(UriHelper.getInstance().exerciseHistoryRecord(id), null);
        context.getContentResolver().notifyChange(UriHelper.getInstance().allExercises(), null);
    }
}
