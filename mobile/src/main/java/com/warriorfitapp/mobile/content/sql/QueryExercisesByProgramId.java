package com.warriorfitapp.mobile.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.warriorfitapp.db.sqlite.schema.table.ExerciseTable;

/**
 * @author Andrii Kovalov
 */
public class QueryExercisesByProgramId extends Query {
    public QueryExercisesByProgramId(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, db, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public Cursor query() {
        String programId = uri.getLastPathSegment();

        String columnExerciseId = ExerciseTable.instance().qualifiedColumnName(ExerciseTable.COLUMN_ID);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(ExerciseTable.TABLE_NAME + " " + ExerciseTable.TABLE_NAME +
                " inner join " + com.warriorfitapp.db.sqlite.schema.table.ExerciseToProgramTable.TABLE_NAME + " etp" +
                " on etp." + com.warriorfitapp.db.sqlite.schema.table.ExerciseToProgramTable.COLUMN_PROGRAM_ID + " = " + programId +
                " and " + columnExerciseId + " = etp." + com.warriorfitapp.db.sqlite.schema.table.ExerciseToProgramTable.COLUMN_EXERCISE_ID +
                " left join " + com.warriorfitapp.db.sqlite.schema.table.FavoriteExerciseTable.TABLE_NAME + " " + com.warriorfitapp.db.sqlite.schema.table.FavoriteExerciseTable.TABLE_NAME +
                " on " + com.warriorfitapp.db.sqlite.schema.table.FavoriteExerciseTable.instance().qualifiedColumnName(com.warriorfitapp.db.sqlite.schema.table.FavoriteExerciseTable.COLUMN_EXERCISE_ID) + " = " + columnExerciseId);

        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = ExerciseTable.instance().qualifiedColumnName(ExerciseTable.COLUMN_NAME);
        }

        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {
        cursor.setNotificationUri(context.getContentResolver(), uri);
    }
}
