package com.cyberwalkabout.cyberfit.content.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.cyberwalkabout.cyberfit.content.UriHelper;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.UserTable;

/**
 * @author Andrii Kovalov
 */
public class DeleteUser extends Delete {
    public DeleteUser(Context context, SQLiteDatabase db, Uri uri, String selection, String[] selectionArgs) {
        super(context, db, uri, selection, selectionArgs);
    }

    @Override
    public int delete() {
        return db.delete(UserTable.TABLE_NAME, null, null);
    }

    @Override
    protected void notifyChange() {
        context.getContentResolver().notifyChange(UriHelper.getInstance().user(), null);
    }
}
