package com.warriorfitapp.mobile.content.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * @author Andrii Kovalov
 */
public class UpdateSocialProfile extends Update {
    public UpdateSocialProfile(Context context, SQLiteDatabase db, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        super(context, db, uri, values, selection, selectionArgs);
    }

    @Override
    protected int update() {
        String id = uri.getLastPathSegment();

        // TODO: convert to trigger
        if (values.getAsBoolean(com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.COLUMN_IS_PRIMARY)) {
            Long userId = values.getAsLong(com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.COLUMN_USER_ID);
            if (userId == null) {
                userId = queryUserId();
            }

            if (userId != null) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.COLUMN_IS_PRIMARY, 1);
                db.update(com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.TABLE_NAME, contentValues, com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.COLUMN_USER_ID + " = ?", new String[]{id});
            }
        }

        values.remove(com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.COLUMN_ID);
        return db.update(com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.TABLE_NAME, values, com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.COLUMN_ID + " = ?", new String[]{id});
    }

    private Long queryUserId() {
        String id = uri.getLastPathSegment();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        Cursor cursor = null;
        queryBuilder.setTables(com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.TABLE_NAME);
        try {
            cursor = queryBuilder.query(db, new String[]{com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.COLUMN_USER_ID}, com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.COLUMN_ID + "=" + id, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getLong(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    @Override
    protected void notifyChange() {
        context.getContentResolver().notifyChange(uri, null);
    }
}