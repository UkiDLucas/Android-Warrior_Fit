package com.warriorfitapp.mobile.content.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.ObjectArrays;
import com.warriorfitapp.db.sqlite.schema.table.ExerciseSessionTable;
import com.warriorfitapp.db.sqlite.schema.table.ExerciseTable;
import com.warriorfitapp.db.sqlite.schema.table.ProgramTable;
import com.warriorfitapp.db.sqlite.schema.table.SubscribedProgramTable;
import com.warriorfitapp.mobile.content.UriHelper;
import com.warriorfitapp.model.v2.ExerciseState;

/**
 * @author Andrii Kovalov
 */
public class QueryExercises extends Query {
    private static final String ALIAS_FAVORITES_ORDER = "ALIAS_FAVORITES_ORDER";
    private static final String ALIAS_STATE_ORDER = "ALIAS_STATE_ORDER";
    private static final String ALIAS_NUM_EXERCISE_COMPLETED = "ALIAS_NUM_EXERCISE_COMPLETED";
    private static final String ALIAS_EXERCISE_COMPLETED_TODAY_ORDER = "exercise_finished";

    public QueryExercises(Context context, SQLiteDatabase db, Uri uri) {
        super(context, db, uri, null, null, null, null);
    }

    @Override
    public Cursor query() {
        String columnExerciseId = ExerciseTable.instance().qualifiedColumnName(ExerciseTable.COLUMN_ID);
        String columnFavoriteExerciseId = com.warriorfitapp.db.sqlite.schema.table.FavoriteExerciseTable.instance().qualifiedColumnName(com.warriorfitapp.db.sqlite.schema.table.FavoriteExerciseTable.COLUMN_EXERCISE_ID) + " as " + ExerciseTable.ALIAS_FAVORITE_EXERCISE_ID;

        Boolean favoritesOnly = Boolean.valueOf(uri.getQueryParameter(com.warriorfitapp.db.sqlite.schema.table.FavoriteExerciseTable.TABLE_NAME));

        String selectProgramNamesInnerSql =
                "(select group_concat(p." + ProgramTable.COLUMN_NAME + ", ', ') " +
                        "from " + com.warriorfitapp.db.sqlite.schema.table.ExerciseToProgramTable.TABLE_NAME + " etp " +
                        "left join " + ProgramTable.TABLE_NAME + " p " +
                        "on p." + ProgramTable.COLUMN_ID + " = etp." + com.warriorfitapp.db.sqlite.schema.table.ExerciseToProgramTable.COLUMN_PROGRAM_ID +
                        " and " + columnExerciseId + " = etp." + com.warriorfitapp.db.sqlite.schema.table.ExerciseToProgramTable.COLUMN_EXERCISE_ID + ") as " + ExerciseTable.ALIAS_PROGRAM_NAMES;

        String selectLastActivityTimestampSql =
                "ifnull((select max(" + ExerciseSessionTable.COLUMN_TIMESTAMP_COMPLETED + ")" +
                        " from " + ExerciseSessionTable.TABLE_NAME + " eh" +
                        " where eh." + ExerciseSessionTable.COLUMN_EXERCISE_ID + " = " + columnExerciseId + "), 0)" +
                        " as " + ExerciseTable.ALIAS_LAST_ACTIVITY_TIMESTAMP;

        String selectExerciseInProgressSql =
                "(select count(*)" +
                        " from " + ExerciseSessionTable.TABLE_NAME + " eh" +
                        " where eh." + ExerciseSessionTable.COLUMN_EXERCISE_ID + " = " + columnExerciseId +
                        " and eh." + ExerciseSessionTable.COLUMN_STATE + " in ('" + ExerciseState.STARTED.name() + "','" + ExerciseState.TIME_RECORDED.name() + "','" + ExerciseState.PAUSED.name() + "'))" +
                        " as " + ExerciseTable.ALIAS_EXERCISE_IN_PROGRESS;

        // favorite exercises should be on top
        String selectFavoritesOrder = "(select 1 where " + com.warriorfitapp.db.sqlite.schema.table.FavoriteExerciseTable.instance().qualifiedColumnName(com.warriorfitapp.db.sqlite.schema.table.FavoriteExerciseTable.COLUMN_EXERCISE_ID) + " is not null) as " + ALIAS_FAVORITES_ORDER;

        String selectExerciseFinishedSql =
                "(select case when count(*) > 0 then 1 else 0 end as " + ALIAS_EXERCISE_COMPLETED_TODAY_ORDER +
                        " from " + ExerciseSessionTable.TABLE_NAME + " eh" +
                        " where eh." + ExerciseSessionTable.COLUMN_EXERCISE_ID + " = " + columnExerciseId +
                        " and eh." + ExerciseSessionTable.COLUMN_STATE + " = '" + ExerciseState.DONE.name() + "'" +
                        " and datetime(eh." + ExerciseSessionTable.COLUMN_TIMESTAMP_COMPLETED + " / 1000, 'unixepoch') >= date('now', 'start of day'))" +
                        " as " + ALIAS_EXERCISE_COMPLETED_TODAY_ORDER;

        String countExerciseSessions = "(select count(*) from " + ExerciseSessionTable.TABLE_NAME + " " + ExerciseSessionTable.TABLE_NAME +
                " where " + ExerciseSessionTable.instance().qualifiedColumnName(ExerciseSessionTable.COLUMN_EXERCISE_ID) + "=" + columnExerciseId + ") as " + ALIAS_NUM_EXERCISE_COMPLETED;

        String[] projection = ObjectArrays.concat(ExerciseTable.ALL_COLUMNS_QUALIFIED,
                new String[]{columnFavoriteExerciseId}, String.class);

        String sql = "select " + Joiner.on(",").join(projection) +
                "," + selectProgramNamesInnerSql +
                "," + selectLastActivityTimestampSql +
                "," + selectExerciseInProgressSql +
                "," + selectFavoritesOrder +
                "," + countExerciseSessions +
                "," + selectExerciseFinishedSql +
                " from " + SubscribedProgramTable.TABLE_NAME + " sub" +
                " inner join " + com.warriorfitapp.db.sqlite.schema.table.SelectedProgramTable.TABLE_NAME + " sp on sp." + com.warriorfitapp.db.sqlite.schema.table.SelectedProgramTable.COLUMN_PROGRAM_ID + " = sub." + SubscribedProgramTable.COLUMN_PROGRAM_ID +
                " inner join " + com.warriorfitapp.db.sqlite.schema.table.ExerciseToProgramTable.TABLE_NAME + " etp on sp." + com.warriorfitapp.db.sqlite.schema.table.SelectedProgramTable.COLUMN_PROGRAM_ID + " = etp." + com.warriorfitapp.db.sqlite.schema.table.ExerciseToProgramTable.COLUMN_PROGRAM_ID +
                " left join " + ExerciseTable.TABLE_NAME + " on " + columnExerciseId + " = etp." + com.warriorfitapp.db.sqlite.schema.table.ExerciseToProgramTable.COLUMN_EXERCISE_ID;

        sql += favoritesOnly ? " inner" : " left";

        sql += " join " + com.warriorfitapp.db.sqlite.schema.table.FavoriteExerciseTable.TABLE_NAME + " " + com.warriorfitapp.db.sqlite.schema.table.FavoriteExerciseTable.TABLE_NAME +
                " on " + com.warriorfitapp.db.sqlite.schema.table.FavoriteExerciseTable.instance().qualifiedColumnName(com.warriorfitapp.db.sqlite.schema.table.FavoriteExerciseTable.COLUMN_EXERCISE_ID) + " = " + columnExerciseId;

        String searchByNameKeyword = uri.getQueryParameter(ExerciseTable.COLUMN_NAME);

        String[] selectionArgs = null;
        if (!TextUtils.isEmpty(searchByNameKeyword)) {
            sql += " where " + ExerciseTable.COLUMN_NAME + " like ?";
            selectionArgs = new String[]{"%" + searchByNameKeyword + "%"};
        }

        sql += " group by " + columnExerciseId +
                " order by " +
                ExerciseTable.ALIAS_EXERCISE_IN_PROGRESS + " desc," + // exercises in progress should be on top
                ALIAS_FAVORITES_ORDER + " desc," + // favorite exercises should be on top below exercises in progress
                ALIAS_EXERCISE_COMPLETED_TODAY_ORDER + "," +
                ALIAS_NUM_EXERCISE_COMPLETED + " desc," + // then it follows with exercises that were completed the most
                ExerciseTable.ALIAS_LAST_ACTIVITY_TIMESTAMP + "," + // then it follows with exercises that were exercised recently
                ExerciseTable.instance().qualifiedColumnName(ExerciseTable.COLUMN_NAME); // last sort rule is to sort by name

        return db.rawQuery(sql, selectionArgs);
    }

    @Override
    protected void setNotificationUri(Cursor cursor) {
        cursor.setNotificationUri(context.getContentResolver(), UriHelper.getInstance().allExercises());
    }
}
