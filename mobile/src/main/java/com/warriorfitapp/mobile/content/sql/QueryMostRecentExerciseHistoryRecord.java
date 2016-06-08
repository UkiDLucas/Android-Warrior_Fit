package com.warriorfitapp.mobile.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.warriorfitapp.db.sqlite.schema.table.ExerciseSessionTable;

/**
 * @author Andrii Kovalov
 */
public class QueryMostRecentExerciseHistoryRecord extends Query {
    public QueryMostRecentExerciseHistoryRecord(Context context, SQLiteDatabase db, Uri uri) {
        super(context, db, uri, null, null, null, null);
    }

    @Override
    protected Cursor query() {
        String exerciseId = uri.getLastPathSegment();

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(ExerciseSessionTable.TABLE_NAME);
        return queryBuilder.query(db,
                ExerciseSessionTable.ALL_COLUMNS,
                ExerciseSessionTable.COLUMN_EXERCISE_ID + " = ?",
                new String[]{exerciseId},
                null,
                null,
                ExerciseSessionTable.COLUMN_TIMESTAMP_COMPLETED + " desc",
                "1");
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {

    }
}
