package com.cyberwalkabout.cyberfit.content.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.cyberwalkabout.cyberfit.content.UriHelper;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.ExerciseSessionTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.LocationInfoTable;
import com.cyberwalkabout.cyberfit.util.Const;

/**
 * @author Andrii Kovalov
 */
public class UpdateExerciseSession extends Update {
    public UpdateExerciseSession(Context context, SQLiteDatabase db, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        super(context, db, uri, values, selection, selectionArgs);
    }

    @Override
    protected int update() {
        String id = uri.getLastPathSegment();

        int updateResult = db.update(ExerciseSessionTable.TABLE_NAME, values, ExerciseSessionTable.COLUMN_ID + " = ?", new String[]{id});

        if (Boolean.valueOf(uri.getQueryParameter(Const.UPDATE_MAX_AND_AVG_VALUES))) {
            updateAvgAndMaxValues();
        }

        return updateResult;
    }

    private void updateAvgAndMaxValues() {
        String selectAvgAltitudeSql = locationInfoFunctionSql("avg", LocationInfoTable.COLUMN_ALTITUDE);
        String selectTopAltitudeSql = locationInfoFunctionSql("max", LocationInfoTable.COLUMN_ALTITUDE);
        String selectMinAltitudeSql = locationInfoFunctionSql("min", LocationInfoTable.COLUMN_ALTITUDE);
        String selectAvgSpeedSql = locationInfoFunctionSql("avg", LocationInfoTable.COLUMN_SPEED);
        String selectTopSpeedSql = locationInfoFunctionSql("max", LocationInfoTable.COLUMN_SPEED);
        String selectAvgPaceSql = locationInfoFunctionSql("avg", LocationInfoTable.COLUMN_PACE);
        String selectTopPaceSql = locationInfoFunctionSql("max", LocationInfoTable.COLUMN_PACE);

        db.execSQL("update " + ExerciseSessionTable.TABLE_NAME + " set "
                + ExerciseSessionTable.COLUMN_AVG_ALTITUDE + " = (" + selectAvgAltitudeSql + "), "
                + ExerciseSessionTable.COLUMN_TOP_ALTITUDE + " = (" + selectTopAltitudeSql + "), "
                + ExerciseSessionTable.COLUMN_AVG_SPEED + " = (" + selectAvgSpeedSql + "), "
                + ExerciseSessionTable.COLUMN_TOP_SPEED + " = (" + selectTopSpeedSql + "), "
                + ExerciseSessionTable.COLUMN_AVG_PACE + " = (" + selectAvgPaceSql + "), "
                + ExerciseSessionTable.COLUMN_TOP_PACE + " = (" + selectTopPaceSql + "), "
                + ExerciseSessionTable.COLUMN_LOWEST_ALTITUDE + " = (" + selectMinAltitudeSql + ")");
    }

    @Override
    protected void notifyChange() {
        context.getContentResolver().notifyChange(UriHelper.getInstance().allExercises(), null);
        context.getContentResolver().notifyChange(uri, null);
    }

    private String locationInfoFunctionSql(String function, String column) {
        return "select " + function + "(" + column + ") from " + LocationInfoTable.TABLE_NAME + " where " + LocationInfoTable.COLUMN_EXERCISE_SESSION_ID + " = " + uri.getLastPathSegment();
    }
}
