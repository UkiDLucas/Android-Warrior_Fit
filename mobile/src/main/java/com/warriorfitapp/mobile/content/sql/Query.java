package com.warriorfitapp.mobile.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.warriorfitapp.db.sqlite.schema.table.DBTable;

/**
 * @author Andrii Kovalov
 */
public abstract class Query {

    protected Context context;
    protected SQLiteDatabase db;

    protected Uri uri;
    protected String[] projection;
    protected String selection;
    protected String[] selectionArgs;
    protected String sortOrder;

    public Query(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        this.context = context;
        this.db = db;
        this.uri = uri;
        this.projection = projection;
        this.selection = selection;
        this.selectionArgs = selectionArgs;
        this.sortOrder = sortOrder;
    }

    public Cursor execute() {
        Cursor cursor = query();
        setNotificationUri(cursor);
        return cursor;
    }

    protected abstract Cursor query();

    protected abstract void setNotificationUri(Cursor cursor);

    protected Cursor queryAllRecords(Uri uri, DBTable table, String[] projection,
                                     String selection, String[] selectionArgs,
                                     String groupBy, String having, String sortOrder) {
        return queryAllRecords(uri, table, projection, selection, selectionArgs, groupBy, having, sortOrder, null);
    }

    protected Cursor queryAllRecords(Uri uri, DBTable table, String[] projection,
                                     String selection, String[] selectionArgs,
                                     String groupBy, String having, String sortOrder, String limit) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(table.getName());

        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, groupBy, having, sortOrder, limit);
        if (uri != null) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }
        return cursor;
    }

    protected Cursor queryRecordById(DBTable table, String[] projection, String id) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(table.getName());
        return queryBuilder.query(db, projection, DBTable.COLUMN_ID + "=?", new String[]{id}, null, null, null);
    }
}
