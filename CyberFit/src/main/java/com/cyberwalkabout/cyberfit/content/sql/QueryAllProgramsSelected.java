package com.cyberwalkabout.cyberfit.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.cyberwalkabout.cyberfit.content.UriHelper;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.AuthorTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.ExerciseSessionTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.ExerciseToProgramTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.ProgramTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.SelectedProgramTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.SubscribedProgramTable;
import com.google.common.collect.ObjectArrays;

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
                " join " + ExerciseToProgramTable.TABLE_NAME + " etp" +
                " on etp." + ExerciseToProgramTable.COLUMN_EXERCISE_ID + " = es." + ExerciseSessionTable.COLUMN_EXERCISE_ID +
                " and etp." + ExerciseToProgramTable.COLUMN_PROGRAM_ID + " = " + ProgramTable.instance().qualifiedColumnName(ProgramTable.COLUMN_ID) + ") as " + ProgramTable.ALIAS_EXERCISES_COMPLETED_PER_PROGRAM;

        projection = ObjectArrays.concat(sql, projection);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(ProgramTable.TABLE_NAME + " " + ProgramTable.TABLE_NAME +
                " inner join " + SubscribedProgramTable.TABLE_NAME + " " + SubscribedProgramTable.TABLE_NAME +
                " on (" + ProgramTable.instance().qualifiedColumnName(ProgramTable.COLUMN_ID) + " = " + SubscribedProgramTable.instance().qualifiedColumnName(SubscribedProgramTable.COLUMN_PROGRAM_ID) + ")" +
                " left join " + SelectedProgramTable.TABLE_NAME + " " + SelectedProgramTable.TABLE_NAME +
                " on (" + ProgramTable.instance().qualifiedColumnName(ProgramTable.COLUMN_ID) + " = " + SelectedProgramTable.instance().qualifiedColumnName(SelectedProgramTable.COLUMN_PROGRAM_ID) + ")" +
                " left join " + AuthorTable.TABLE_NAME + " " + AuthorTable.TABLE_NAME +
                " on (" + ProgramTable.instance().qualifiedColumnName(ProgramTable.COLUMN_AUTHOR_ID) + " = " + AuthorTable.instance().qualifiedColumnName(AuthorTable.COLUMN_ID) + ")");

        cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        return cursor;
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {
        cursor.setNotificationUri(context.getContentResolver(), UriHelper.getInstance().allProgramsSelected());
    }
}
