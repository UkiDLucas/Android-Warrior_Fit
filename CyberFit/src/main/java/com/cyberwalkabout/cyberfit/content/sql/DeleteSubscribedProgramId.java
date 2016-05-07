package com.cyberwalkabout.cyberfit.content.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.cyberwalkabout.cyberfit.content.UriHelper;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.SubscribedProgramTable;

/**
 * @author Andrii Kovalov
 */
public class DeleteSubscribedProgramId extends Delete {
    public DeleteSubscribedProgramId(Context context, SQLiteDatabase db, Uri uri, String selection, String[] selectionArgs) {
        super(context, db, uri, selection, selectionArgs);
    }

    @Override
    public int delete() {
        String id = uri.getLastPathSegment();
        return db.delete(SubscribedProgramTable.TABLE_NAME, SubscribedProgramTable.COLUMN_PROGRAM_ID + " = ?", new String[]{id});
    }

    @Override
    protected void notifyChange() {
        context.getContentResolver().notifyChange(UriHelper.getInstance().allProgramsWithAuthors(null), null);
        context.getContentResolver().notifyChange(UriHelper.getInstance().allExercises(), null);
    }
}
