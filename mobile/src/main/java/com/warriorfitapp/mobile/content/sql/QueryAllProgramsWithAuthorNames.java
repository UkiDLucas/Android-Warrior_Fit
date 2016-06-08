package com.warriorfitapp.mobile.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.warriorfitapp.db.sqlite.schema.table.AuthorTable;
import com.warriorfitapp.db.sqlite.schema.table.ProgramTable;
import com.warriorfitapp.db.sqlite.schema.table.SubscribedProgramTable;
import com.warriorfitapp.mobile.content.UriHelper;

/**
 * @author Andrii Kovalov
 */
public class QueryAllProgramsWithAuthorNames extends Query {
    public QueryAllProgramsWithAuthorNames(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, db, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public Cursor query() {
        Cursor cursor;
        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = ProgramTable.instance().qualifiedColumnName(ProgramTable.COLUMN_NAME);
        }

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(ProgramTable.TABLE_NAME + " " + ProgramTable.TABLE_NAME +
                " left join " + AuthorTable.TABLE_NAME + " " + AuthorTable.TABLE_NAME +
                " on (" + ProgramTable.instance().qualifiedColumnName(ProgramTable.COLUMN_AUTHOR_ID) + " = " + AuthorTable.instance().qualifiedColumnName(AuthorTable.COLUMN_ID) + ")" +
                " left join " + SubscribedProgramTable.TABLE_NAME + " " + SubscribedProgramTable.TABLE_NAME +
                " on (" + ProgramTable.instance().qualifiedColumnName(ProgramTable.COLUMN_ID) + " = " + SubscribedProgramTable.instance().qualifiedColumnName(SubscribedProgramTable.COLUMN_PROGRAM_ID) + ")");

        String searchByNameKeyword = uri.getQueryParameter(ProgramTable.COLUMN_NAME);
        if (!TextUtils.isEmpty(searchByNameKeyword)) {
            selection = ProgramTable.instance().qualifiedColumnName(ProgramTable.COLUMN_NAME) + " like ?";
            selectionArgs = new String[]{"%" + searchByNameKeyword + "%"};
        }

        cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {
        cursor.setNotificationUri(context.getContentResolver(), UriHelper.getInstance().allPrograms());
    }
}
