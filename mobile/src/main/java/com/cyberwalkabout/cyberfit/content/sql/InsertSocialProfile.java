package com.cyberwalkabout.cyberfit.content.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.cyberwalkabout.cyberfit.content.UriHelper;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.SocialProfileTable;

/**
 * @author Andrii Kovalov
 */
public class InsertSocialProfile extends Insert {
    private long id;

    public InsertSocialProfile(Context context, SQLiteDatabase db, Uri uri, ContentValues values) {
        super(context, db, uri, values);
    }

    @Override
    protected Uri insert() {
        values.put(SocialProfileTable.COLUMN_DATE_CREATED, System.currentTimeMillis());
        id = db.insert(SocialProfileTable.TABLE_NAME, null, values);
        return uri.buildUpon().appendPath(Long.toString(id)).build();
    }

    @Override
    protected void notifyChange() {
        context.getContentResolver().notifyChange(UriHelper.getInstance().userById(id), null);
    }
}
