package com.warriorfitapp.mobile.content.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.warriorfitapp.db.sqlite.schema.table.ExerciseSessionTable;
import com.warriorfitapp.mobile.content.UriHelper;

/**
 * @author Andrii Kovalov
 */
public class DeleteExerciseSessionById extends Delete {
    public DeleteExerciseSessionById(Context context, SQLiteDatabase db, Uri uri, String selection, String[] selectionArgs) {
        super(context, db, uri, selection, selectionArgs);
    }

    @Override
    public int delete() {
        String exerciseSessionId = uri.getLastPathSegment();
        return db.delete(ExerciseSessionTable.TABLE_NAME, ExerciseSessionTable.COLUMN_ID + " = ?", new String[]{exerciseSessionId});
    }

    @Override
    protected void notifyChange() {
        context.getContentResolver().notifyChange(UriHelper.getInstance().allExerciseSessions(), null);
    }
}
