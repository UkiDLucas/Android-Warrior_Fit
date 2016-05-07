package com.cyberwalkabout.cyberfit.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.database.DatabaseUtilsCompat;

import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.ExerciseSessionTable;
import com.cyberwalkabout.cyberfit.model.v2.ExerciseState;

/**
 * @author Andrii Kovalov
 */
public class QueryNumberOfExercisesCompletedToday extends Query {
    private static final String TAG = QueryNumberOfExercisesCompletedToday.class.getSimpleName();

    public QueryNumberOfExercisesCompletedToday(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, db, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected Cursor query() {
        selection = "datetime(" + ExerciseSessionTable.COLUMN_TIMESTAMP_COMPLETED + " / 1000, 'unixepoch') >= current_date and " + ExerciseSessionTable.COLUMN_STATE + "=?";
        selectionArgs = new String[]{ExerciseState.DONE.name()};

        if (uri.getQueryParameter(ExerciseSessionTable.COLUMN_USER_ID) != null) {
            selection = DatabaseUtilsCompat.concatenateWhere(selection, ExerciseSessionTable.COLUMN_USER_ID + "=?");
            selectionArgs = DatabaseUtilsCompat.appendSelectionArgs(selectionArgs, new String[]{uri.getQueryParameter(ExerciseSessionTable.COLUMN_USER_ID)});
        }

        projection = new String[]{"count(*)"};

        return db.query(ExerciseSessionTable.TABLE_NAME, projection, selection, selectionArgs, null, null, null, null);
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {

    }
}
