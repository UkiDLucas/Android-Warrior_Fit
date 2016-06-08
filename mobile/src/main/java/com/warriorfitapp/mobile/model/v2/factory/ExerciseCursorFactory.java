package com.warriorfitapp.mobile.model.v2.factory;


import android.database.Cursor;

import com.warriorfitapp.db.sqlite.schema.table.ExerciseTable;
import com.warriorfitapp.model.v2.Exercise;

/**
 * @author Andrii Kovalov
 */
public class ExerciseCursorFactory implements com.warriorfitapp.model.v2.ModelFactory<Exercise, Cursor> {

    private static final ExerciseCursorFactory INSTANCE = new ExerciseCursorFactory();

    public static ExerciseCursorFactory getInstance() {
        return INSTANCE;
    }

    private ExerciseCursorFactory() {
    }

    @Override
    public Exercise create(Cursor cursor) {
        Exercise exercise = new Exercise();
        exercise.setId(cursor.getString(cursor.getColumnIndex(ExerciseTable.COLUMN_ID)));
        exercise.setActive(cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_ACTIVE)) == 1);
        exercise.setDisplayOrder(cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_DISPLAY_ORDER)));
        exercise.setTrackDistance(cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_TRACK_DISTANCE)) == 1);
        exercise.setTrackWeight(cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_TRACK_WEIGHT)) == 1);
        exercise.setTrackTime(cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_TRACK_TIME)) == 1);
        exercise.setTrackRepetitions(cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_TRACK_REPETITIONS)) == 1);
        exercise.setTrackCalories(cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_TRACK_CALORIES)) == 1);
        exercise.setName(cursor.getString(cursor.getColumnIndex(ExerciseTable.COLUMN_NAME)));
        exercise.setDescription(cursor.getString(cursor.getColumnIndex(ExerciseTable.COLUMN_DESCRIPTION)));
        exercise.setYoutubeId(cursor.getString(cursor.getColumnIndex(ExerciseTable.COLUMN_YOUTUBE_ID)));
        exercise.setIgnoreYoutubeText(cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_IGNORE_YOUTUBE_TEXT)) == 1);
        exercise.setMapRequired(cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_MAP_REQUIRED)) == 1);
        return exercise;
    }
}