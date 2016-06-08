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
public class QueryInProgressExerciseSession extends Query {
    public QueryInProgressExerciseSession(Context context, SQLiteDatabase db, Uri uri) {
        super(context, db, uri, null, null, null, null);
    }

    @Override
    protected Cursor query() {
        String exerciseId = uri.getQueryParameter(Const.EXERCISE_ID);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(ExerciseSessionTable.TABLE_NAME);
        return queryBuilder.query(db,
                ExerciseSessionTable.ALL_COLUMNS,
                ExerciseSessionTable.COLUMN_EXERCISE_ID + " = ?" +
                        " and (" + ExerciseSessionTable.COLUMN_STATE + " = ? or " + ExerciseSessionTable.COLUMN_STATE + " = ? or " + ExerciseSessionTable.COLUMN_STATE + " = ?)",
                new String[]{exerciseId, ExerciseState.STARTED.name(), ExerciseState.TIME_RECORDED.name(), ExerciseState.PAUSED.name()},
                null,
                null,
                ExerciseSessionTable.COLUMN_TIMESTAMP_COMPLETED + " desc",
                "1");
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {

    }
}
