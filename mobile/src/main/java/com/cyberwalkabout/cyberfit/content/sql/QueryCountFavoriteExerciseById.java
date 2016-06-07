package com.cyberwalkabout.cyberfit.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.FavoriteExerciseTable;
import com.cyberwalkabout.cyberfit.util.Const;

/**
 * @author Andrii Kovalov
 */
public class QueryCountFavoriteExerciseById extends Query {
    public QueryCountFavoriteExerciseById(Context context, SQLiteDatabase db, Uri uri) {
        super(context, db, uri, null, null, null, null);
    }

    @Override
    protected Cursor query() {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(FavoriteExerciseTable.TABLE_NAME);
        String exerciseId = uri.getQueryParameter(Const.EXERCISE_ID);
        return queryBuilder.query(db, new String[]{"count(*)"}, FavoriteExerciseTable.COLUMN_EXERCISE_ID + " = ?", new String[]{exerciseId}, null, null, null);
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {

    }
}
