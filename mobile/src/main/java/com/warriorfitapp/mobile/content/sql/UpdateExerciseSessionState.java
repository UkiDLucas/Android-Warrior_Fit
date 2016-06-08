package com.warriorfitapp.mobile.content.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.warriorfitapp.db.sqlite.schema.table.ExerciseSessionTable;
import com.warriorfitapp.mobile.content.UriHelper;
import com.warriorfitapp.mobile.util.Const;
import com.warriorfitapp.model.v2.ExerciseState;

/**
 * @author Andrii Kovalov
 */
public class UpdateExerciseSessionState extends Update {
    private static final String TAG = UpdateExerciseSessionState.class.getSimpleName();

    public UpdateExerciseSessionState(Context context, SQLiteDatabase db, Uri uri) {
        super(context, db, uri, null, null, null);
    }

    @Override
    protected int update() {
        String id = uri.getLastPathSegment();
        try {
            ExerciseState exerciseState = ExerciseState.valueOf(uri.getQueryParameter(Const.STATE));

            ContentValues values = new ContentValues();
            values.put(ExerciseSessionTable.COLUMN_STATE, exerciseState.name());

            return db.update(ExerciseSessionTable.TABLE_NAME, values, ExerciseSessionTable.COLUMN_ID + " = ?", new String[]{id});
        } catch (Exception e) {
            Log.e(TAG, "couldn't update exercise state", e);
        }
        return 0;
    }

    @Override
    protected void notifyChange() {
        Uri notifyUri = UriHelper.getInstance().exerciseHistoryRecord(Long.valueOf(uri.getLastPathSegment()))
                .buildUpon().appendQueryParameter(Const.STATE, uri.getQueryParameter(Const.STATE)).build();
        context.getContentResolver().notifyChange(notifyUri, null);
        context.getContentResolver().notifyChange(UriHelper.getInstance().allExercises(), null);
    }
}
