package com.cyberwalkabout.cyberfit.content.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.cyberwalkabout.cyberfit.content.UriHelper;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.FavoriteExerciseTable;
import com.cyberwalkabout.cyberfit.util.Const;

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
        return db.delete(FavoriteExerciseTable.TABLE_NAME, FavoriteExerciseTable.COLUMN_EXERCISE_ID + " = ?", new String[]{exerciseId});
    }

    @Override
    protected void notifyChange() {
        context.getContentResolver().notifyChange(UriHelper.getInstance().allExercises(), null);
    }
}
