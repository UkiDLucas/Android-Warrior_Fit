package com.warriorfitapp.mobile.content.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.warriorfitapp.mobile.content.UriHelper;
import com.warriorfitapp.mobile.util.Const;

/**
 * @author Andrii Kovalov
 */
public class DeleteFavoriteExerciseId extends Delete {
    public DeleteFavoriteExerciseId(Context context, SQLiteDatabase db, Uri uri, String selection, String[] selectionArgs) {
        super(context, db, uri, selection, selectionArgs);
    }

    @Override
    public int delete() {
        String exerciseId = uri.getQueryParameter(Const.EXERCISE_ID);
        return db.delete(com.warriorfitapp.db.sqlite.schema.table.FavoriteExerciseTable.TABLE_NAME, com.warriorfitapp.db.sqlite.schema.table.FavoriteExerciseTable.COLUMN_EXERCISE_ID + " = ?", new String[]{exerciseId});
    }

    @Override
    protected void notifyChange() {
        context.getContentResolver().notifyChange(UriHelper.getInstance().allExercises(), null);
    }
}
