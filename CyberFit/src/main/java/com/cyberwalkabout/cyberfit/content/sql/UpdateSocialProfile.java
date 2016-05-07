package com.cyberwalkabout.cyberfit.content.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.SocialProfileTable;

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
        if (values.getAsBoolean(SocialProfileTable.COLUMN_IS_PRIMARY)) {
            Long userId = values.getAsLong(SocialProfileTable.COLUMN_USER_ID);
            if (userId == null) {
                userId = queryUserId();
            }

            if (userId != null) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(SocialProfileTable.COLUMN_IS_PRIMARY, 1);
                db.update(SocialProfileTable.TABLE_NAME, contentValues, SocialProfileTable.COLUMN_USER_ID + " = ?", new String[]{id});
            }
        }

        values.remove(SocialProfileTable.COLUMN_ID);
        return db.update(SocialProfileTable.TABLE_NAME, values, SocialProfileTable.COLUMN_ID + " = ?", new String[]{id});
    }

    private Long queryUserId() {
        String id = uri.getLastPathSegment();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        Cursor cursor = null;
        queryBuilder.setTables(SocialProfileTable.TABLE_NAME);
        try {
            cursor = queryBuilder.query(db, new String[]{SocialProfileTable.COLUMN_USER_ID}, SocialProfileTable.COLUMN_ID + "=" + id, null, null, null, null);
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