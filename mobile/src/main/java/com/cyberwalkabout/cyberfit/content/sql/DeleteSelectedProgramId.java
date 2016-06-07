package com.cyberwalkabout.cyberfit.content.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.cyberwalkabout.cyberfit.content.UriHelper;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.SelectedProgramTable;

/**
 * @author Andrii Kovalov
 */
public class DeleteSelectedProgramId extends Delete {
    public DeleteSelectedProgramId(Context context, SQLiteDatabase db, Uri uri, String selection, String[] selectionArgs) {
        super(context, db, uri, selection, selectionArgs);
    }

    @Override
    protected int delete() {
        String id = uri.getLastPathSegment();
        return db.delete(SelectedProgramTable.TABLE_NAME, SelectedProgramTable.COLUMN_PROGRAM_ID + " = ?", new String[]{id});
    }

    @Override
    protected void notifyChange() {
        context.getContentResolver().notifyChange(UriHelper.getInstance().allExercisesWithProgramNames(null, false), null);
        context.getContentResolver().notifyChange(UriHelper.getInstance().allProgramsSelected(), null);
    }
}
