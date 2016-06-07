package com.cyberwalkabout.cyberfit.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.cyberwalkabout.cyberfit.content.UriHelper;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.ProgramTable;

/**
 * @author Andrii Kovalov
 */
public class QueryAllPrograms extends Query {
    public QueryAllPrograms(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, db, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public Cursor query() {
        Cursor cursor;
        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = ProgramTable.COLUMN_NAME;
        }

        String searchByNameKeyword = uri.getQueryParameter(ProgramTable.COLUMN_NAME);
        if (TextUtils.isEmpty(searchByNameKeyword)) {
            cursor = queryAllRecords(uri, ProgramTable.instance(), projection, selection, selectionArgs, null, null, sortOrder);
        } else {
            cursor = queryAllRecords(uri, ProgramTable.instance(), projection,
                    ProgramTable.COLUMN_NAME + " like ?", new String[]{"%" + searchByNameKeyword + "%"},
                    null, null, sortOrder);
        }
        return cursor;
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {
        cursor.setNotificationUri(context.getContentResolver(), UriHelper.getInstance().allPrograms());
    }
}
