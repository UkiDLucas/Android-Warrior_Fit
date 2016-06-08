package com.warriorfitapp.mobile.model.v2.factory;

import android.database.Cursor;

import com.warriorfitapp.db.sqlite.schema.table.ExerciseSessionTable;
import com.warriorfitapp.model.v2.ExerciseSession;
import com.warriorfitapp.model.v2.ExerciseState;
import com.warriorfitapp.model.v2.ModelFactory;

/**
 * @author Andrii Kovalov
 */
public class ExerciseSessionCursorFactory implements ModelFactory<ExerciseSession, Cursor> {

    private static final ExerciseSessionCursorFactory INSTANCE = new ExerciseSessionCursorFactory();

    public static ExerciseSessionCursorFactory getInstance() {
        return INSTANCE;
    }

    private ExerciseSessionCursorFactory() {
    }

    @Override
    public ExerciseSession create(Cursor cursor) {
        ExerciseSession exerciseSession = new ExerciseSession();
        exerciseSession.setId(cursor.getLong(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_ID)));
        exerciseSession.setDistance(cursor.getDouble(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_DISTANCE)));
        exerciseSession.setTime(cursor.getLong(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_TIME)));

        exerciseSession.setRepetitions(cursor.getInt(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_REPETITIONS)));
        exerciseSession.setWeight(cursor.getDouble(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_DISTANCE)));

        exerciseSession.setExerciseId(cursor.getString(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_EXERCISE_ID)));
        exerciseSession.setState(ExerciseState.valueOf(cursor.getString(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_STATE))));

        exerciseSession.setTimestampCompleted(cursor.getLong(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_TIMESTAMP_COMPLETED)));

        exerciseSession.setTimestampStarted(cursor.getLong(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_TIMESTAMP_STARTED)));
        exerciseSession.setLastTimestampStarted(cursor.getLong(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_LAST_TIMESTAMP_STARTED)));

        exerciseSession.setAvgPace(cursor.getDouble(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_AVG_PACE)));
        exerciseSession.setTopPace(cursor.getDouble(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_TOP_PACE)));
        exerciseSession.setAvgAltitude(cursor.getDouble(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_AVG_ALTITUDE)));
        exerciseSession.setTopAltitude(cursor.getDouble(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_TOP_ALTITUDE)));
        exerciseSession.setLowestAltitude(cursor.getDouble(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_LOWEST_ALTITUDE)));
        exerciseSession.setAvgSpeed(cursor.getDouble(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_AVG_SPEED)));
        exerciseSession.setTopSpeed(cursor.getDouble(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_TOP_SPEED)));

        exerciseSession.setUserId(cursor.getLong(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_USER_ID)));
        exerciseSession.setUserNote(cursor.getString(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_USER_NOTE)));
        return exerciseSession;
    }
}
