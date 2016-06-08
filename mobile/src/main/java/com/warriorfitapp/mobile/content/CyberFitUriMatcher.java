package com.warriorfitapp.mobile.content;

import android.content.UriMatcher;

import com.warriorfitapp.db.sqlite.schema.table.ExerciseSessionTable;
import com.warriorfitapp.db.sqlite.schema.table.ProgramTable;

/**
 * @author Andrii Kovalov
 */
public class CyberFitUriMatcher extends UriMatcher {

    public static final int ALL_PROGRAMS = 1;
    public static final int PROGRAM_BY_ID = 2;
    public static final int ALL_AUTHORS = 3;
    public static final int AUTHOR_BY_ID = 4;
    public static final int ALL_EXERCISES = 5;
    public static final int ALL_EXERCISE_TO_PROGRAM_ASSOCIATIONS = 7;
    public static final int ALL_PROGRAMS_WITH_AUTHORS = 8;
    public static final int ALL_EXERCISES_WITH_PROGRAM_NAMES = 9;
    public static final int ALL_PROGRAMS_SELECTED = 10;
    public static final int SELECTED_PROGRAM = 11;
    public static final int EXERCISES_BY_PROGRAM_ID = 12;
    public static final int SUBSCRIBED_PROGRAM = 13;
    public static final int FAVORITE_EXERCISE = 14;
    public static final int EXERCISE_HISTORY_RECORDS = 15;
    public static final int EXERCISE_SESSION = 16;
    public static final int MOST_RECENT_EXERCISE_HISTORY_RECORD = 17;
    public static final int EXERCISE_HISTORY_RECORD_IN_PROGRESS = 18;
    public static final int MOST_RECENT_EXERCISE_COMPLETED_HISTORY_RECORD = 19;
    public static final int EXERCISE_HISTORY_RECORDS_FOR_SHARING = 20;
    public static final int PROGRAMS_BY_EXERCISE_ID = 21;
    public static final int AUTHOR_BY_EXERCISE_ID = 22;
    public static final int EXERCISE_SESSION_STATE = 23;
    public static final int MOST_RECENT_LOCATION_INFO_STATE = 24;
    public static final int EXERCISE_HISTORY_RECORD_COMPLETED = 25;
    public static final int MAP_EXERCISE_HISTORY_RECORD = 26;
    public static final int COUNT_LOCATION_INFO_BY_EXERCISE_HISTORY_RECORD_ID = 27;
    public static final int LOCATION_INFO = 28;
    public static final int LOCATION_INFO_BY_ID = 29;
    public static final int MOST_RECENT_LOCATION_INFO_FOR_EXERCISE = 30;
    public static final int USER = 31;
    public static final int USER_BY_ID = 32;
    public static final int SOCIAL_PROFILE = 33;
    public static final int SOCIAL_PROFILE_BY_ID = 34;
    public static final int NUM_OF_COMPLETED_EXERCISES_TODAY = 35;


    public static final String URI_SEGMENT_MOST_RECENT = "mostRecent";
    public static final String URI_SEGMENT_MOST_RECENT_COMPLETED = "mostRecentCompleted";
    public static final String URI_SEGMENT_COUNT = "count";
    public static final String URI_SEGMENT_DONE = "done";
    public static final String URI_SEGMENT_SHARE = "share";
    public static final String URI_SEGMENT_IN_PROGRESS = "inProgress";
    public static final String URI_SEGMENT_PAUSED = "paused";
    public static final String URI_SEGMENT_JOIN = "join";
    public static final String URI_SEGMENT_NUM_COMPLETED_TODAY = "numCompletedToday";
    public static final String URI_SEGMENT_COMPLETED = "completed";

    public CyberFitUriMatcher() {
        super(NO_MATCH);
        addURI(CyberFitContentProvider.AUTHORITY, ProgramTable.TABLE_NAME, ALL_PROGRAMS);
        addURI(CyberFitContentProvider.AUTHORITY, ProgramTable.TABLE_NAME + "/#", PROGRAM_BY_ID);
        addURI(CyberFitContentProvider.AUTHORITY, ProgramTable.TABLE_NAME + "/" + com.warriorfitapp.db.sqlite.schema.table.SelectedProgramTable.TABLE_NAME, ALL_PROGRAMS_SELECTED);
        addURI(CyberFitContentProvider.AUTHORITY, com.warriorfitapp.db.sqlite.schema.table.AuthorTable.TABLE_NAME, ALL_AUTHORS);
        addURI(CyberFitContentProvider.AUTHORITY, com.warriorfitapp.db.sqlite.schema.table.AuthorTable.TABLE_NAME + "/#", AUTHOR_BY_ID);
        addURI(CyberFitContentProvider.AUTHORITY, com.warriorfitapp.db.sqlite.schema.table.ExerciseTable.TABLE_NAME, ALL_EXERCISES);
        addURI(CyberFitContentProvider.AUTHORITY, com.warriorfitapp.db.sqlite.schema.table.ExerciseTable.TABLE_NAME + "/" + ProgramTable.TABLE_NAME + "/#", EXERCISES_BY_PROGRAM_ID);
        addURI(CyberFitContentProvider.AUTHORITY, com.warriorfitapp.db.sqlite.schema.table.ExerciseToProgramTable.TABLE_NAME, ALL_EXERCISE_TO_PROGRAM_ASSOCIATIONS);
        addURI(CyberFitContentProvider.AUTHORITY, ProgramTable.TABLE_NAME + "/" + URI_SEGMENT_JOIN + "/" + com.warriorfitapp.db.sqlite.schema.table.AuthorTable.TABLE_NAME, ALL_PROGRAMS_WITH_AUTHORS);
        addURI(CyberFitContentProvider.AUTHORITY, com.warriorfitapp.db.sqlite.schema.table.ExerciseTable.TABLE_NAME + "/" + URI_SEGMENT_JOIN + "/" + com.warriorfitapp.db.sqlite.schema.table.ExerciseTable.ALIAS_PROGRAM_NAMES, ALL_EXERCISES_WITH_PROGRAM_NAMES);
        addURI(CyberFitContentProvider.AUTHORITY, com.warriorfitapp.db.sqlite.schema.table.SelectedProgramTable.TABLE_NAME + "/#", SELECTED_PROGRAM);
        addURI(CyberFitContentProvider.AUTHORITY, com.warriorfitapp.db.sqlite.schema.table.SubscribedProgramTable.TABLE_NAME + "/#", SUBSCRIBED_PROGRAM);
        addURI(CyberFitContentProvider.AUTHORITY, com.warriorfitapp.db.sqlite.schema.table.FavoriteExerciseTable.TABLE_NAME, FAVORITE_EXERCISE);
        addURI(CyberFitContentProvider.AUTHORITY, ExerciseSessionTable.TABLE_NAME, EXERCISE_HISTORY_RECORDS);
        addURI(CyberFitContentProvider.AUTHORITY, ExerciseSessionTable.TABLE_NAME + "/#", EXERCISE_SESSION);
        addURI(CyberFitContentProvider.AUTHORITY, ExerciseSessionTable.TABLE_NAME + "/" + URI_SEGMENT_MOST_RECENT, MOST_RECENT_EXERCISE_HISTORY_RECORD);
        addURI(CyberFitContentProvider.AUTHORITY, ExerciseSessionTable.TABLE_NAME + "/" + URI_SEGMENT_IN_PROGRESS, EXERCISE_HISTORY_RECORD_IN_PROGRESS);
        addURI(CyberFitContentProvider.AUTHORITY, ExerciseSessionTable.TABLE_NAME + "/" + URI_SEGMENT_MOST_RECENT_COMPLETED, MOST_RECENT_EXERCISE_COMPLETED_HISTORY_RECORD);
        addURI(CyberFitContentProvider.AUTHORITY, ExerciseSessionTable.TABLE_NAME + "/" + URI_SEGMENT_SHARE, EXERCISE_HISTORY_RECORDS_FOR_SHARING);
        addURI(CyberFitContentProvider.AUTHORITY, ProgramTable.TABLE_NAME + "/" + com.warriorfitapp.db.sqlite.schema.table.ExerciseTable.TABLE_NAME + "", PROGRAMS_BY_EXERCISE_ID);
        addURI(CyberFitContentProvider.AUTHORITY, com.warriorfitapp.db.sqlite.schema.table.ExerciseTable.TABLE_NAME + "/" + com.warriorfitapp.db.sqlite.schema.table.AuthorTable.TABLE_NAME, AUTHOR_BY_EXERCISE_ID);
        addURI(CyberFitContentProvider.AUTHORITY, ExerciseSessionTable.TABLE_NAME + "/" + ExerciseSessionTable.COLUMN_STATE + "/#", EXERCISE_SESSION_STATE);
        addURI(CyberFitContentProvider.AUTHORITY, com.warriorfitapp.db.sqlite.schema.table.LocationInfoTable.TABLE_NAME + "/" + URI_SEGMENT_MOST_RECENT + "/" + com.warriorfitapp.db.sqlite.schema.table.LocationInfoTable.COLUMN_TYPE + "/#", MOST_RECENT_LOCATION_INFO_STATE);
        addURI(CyberFitContentProvider.AUTHORITY, ExerciseSessionTable.TABLE_NAME + "/" + URI_SEGMENT_DONE + "/#", EXERCISE_HISTORY_RECORD_COMPLETED);
        addURI(CyberFitContentProvider.AUTHORITY, ExerciseSessionTable.TABLE_NAME + "/" + com.warriorfitapp.db.sqlite.schema.table.ExerciseTable.COLUMN_MAP_REQUIRED + "/", MAP_EXERCISE_HISTORY_RECORD);
        addURI(CyberFitContentProvider.AUTHORITY, com.warriorfitapp.db.sqlite.schema.table.LocationInfoTable.TABLE_NAME + "/" + URI_SEGMENT_COUNT + "/#", COUNT_LOCATION_INFO_BY_EXERCISE_HISTORY_RECORD_ID);
        addURI(CyberFitContentProvider.AUTHORITY, com.warriorfitapp.db.sqlite.schema.table.LocationInfoTable.TABLE_NAME, LOCATION_INFO);
        addURI(CyberFitContentProvider.AUTHORITY, com.warriorfitapp.db.sqlite.schema.table.LocationInfoTable.TABLE_NAME + "/#", LOCATION_INFO_BY_ID);
        addURI(CyberFitContentProvider.AUTHORITY, com.warriorfitapp.db.sqlite.schema.table.LocationInfoTable.TABLE_NAME + "/" + URI_SEGMENT_MOST_RECENT + "/", MOST_RECENT_LOCATION_INFO_FOR_EXERCISE);
        addURI(CyberFitContentProvider.AUTHORITY, com.warriorfitapp.db.sqlite.schema.table.UserTable.TABLE_NAME, USER);
        addURI(CyberFitContentProvider.AUTHORITY, com.warriorfitapp.db.sqlite.schema.table.UserTable.TABLE_NAME + "/#", USER_BY_ID);
        addURI(CyberFitContentProvider.AUTHORITY, com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.TABLE_NAME, SOCIAL_PROFILE);
        addURI(CyberFitContentProvider.AUTHORITY, com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.TABLE_NAME + "/#", SOCIAL_PROFILE_BY_ID);
        addURI(CyberFitContentProvider.AUTHORITY, ExerciseSessionTable.TABLE_NAME + "/" + URI_SEGMENT_NUM_COMPLETED_TODAY, NUM_OF_COMPLETED_EXERCISES_TODAY);
    }
}
