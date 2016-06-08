package com.warriorfitapp.mobile.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.warriorfitapp.db.sqlite.schema.table.ExerciseToProgramTable;
import com.warriorfitapp.db.sqlite.schema.table.ProgramTable;
import com.warriorfitapp.mobile.content.UriHelper;
import com.warriorfitapp.mobile.util.Const;

/**
 * @author Andrii Kovalov
 */
public class QueryAuthorByExerciseId extends Query {
    public QueryAuthorByExerciseId(Context context, SQLiteDatabase db, Uri uri, String[] projection, String sortOrder) {
        super(context, db, uri, projection, null, null, sortOrder);
    }

    @Override
    public Cursor query() {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        queryBuilder.setTables(ExerciseToProgramTable.TABLE_NAME + " etp" +
                " inner join " + ProgramTable.TABLE_NAME + " p" +
                " on p." + ProgramTable.COLUMN_ID + " = etp." + ExerciseToProgramTable.COLUMN_PROGRAM_ID +
                " inner join " + com.warriorfitapp.db.sqlite.schema.table.AuthorTable.TABLE_NAME + " " + com.warriorfitapp.db.sqlite.schema.table.AuthorTable.TABLE_NAME +
                " on p." + ProgramTable.COLUMN_AUTHOR_ID + " = " + com.warriorfitapp.db.sqlite.schema.table.AuthorTable.instance().qualifiedColumnName(com.warriorfitapp.db.sqlite.schema.table.AuthorTable.COLUMN_ID));

        selection = "etp." + ExerciseToProgramTable.COLUMN_EXERCISE_ID + " = ?";
        String exerciseId = uri.getQueryParameter(Const.EXERCISE_ID);
        selectionArgs = new String[]{exerciseId};

        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder, "1");
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {
        cursor.setNotificationUri(context.getContentResolver(), UriHelper.getInstance().allProgramsSelected());
    }
}
