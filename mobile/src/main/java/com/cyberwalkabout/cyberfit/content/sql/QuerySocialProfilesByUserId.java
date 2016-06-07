package com.cyberwalkabout.cyberfit.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.database.DatabaseUtilsCompat;

import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.SocialProfileTable;

/**
 * @author Andrii Kovalov
 */
public class QuerySocialProfilesByUserId extends Query {
    public QuerySocialProfilesByUserId(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, db, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public Cursor query() {
        String userId = uri.getQueryParameter(SocialProfileTable.COLUMN_USER_ID);
        selection = DatabaseUtilsCompat.concatenateWhere(selection, SocialProfileTable.COLUMN_USER_ID + "=" + userId);
        return queryAllRecords(uri, SocialProfileTable.instance(), projection, selection, selectionArgs, null, null, sortOrder, "1");
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {
        cursor.setNotificationUri(context.getContentResolver(), uri);
    }
}
