package com.warriorfitapp.mobile.content.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.warriorfitapp.db.sqlite.schema.table.SubscribedProgramTable;
import com.warriorfitapp.mobile.content.UriHelper;

/**
 * @author Andrii Kovalov
 */
public class InsertSubscribedProgramId extends Insert {
    public InsertSubscribedProgramId(Context context, SQLiteDatabase db, Uri uri, ContentValues values) {
        super(context, db, uri, values);
    }

    @Override
    public Uri insert() {
        ContentValues values;
        String id = uri.getLastPathSegment();

        int count = countSubscribedProgramById(uri);

        if (count == 0) {
            values = new ContentValues();
            values.put(SubscribedProgramTable.COLUMN_PROGRAM_ID, id);

            db.insert(SubscribedProgramTable.TABLE_NAME, null, values);
        }

        return uri;
    }

    @Override
    public void notifyChange() {
        context.getContentResolver().notifyChange(UriHelper.getInstance().allProgramsWithAuthors(null), null);
        context.getContentResolver().notifyChange(UriHelper.getInstance().allExercises(), null);
    }


    private int countSubscribedProgramById(Uri uri) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(SubscribedProgramTable.TABLE_NAME);

        int count = 0;
        Cursor cursor = null;
        try {
            cursor = new QueryCountSubscribedProgramById(context, db, uri).execute();
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
