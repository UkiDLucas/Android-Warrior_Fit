package com.warriorfitapp.mobile.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.v4.database.DatabaseUtilsCompat;

import com.warriorfitapp.db.sqlite.schema.table.ExerciseSessionTable;
import com.warriorfitapp.db.sqlite.schema.table.ExerciseTable;
import com.warriorfitapp.mobile.util.Const;

/**
 * @author Andrii Kovalov
 */
public class QueryExerciseSessions extends Query {
    private static final String TAG = QueryExerciseSessions.class.getSimpleName();

    public QueryExerciseSessions(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, db, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected Cursor query() {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(ExerciseSessionTable.TABLE_NAME + " " + ExerciseSessionTable.TABLE_NAME +
                " left join " + ExerciseTable.TABLE_NAME + " " + ExerciseTable.TABLE_NAME +
                " on " + ExerciseTable.instance().qualifiedColumnName(ExerciseTable.COLUMN_ID) + " = " + ExerciseSessionTable.instance().qualifiedColumnName(ExerciseSessionTable.COLUMN_EXERCISE_ID));

        if (uri.getQueryParameter(ExerciseSessionTable.COLUMN_STATE) != null) {
            selection = DatabaseUtilsCompat.concatenateWhere(selection, ExerciseSessionTable.COLUMN_STATE + " = ?");
            selectionArgs = DatabaseUtilsCompat.appendSelectionArgs(selectionArgs, new String[]{uri.getQueryParameter(ExerciseSessionTable.COLUMN_STATE)});
        }

        if (Boolean.valueOf(uri.getQueryParameter(Const.TODAY_ONLY))) {
            selection = DatabaseUtilsCompat.concatenateWhere(selection, "datetime(" + ExerciseSessionTable.COLUMN_TIMESTAMP_COMPLETED + " / 1000, 'unixepoch') >= current_date");
        }

        String sortOrder = ExerciseSessionTable.instance().qualifiedColumnName(ExerciseSessionTable.COLUMN_TIMESTAMP_COMPLETED) + " desc";

        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {

    }
}
