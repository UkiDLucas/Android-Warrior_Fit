package com.warriorfitapp.mobile.content.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * @author Andrii Kovalov
 */
public abstract class Insert {
    protected Context context;
    protected SQLiteDatabase db;

    protected Uri uri;
    protected ContentValues values;

    public Insert(Context context, SQLiteDatabase db, Uri uri, ContentValues values) {
        this.context = context;
        this.db = db;
        this.uri = uri;
        this.values = values;
    }

    public Uri execute() {
        Uri uri = insert();
        notifyChange();
        return uri;
    }

    protected abstract Uri insert();

    protected abstract void notifyChange();
}
