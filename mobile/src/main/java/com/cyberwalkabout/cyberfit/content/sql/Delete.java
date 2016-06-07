package com.cyberwalkabout.cyberfit.content.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * @author Andrii Kovalov
 */
public abstract class Delete {

    protected Context context;
    protected SQLiteDatabase db;

    protected Uri uri;
    protected String selection;
    protected String[] selectionArgs;

    public Delete(Context context, SQLiteDatabase db, Uri uri, String selection, String[] selectionArgs) {
        this.context = context;
        this.db = db;
        this.uri = uri;
        this.selection = selection;
        this.selectionArgs = selectionArgs;
    }

    public int execute() {
        int result = delete();
        notifyChange();
        return result;
    }

    protected abstract int delete();

    protected abstract void notifyChange();
}
