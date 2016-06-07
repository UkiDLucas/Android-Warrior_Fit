package com.cyberwalkabout.cyberfit.content.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.cyberwalkabout.cyberfit.content.UriHelper;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.FavoriteExerciseTable;
import com.cyberwalkabout.cyberfit.util.Const;

/**
 * @author Andrii Kovalov
 */
public class InsertFavoriteExerciseId extends Insert {
    public InsertFavoriteExerciseId(Context context, SQLiteDatabase db, Uri uri, ContentValues values) {
        super(context, db, uri, values);
    }

    @Override
    protected Uri insert() {
        ContentValues values;
        String exerciseId = uri.getQueryParameter(Const.EXERCISE_ID);

        int count = countFavoriteExerciseById(uri);

        if (count == 0) {
            values = new ContentValues();
            values.put(FavoriteExerciseTable.COLUMN_EXERCISE_ID, exerciseId);

            db.insert(FavoriteExerciseTable.TABLE_NAME, null, values);
        }
        return null;
    }

    @Override
    protected void notifyChange() {
        context.getContentResolver().notifyChange(UriHelper.getInstance().allExercises(), null);
    }


    private int countFavoriteExerciseById(Uri uri) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(FavoriteExerciseTable.TABLE_NAME);

        int count = 0;
        Cursor cursor = null;
        try {
            cursor = new QueryCountFavoriteExerciseById(context, db, uri).execute();
            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }
}
