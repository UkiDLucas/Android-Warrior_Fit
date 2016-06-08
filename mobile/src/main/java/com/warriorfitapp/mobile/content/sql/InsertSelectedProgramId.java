package com.warriorfitapp.mobile.content.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.warriorfitapp.mobile.content.UriHelper;

/**
 * @author Andrii Kovalov
 */
public class InsertSelectedProgramId extends Insert {
    public InsertSelectedProgramId(Context context, SQLiteDatabase db, Uri uri, ContentValues values) {
        super(context, db, uri, values);
    }

    @Override
    public Uri insert() {
        ContentValues values;
        String id = uri.getLastPathSegment();

        int count = countSelectedProgramById(uri);

        if (count == 0) {
            values = new ContentValues();
            values.put(com.warriorfitapp.db.sqlite.schema.table.SelectedProgramTable.COLUMN_PROGRAM_ID, id);

            db.insert(com.warriorfitapp.db.sqlite.schema.table.SelectedProgramTable.TABLE_NAME, null, values);
        }

        return uri;
    }

    @Override
    public void notifyChange() {
        context.getContentResolver().notifyChange(UriHelper.getInstance().allExercises(), null);
        context.getContentResolver().notifyChange(UriHelper.getInstance().allProgramsSelected(), null);
    }

    private int countSelectedProgramById(Uri uri) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(com.warriorfitapp.db.sqlite.schema.table.SelectedProgramTable.TABLE_NAME);

        int count = 0;
        Cursor cursor = null;
        try {
            cursor = queryBuilder.query(db, new String[]{"count(*)"}, com.warriorfitapp.db.sqlite.schema.table.SelectedProgramTable.COLUMN_PROGRAM_ID + " = ?", new String[]{uri.getLastPathSegment()}, null, null, null);

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
