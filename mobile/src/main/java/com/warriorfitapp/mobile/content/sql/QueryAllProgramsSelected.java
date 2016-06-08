package com.warriorfitapp.mobile.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.google.common.collect.ObjectArrays;
import com.warriorfitapp.db.sqlite.schema.table.ExerciseSessionTable;
import com.warriorfitapp.db.sqlite.schema.table.ProgramTable;
import com.warriorfitapp.db.sqlite.schema.table.SubscribedProgramTable;
import com.warriorfitapp.mobile.content.UriHelper;

/**
 * @author Andrii Kovalov
 */
public class QueryAllProgramsSelected extends Query {
    public QueryAllProgramsSelected(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, db, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public Cursor query() {
        Cursor cursor;
        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = ProgramTable.ALIAS_EXERCISES_COMPLETED_PER_PROGRAM + " desc, " + ProgramTable.instance().qualifiedColumnName(ProgramTable.COLUMN_NAME);
        }

        String sql = "(select count(*)" +
                " from " + ExerciseSessionTable.TABLE_NAME + " es" +
                " join " + com.warriorfitapp.db.sqlite.schema.table.ExerciseToProgramTable.TABLE_NAME + " etp" +
                " on etp." + com.warriorfitapp.db.sqlite.schema.table.ExerciseToProgramTable.COLUMN_EXERCISE_ID + " = es." + ExerciseSessionTable.COLUMN_EXERCISE_ID +
                " and etp." + com.warriorfitapp.db.sqlite.schema.table.ExerciseToProgramTable.COLUMN_PROGRAM_ID + " = " + ProgramTable.instance().qualifiedColumnName(ProgramTable.COLUMN_ID) + ") as " + ProgramTable.ALIAS_EXERCISES_COMPLETED_PER_PROGRAM;

        projection = ObjectArrays.concat(sql, projection);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(ProgramTable.TABLE_NAME + " " + ProgramTable.TABLE_NAME +
                " inner join " + SubscribedProgramTable.TABLE_NAME + " " + SubscribedProgramTable.TABLE_NAME +
                " on (" + ProgramTable.instance().qualifiedColumnName(ProgramTable.COLUMN_ID) + " = " + SubscribedProgramTable.instance().qualifiedColumnName(SubscribedProgramTable.COLUMN_PROGRAM_ID) + ")" +
                " left join " + com.warriorfitapp.db.sqlite.schema.table.SelectedProgramTable.TABLE_NAME + " " + com.warriorfitapp.db.sqlite.schema.table.SelectedProgramTable.TABLE_NAME +
                " on (" + ProgramTable.instance().qualifiedColumnName(ProgramTable.COLUMN_ID) + " = " + com.warriorfitapp.db.sqlite.schema.table.SelectedProgramTable.instance().qualifiedColumnName(com.warriorfitapp.db.sqlite.schema.table.SelectedProgramTable.COLUMN_PROGRAM_ID) + ")" +
                " left join " + com.warriorfitapp.db.sqlite.schema.table.AuthorTable.TABLE_NAME + " " + com.warriorfitapp.db.sqlite.schema.table.AuthorTable.TABLE_NAME +
                " on (" + ProgramTable.instance().qualifiedColumnName(ProgramTable.COLUMN_AUTHOR_ID) + " = " + com.warriorfitapp.db.sqlite.schema.table.AuthorTable.instance().qualifiedColumnName(com.warriorfitapp.db.sqlite.schema.table.AuthorTable.COLUMN_ID) + ")");

        cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        return cursor;
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {
        cursor.setNotificationUri(context.getContentResolver(), UriHelper.getInstance().allProgramsSelected());
    }
}
