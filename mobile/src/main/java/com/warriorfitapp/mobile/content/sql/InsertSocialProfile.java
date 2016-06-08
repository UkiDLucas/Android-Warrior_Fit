package com.warriorfitapp.mobile.content.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.warriorfitapp.mobile.content.UriHelper;

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
        values.put(com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.COLUMN_DATE_CREATED, System.currentTimeMillis());
        id = db.insert(com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.TABLE_NAME, null, values);
        return uri.buildUpon().appendPath(Long.toString(id)).build();
    }

    @Override
    protected void notifyChange() {
        context.getContentResolver().notifyChange(UriHelper.getInstance().userById(id), null);
    }
}
