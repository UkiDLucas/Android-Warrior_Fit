package com.warriorfitapp.mobile.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.warriorfitapp.db.sqlite.schema.table.ExerciseSessionTable;
import com.warriorfitapp.mobile.util.Const;
import com.warriorfitapp.model.v2.ExerciseState;

/**
 * @author Andrii Kovalov
 */
public class QueryMostRecentCompletedExerciseSession extends Query {
    public QueryMostRecentCompletedExerciseSession(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, db, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected Cursor query() {
        String exerciseId = uri.getQueryParameter(Const.EXERCISE_ID);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(ExerciseSessionTable.TABLE_NAME);

        /*queryBuilder.setTables(ExerciseSessionTable.TABLE_NAME + " " + ExerciseSessionTable.TABLE_NAME +
                " left join " + ExerciseTable.TABLE_NAME + " " + ExerciseTable.TABLE_NAME +
                " on " + ExerciseTable.getInstance().qualifiedColumnName(ExerciseTable.COLUMN_ID) + " = " + ExerciseSessionTable.getInstance().qualifiedColumnName(ExerciseSessionTable.COLUMN_EXERCISE_ID));*/


        return queryBuilder.query(db,
                projection,
                ExerciseSessionTable.COLUMN_EXERCISE_ID + " = ? and " + ExerciseSessionTable.COLUMN_STATE + " = ?",
                new String[]{exerciseId, ExerciseState.DONE.name()},
                null,
                null,
                ExerciseSessionTable.COLUMN_TIMESTAMP_COMPLETED + " desc",
                "1");
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {

    }
}
