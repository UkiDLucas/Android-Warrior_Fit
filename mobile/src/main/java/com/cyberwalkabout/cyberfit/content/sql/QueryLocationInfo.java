package com.cyberwalkabout.cyberfit.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.LocationInfoTable;
import com.google.common.base.Splitter;

import java.util.Iterator;

/**
 * @author Andrii Kovalov
 */
public class QueryLocationInfo extends Query {
    public QueryLocationInfo(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, db, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected Cursor query() {
        String exerciseHistoryRecordId = uri.getQueryParameter(LocationInfoTable.COLUMN_EXERCISE_SESSION_ID);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(LocationInfoTable.TABLE_NAME);

        String where = buildWhereClause();

        return queryBuilder.query(db,
                projection,
                where,
                new String[]{exerciseHistoryRecordId},
                null,
                null,
                sortOrder,
                null);
    }

    @NonNull
    private String buildWhereClause() {
        String where = LocationInfoTable.COLUMN_EXERCISE_SESSION_ID + " = ?";

        String typesParam = uri.getQueryParameter(LocationInfoTable.COLUMN_TYPE);

        Iterable<String> types;
        if (!TextUtils.isEmpty(typesParam)) {
            types = Splitter.on(",").omitEmptyStrings().trimResults().split(typesParam);

            Iterator<String> iterator = types.iterator();

            if (iterator.hasNext()) {
                where += " and " + LocationInfoTable.COLUMN_TYPE + " in(";
            }

            while (iterator.hasNext()) {
                String type = iterator.next();
                where += "'" + type + "'";
                if (iterator.hasNext()) {
                    where += ",";
                }
            }

            where += ")";
        }
        return where;
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {

    }
}
