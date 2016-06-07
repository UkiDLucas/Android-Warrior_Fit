package com.cyberwalkabout.cyberfit.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.cyberwalkabout.cyberfit.content.CyberFitUriMatcher;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.ExerciseSessionTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.ExerciseTable;
import com.cyberwalkabout.cyberfit.model.v2.ExerciseState;
import com.google.common.base.Joiner;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Andrii Kovalov
 */
public class QueryMapExerciseHistory extends Query {
    private static final String TAG = QueryMapExerciseHistory.class.getSimpleName();

    public QueryMapExerciseHistory(Context context, SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, db, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected Cursor query() {
        boolean inProgress = Boolean.valueOf(uri.getQueryParameter(CyberFitUriMatcher.URI_SEGMENT_IN_PROGRESS));
        boolean paused = Boolean.valueOf(uri.getQueryParameter(CyberFitUriMatcher.URI_SEGMENT_PAUSED));

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(ExerciseSessionTable.TABLE_NAME + " " + ExerciseSessionTable.TABLE_NAME +
                " left join " + ExerciseTable.TABLE_NAME + " " + ExerciseTable.TABLE_NAME +
                " on " + ExerciseTable.instance().qualifiedColumnName(ExerciseTable.COLUMN_ID) + " = " + ExerciseSessionTable.instance().qualifiedColumnName(ExerciseSessionTable.COLUMN_EXERCISE_ID) +
                " and " + ExerciseTable.instance().qualifiedColumnName(ExerciseTable.COLUMN_MAP_REQUIRED) + " = 1");

        Set<String> states = new HashSet<String>();

        if (inProgress) {
            states.add("'" + ExerciseState.STARTED.name() + "'");
            states.add("'" + ExerciseState.TIME_RECORDED.name() + "'");
        }

        if (paused) {
            states.add("'" + ExerciseState.PAUSED + "'");
        }

        selection = ExerciseSessionTable.COLUMN_STATE + " in (" + Joiner.on(",").join(states) + ")";

        return queryBuilder.query(db, projection, selection, null, null, null, ExerciseSessionTable.instance().qualifiedColumnName(ExerciseSessionTable.COLUMN_TIMESTAMP_COMPLETED) + " desc");
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {

    }
}
