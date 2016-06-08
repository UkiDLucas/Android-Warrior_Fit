package com.warriorfitapp.mobile.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.warriorfitapp.db.sqlite.schema.table.ExerciseTable;
import com.warriorfitapp.mobile.content.sql.DeleteExerciseSessionById;
import com.warriorfitapp.mobile.content.sql.DeleteFavoriteExerciseId;
import com.warriorfitapp.mobile.content.sql.DeleteSelectedProgramId;
import com.warriorfitapp.mobile.content.sql.DeleteSubscribedProgramId;
import com.warriorfitapp.mobile.content.sql.DeleteUser;
import com.warriorfitapp.mobile.content.sql.InsertExerciseSession;
import com.warriorfitapp.mobile.content.sql.InsertFavoriteExerciseId;
import com.warriorfitapp.mobile.content.sql.InsertLocationInfo;
import com.warriorfitapp.mobile.content.sql.InsertSelectedProgramId;
import com.warriorfitapp.mobile.content.sql.InsertSocialProfile;
import com.warriorfitapp.mobile.content.sql.InsertSubscribedProgramId;
import com.warriorfitapp.mobile.content.sql.InsertUser;
import com.warriorfitapp.mobile.content.sql.QueryAllAuthors;
import com.warriorfitapp.mobile.content.sql.QueryAllExercises;
import com.warriorfitapp.mobile.content.sql.QueryAllPrograms;
import com.warriorfitapp.mobile.content.sql.QueryAllProgramsSelected;
import com.warriorfitapp.mobile.content.sql.QueryAllProgramsWithAuthorNames;
import com.warriorfitapp.mobile.content.sql.QueryAuthorByExerciseId;
import com.warriorfitapp.mobile.content.sql.QueryAuthorById;
import com.warriorfitapp.mobile.content.sql.QueryCompletedExerciseSession;
import com.warriorfitapp.mobile.content.sql.QueryCountFavoriteExerciseById;
import com.warriorfitapp.mobile.content.sql.QueryCountLocationInfoByExerciseSessionId;
import com.warriorfitapp.mobile.content.sql.QueryCountSubscribedProgramById;
import com.warriorfitapp.mobile.content.sql.QueryExerciseById;
import com.warriorfitapp.mobile.content.sql.QueryExerciseSessions;
import com.warriorfitapp.mobile.content.sql.QueryExerciseToProgramAssociations;
import com.warriorfitapp.mobile.content.sql.QueryExercises;
import com.warriorfitapp.mobile.content.sql.QueryExercisesByProgramId;
import com.warriorfitapp.mobile.content.sql.QueryInProgressExerciseSession;
import com.warriorfitapp.mobile.content.sql.QueryLocationInfo;
import com.warriorfitapp.mobile.content.sql.QueryLocationInfoById;
import com.warriorfitapp.mobile.content.sql.QueryMapExerciseHistory;
import com.warriorfitapp.mobile.content.sql.QueryMostRecentCompletedExerciseSession;
import com.warriorfitapp.mobile.content.sql.QueryMostRecentExerciseHistoryRecord;
import com.warriorfitapp.mobile.content.sql.QueryMostRecentLocationInfo;
import com.warriorfitapp.mobile.content.sql.QueryNumberOfExercisesCompletedToday;
import com.warriorfitapp.mobile.content.sql.QueryProgramById;
import com.warriorfitapp.mobile.content.sql.QueryProgramsByExerciseId;
import com.warriorfitapp.mobile.content.sql.QuerySocialProfilesBySocialId;
import com.warriorfitapp.mobile.content.sql.QuerySocialProfilesByUserId;
import com.warriorfitapp.mobile.content.sql.QueryUser;
import com.warriorfitapp.mobile.content.sql.UpdateExerciseSession;
import com.warriorfitapp.mobile.content.sql.UpdateExerciseSessionState;
import com.warriorfitapp.mobile.content.sql.UpdateMostRecentLocationInfoType;
import com.warriorfitapp.mobile.content.sql.UpdateSocialProfile;
import com.warriorfitapp.mobile.content.sql.UpdateUser;

import java.io.IOException;

public class CyberFitContentProvider extends ContentProvider {
    private static final String TAG = CyberFitContentProvider.class.getSimpleName();
    public static final String AUTHORITY = "com.cyberwalkabout.cyberfit.contentprovider";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private final UriMatcher uriMatcher = new CyberFitUriMatcher();
    private DatabaseHelper dbeHelper;

    @Override
    public boolean onCreate() {
        dbeHelper = new DatabaseHelper(getContext());
        try {
            dbeHelper.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete " + uri);
        switch (uriMatcher.match(uri)) {
            case CyberFitUriMatcher.SELECTED_PROGRAM: {
                return new DeleteSelectedProgramId(getContext(), dbeHelper.getWritableDatabase(), uri, selection, selectionArgs).execute();
            }
            case CyberFitUriMatcher.SUBSCRIBED_PROGRAM: {
                return new DeleteSubscribedProgramId(getContext(), dbeHelper.getWritableDatabase(), uri, selection, selectionArgs).execute();
            }
            case CyberFitUriMatcher.FAVORITE_EXERCISE: {
                return new DeleteFavoriteExerciseId(getContext(), dbeHelper.getWritableDatabase(), uri, selection, selectionArgs).execute();
            }
            case CyberFitUriMatcher.EXERCISE_SESSION: {
                return new DeleteExerciseSessionById(getContext(), dbeHelper.getWritableDatabase(), uri, selection, selectionArgs).execute();
            }
            case CyberFitUriMatcher.USER: {
                return new DeleteUser(getContext(), dbeHelper.getWritableDatabase(), uri, selection, selectionArgs).execute();
            }
            default:
                throw new IllegalArgumentException("Not supported URI: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Log.d(TAG, "insert " + uri);
        switch (uriMatcher.match(uri)) {
            case CyberFitUriMatcher.SELECTED_PROGRAM: {
                return new InsertSelectedProgramId(getContext(), dbeHelper.getWritableDatabase(), uri, values).execute();
            }
            case CyberFitUriMatcher.SUBSCRIBED_PROGRAM: {
                return new InsertSubscribedProgramId(getContext(), dbeHelper.getWritableDatabase(), uri, values).execute();
            }
            case CyberFitUriMatcher.FAVORITE_EXERCISE: {
                return new InsertFavoriteExerciseId(getContext(), dbeHelper.getWritableDatabase(), uri, values).execute();
            }
            case CyberFitUriMatcher.EXERCISE_HISTORY_RECORDS: {
                return new InsertExerciseSession(getContext(), dbeHelper.getWritableDatabase(), uri, values).execute();
            }
            case CyberFitUriMatcher.LOCATION_INFO: {
                return new InsertLocationInfo(getContext(), dbeHelper.getWritableDatabase(), uri, values).execute();
            }
            case CyberFitUriMatcher.USER: {
                return new InsertUser(getContext(), dbeHelper.getWritableDatabase(), uri, values).execute();
            }
            case CyberFitUriMatcher.SOCIAL_PROFILE: {
                return new InsertSocialProfile(getContext(), dbeHelper.getWritableDatabase(), uri, values).execute();
            }
            default:
                throw new IllegalArgumentException("Not supported URI: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(TAG, "update " + uri);
        switch (uriMatcher.match(uri)) {
            case CyberFitUriMatcher.EXERCISE_SESSION: {
                return new UpdateExerciseSession(getContext(), dbeHelper.getWritableDatabase(), uri, values, selection, selectionArgs).execute();
            }
            case CyberFitUriMatcher.EXERCISE_SESSION_STATE: {
                return new UpdateExerciseSessionState(getContext(), dbeHelper.getWritableDatabase(), uri).execute();
            }
            case CyberFitUriMatcher.MOST_RECENT_LOCATION_INFO_STATE: {
                return new UpdateMostRecentLocationInfoType(getContext(), dbeHelper.getWritableDatabase(), uri).execute();
            }
            case CyberFitUriMatcher.USER_BY_ID: {
                return new UpdateUser(getContext(), dbeHelper.getWritableDatabase(), uri, values, selection, selectionArgs).execute();
            }
            case CyberFitUriMatcher.SOCIAL_PROFILE_BY_ID: {
                return new UpdateSocialProfile(getContext(), dbeHelper.getWritableDatabase(), uri, values, selection, selectionArgs).execute();
            }
        }

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query " + uri);

        Cursor cursor = null;

        SQLiteDatabase database = dbeHelper.getReadableDatabase();

        switch (uriMatcher.match(uri)) {
            case CyberFitUriMatcher.ALL_PROGRAMS: {
                cursor = new QueryAllPrograms(getContext(), database, uri, projection, selection, selectionArgs, sortOrder).execute();
            }
            break;
            case CyberFitUriMatcher.ALL_PROGRAMS_WITH_AUTHORS: {
                cursor = new QueryAllProgramsWithAuthorNames(getContext(), database, uri, projection, selection, selectionArgs, sortOrder).execute();
            }
            break;
            case CyberFitUriMatcher.ALL_PROGRAMS_SELECTED: {
                cursor = new QueryAllProgramsSelected(getContext(), database, uri, projection, selection, selectionArgs, sortOrder).execute();
            }
            break;
            case CyberFitUriMatcher.PROGRAM_BY_ID: {
                cursor = new QueryProgramById(getContext(), database, uri, projection, selection, selectionArgs, sortOrder).execute();
            }
            break;
            case CyberFitUriMatcher.ALL_AUTHORS: {
                cursor = new QueryAllAuthors(getContext(), database, uri, projection, selection, selectionArgs, sortOrder).execute();
            }
            break;
            case CyberFitUriMatcher.AUTHOR_BY_ID: {
                cursor = new QueryAuthorById(getContext(), dbeHelper.getWritableDatabase(), uri, projection).execute();
            }
            break;
            case CyberFitUriMatcher.ALL_EXERCISES: {
                String exerciseId = uri.getQueryParameter(ExerciseTable.COLUMN_ID);

                if (!TextUtils.isEmpty(exerciseId)) {
                    cursor = new QueryExerciseById(getContext(), database, uri, projection).execute();
                } else {
                    cursor = new QueryAllExercises(getContext(), database, uri, projection, selection, selectionArgs, sortOrder).execute();
                }
            }
            break;
            case CyberFitUriMatcher.ALL_EXERCISES_WITH_PROGRAM_NAMES: {
                cursor = new QueryExercises(getContext(), database, uri).execute();
            }
            break;
            case CyberFitUriMatcher.ALL_EXERCISE_TO_PROGRAM_ASSOCIATIONS: {
                cursor = new QueryExerciseToProgramAssociations(getContext(), database, uri, projection, selection, selectionArgs, sortOrder).execute();
            }
            break;
            case CyberFitUriMatcher.EXERCISES_BY_PROGRAM_ID: {
                cursor = new QueryExercisesByProgramId(getContext(), database, uri, projection, selection, selectionArgs, sortOrder).execute();
            }
            break;
            case CyberFitUriMatcher.SUBSCRIBED_PROGRAM: {
                cursor = new QueryCountSubscribedProgramById(getContext(), database, uri).execute();
            }
            break;
            case CyberFitUriMatcher.FAVORITE_EXERCISE: {
                cursor = new QueryCountFavoriteExerciseById(getContext(), database, uri).execute();
            }
            break;
            case CyberFitUriMatcher.MOST_RECENT_EXERCISE_HISTORY_RECORD: {
                cursor = new QueryMostRecentExerciseHistoryRecord(getContext(), database, uri).execute();
            }
            break;
            case CyberFitUriMatcher.EXERCISE_HISTORY_RECORD_IN_PROGRESS: {
                cursor = new QueryInProgressExerciseSession(getContext(), database, uri).execute();
            }
            break;
            case CyberFitUriMatcher.MOST_RECENT_EXERCISE_COMPLETED_HISTORY_RECORD: {
                cursor = new QueryMostRecentCompletedExerciseSession(getContext(), database, uri, projection, selection, selectionArgs, sortOrder).execute();
            }
            break;
            case CyberFitUriMatcher.EXERCISE_HISTORY_RECORDS: {
                cursor = new QueryExerciseSessions(getContext(), database, uri, projection, selection, selectionArgs, sortOrder).execute();
            }
            break;
            case CyberFitUriMatcher.PROGRAMS_BY_EXERCISE_ID: {
                cursor = new QueryProgramsByExerciseId(getContext(), database, uri, projection, sortOrder).execute();
            }
            break;
            case CyberFitUriMatcher.AUTHOR_BY_EXERCISE_ID: {
                cursor = new QueryAuthorByExerciseId(getContext(), database, uri, projection, sortOrder).execute();
            }
            break;
            case CyberFitUriMatcher.EXERCISE_HISTORY_RECORD_COMPLETED: {
                cursor = new QueryCompletedExerciseSession(getContext(), database, uri).execute();
            }
            break;
            case CyberFitUriMatcher.MAP_EXERCISE_HISTORY_RECORD: {
                cursor = new QueryMapExerciseHistory(getContext(), database, uri, projection, selection, selectionArgs, sortOrder).execute();
            }
            break;
            case CyberFitUriMatcher.COUNT_LOCATION_INFO_BY_EXERCISE_HISTORY_RECORD_ID: {
                cursor = new QueryCountLocationInfoByExerciseSessionId(getContext(), database, uri).execute();
            }
            break;
            case CyberFitUriMatcher.LOCATION_INFO_BY_ID: {
                cursor = new QueryLocationInfoById(getContext(), database, uri, projection).execute();
            }
            break;
            case CyberFitUriMatcher.MOST_RECENT_LOCATION_INFO_FOR_EXERCISE: {
                cursor = new QueryMostRecentLocationInfo(getContext(), database, uri, projection, selection, selectionArgs, sortOrder).execute();
            }
            break;
            case CyberFitUriMatcher.LOCATION_INFO: {
                cursor = new QueryLocationInfo(getContext(), database, uri, projection, selection, selectionArgs, sortOrder).execute();
            }
            break;
            case CyberFitUriMatcher.USER: {
                cursor = new QueryUser(getContext(), database, uri, projection, selection, selectionArgs, sortOrder).execute();
            }
            break;
            case CyberFitUriMatcher.SOCIAL_PROFILE: {
                if (!TextUtils.isEmpty(uri.getQueryParameter(com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.COLUMN_USER_ID))) {
                    cursor = new QuerySocialProfilesByUserId(getContext(), database, uri, projection, selection, selectionArgs, sortOrder).execute();
                } else if (!TextUtils.isEmpty(uri.getQueryParameter(com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.COLUMN_SOCIAL_ID))) {
                    cursor = new QuerySocialProfilesBySocialId(getContext(), database, uri, projection, selection, selectionArgs, sortOrder).execute();
                } else {
                    throw new IllegalArgumentException("Not supported URI: " + uri);
                }
            }
            break;
            case CyberFitUriMatcher.NUM_OF_COMPLETED_EXERCISES_TODAY: {
                cursor = new QueryNumberOfExercisesCompletedToday(getContext(), database, uri, projection, selection, selectionArgs, sortOrder).execute();
            }
            break;
        }

        if (cursor != null) {
            return cursor;
        } else {
            throw new IllegalArgumentException("Not supported URI: " + uri);
        }
    }
}
