package com.warriorfitapp.mobile.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.warriorfitapp.db.sqlite.schema.table.ProgramTable;
import com.warriorfitapp.mobile.content.UriHelper;
import com.warriorfitapp.mobile.util.Const;

/**
 * @author Andrii Kovalov
 */
public class QueryProgramsByExerciseId extends Query {
    public QueryProgramsByExerciseId(Context context, SQLiteDatabase db, Uri uri, String[] projection, String sortOrder) {
        super(context, db, uri, projection, null, null, sortOrder);
    }

    @Override
    public Cursor query() {
        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = ProgramTable.COLUMN_NAME;
        }

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        queryBuilder.setTables(
                com.warriorfitapp.db.sqlite.schema.table.ExerciseToProgramTable.TABLE_NAME + " etp" +
                        " inner join " + ProgramTable.TABLE_NAME + " " + ProgramTable.TABLE_NAME +
                        " on " + ProgramTable.instance().qualifiedColumnName(ProgramTable.COLUMN_ID) + " = etp." + com.warriorfitapp.db.sqlite.schema.table.ExerciseToProgramTable.COLUMN_PROGRAM_ID);

        selection = "etp." + com.warriorfitapp.db.sqlite.schema.table.ExerciseToProgramTable.COLUMN_EXERCISE_ID + " = ?";
        String exerciseId = uri.getQueryParameter(Const.EXERCISE_ID);
        selectionArgs = new String[]{exerciseId};

        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {
        cursor.setNotificationUri(context.getContentResolver(), UriHelper.getInstance().allProgramsSelected());
    }
}
