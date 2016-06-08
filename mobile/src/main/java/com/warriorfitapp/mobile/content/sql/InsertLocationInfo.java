package com.warriorfitapp.mobile.content.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.warriorfitapp.db.sqlite.schema.table.ExerciseSessionTable;
import com.warriorfitapp.db.sqlite.schema.table.LocationInfoTable;
import com.warriorfitapp.mobile.content.UriHelper;

/**
 * @author Andrii Kovalov
 */
public class InsertLocationInfo extends Insert {
    private long id;

    public InsertLocationInfo(Context context, SQLiteDatabase db, Uri uri, ContentValues values) {
        super(context, db, uri, values);
    }

    @Override
    protected Uri insert() {
        values.put(LocationInfoTable.COLUMN_TIMESTAMP, System.currentTimeMillis());
        id = db.insert(LocationInfoTable.TABLE_NAME, null, values);
        return uri.buildUpon().appendPath(Long.toString(id)).build();
    }

    @Override
    protected void notifyChange() {
        long exerciseHistoryRecordId = values.getAsLong(LocationInfoTable.COLUMN_EXERCISE_SESSION_ID);

        Uri notifyUri = UriHelper.getInstance().locationInfo(id).buildUpon()
                .appendQueryParameter(LocationInfoTable.COLUMN_EXERCISE_SESSION_ID, Long.toString(exerciseHistoryRecordId))
                .appendQueryParameter(ExerciseSessionTable.COLUMN_EXERCISE_ID, uri.getQueryParameter(ExerciseSessionTable.COLUMN_EXERCISE_ID))
                .build();
        context.getContentResolver().notifyChange(notifyUri, null);
    }
}
