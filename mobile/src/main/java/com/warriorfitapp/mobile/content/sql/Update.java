package com.warriorfitapp.mobile.content.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * @author Andrii Kovalov
 */
public abstract class Update {

    protected Context context;
    protected SQLiteDatabase db;

    protected Uri uri;
    protected ContentValues values;

    protected String selection;
    protected String[] selectionArgs;

    public Update(Context context, SQLiteDatabase db, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        this.context = context;
        this.db = db;
        this.uri = uri;
        this.values = values;
        this.selection = selection;
        this.selectionArgs = selectionArgs;
    }

    public int execute() {
        int result = update();
        notifyChange();
        return result;
    }

    protected abstract int update();

    protected abstract void notifyChange();
}
