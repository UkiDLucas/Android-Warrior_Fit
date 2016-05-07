package com.cyberwalkabout.cyberfit.content;

import android.net.Uri;
import android.text.TextUtils;

import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.AuthorTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.ExerciseSessionTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.ExerciseTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.FavoriteExerciseTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.LocationInfoTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.ProgramTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.SelectedProgramTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.SocialProfileTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.SubscribedProgramTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.UserTable;
import com.cyberwalkabout.cyberfit.model.v2.ExerciseState;
import com.cyberwalkabout.cyberfit.model.v2.LocationInfo;
import com.cyberwalkabout.cyberfit.util.Const;
import com.google.common.base.Joiner;

/**
 * @author Andrii Kovalov
 */
public class UriHelper {
    private static UriHelper INSTANCE = new UriHelper();

    public static UriHelper getInstance() {
        return INSTANCE;
    }

    private UriHelper() {
    }

    public Uri allPrograms() {
        return Uri.withAppendedPath(CyberFitContentProvider.CONTENT_URI, ProgramTable.TABLE_NAME);
    }

    public Uri allExercises() {
        return Uri.withAppendedPath(CyberFitContentProvider.CONTENT_URI, ExerciseTable.TABLE_NAME);
    }

    public Uri allProgramsSelected() {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(ProgramTable.TABLE_NAME);
        builder.appendPath(SelectedProgramTable.TABLE_NAME);
        return builder.build();
    }

    public Uri allProgramsWithAuthors(String programNameSearchKeyword) {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(ProgramTable.TABLE_NAME);
        builder.appendPath(CyberFitUriMatcher.URI_SEGMENT_JOIN);
        builder.appendPath(AuthorTable.TABLE_NAME);

        if (!TextUtils.isEmpty(programNameSearchKeyword)) {
            builder.appendQueryParameter(ProgramTable.COLUMN_NAME, programNameSearchKeyword);
        }

        return builder.build();
    }

    public Uri allExercisesWithProgramNames(String exerciseNameSearchKeyword, boolean favoritesOnly) {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(ExerciseTable.TABLE_NAME);
        builder.appendPath(CyberFitUriMatcher.URI_SEGMENT_JOIN);
        builder.appendPath(ExerciseTable.ALIAS_PROGRAM_NAMES);

        if (!TextUtils.isEmpty(exerciseNameSearchKeyword)) {
            builder.appendQueryParameter(ExerciseTable.COLUMN_NAME, exerciseNameSearchKeyword);
        }

        if (favoritesOnly) {
            builder.appendQueryParameter(FavoriteExerciseTable.TABLE_NAME, String.valueOf(favoritesOnly));
        }

        return builder.build();
    }

    public Uri selectedProgram(long programId) {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(SelectedProgramTable.TABLE_NAME);
        builder.appendPath(Long.toString(programId));
        return builder.build();
    }

    public Uri subscribedProgram(long programId) {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(SubscribedProgramTable.TABLE_NAME);
        builder.appendPath(Long.toString(programId));
        return builder.build();
    }

    public Uri exercisesByProgramId(long programId, String exerciseNameSearchKeyword) {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(ExerciseTable.TABLE_NAME);
        builder.appendPath(ProgramTable.TABLE_NAME);
        builder.appendPath(Long.toString(programId));

        if (!TextUtils.isEmpty(exerciseNameSearchKeyword)) {
            builder.appendQueryParameter(ExerciseTable.COLUMN_NAME, exerciseNameSearchKeyword);
        }

        return builder.build();
    }

    public Uri favoriteExercise(String exerciseId) {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(FavoriteExerciseTable.TABLE_NAME);
        builder.appendQueryParameter(Const.EXERCISE_ID, exerciseId);
        return builder.build();
    }

    public Uri allExerciseSessions() {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(ExerciseSessionTable.TABLE_NAME);
        return builder.build();
    }

    public Uri allExerciseSessions(ExerciseState exerciseState) {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(ExerciseSessionTable.TABLE_NAME);
        builder.appendQueryParameter(ExerciseSessionTable.COLUMN_STATE, exerciseState.name());
        return builder.build();
    }

    public Uri exerciseHistoryRecord(long id) {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(ExerciseSessionTable.TABLE_NAME);
        builder.appendPath(Long.toString(id));
        return builder.build();
    }

    public Uri mostRecentExerciseSession(String exerciseId) {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(ExerciseSessionTable.TABLE_NAME);
        builder.appendPath(CyberFitUriMatcher.URI_SEGMENT_MOST_RECENT);
        builder.appendQueryParameter(Const.EXERCISE_ID, exerciseId);
        return builder.build();
    }

    public Uri mostRecentCompletedExerciseHistoryRecord(String exerciseId) {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(ExerciseSessionTable.TABLE_NAME);
        builder.appendPath(CyberFitUriMatcher.URI_SEGMENT_MOST_RECENT_COMPLETED);
        builder.appendQueryParameter(Const.EXERCISE_ID, exerciseId);
        return builder.build();
    }

    public Uri inProgressExerciseSession(String exerciseId) {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(ExerciseSessionTable.TABLE_NAME);
        builder.appendPath(CyberFitUriMatcher.URI_SEGMENT_IN_PROGRESS);
        builder.appendQueryParameter(Const.EXERCISE_ID, exerciseId);
        return builder.build();
    }

    public Uri exerciseById(String exerciseId) {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(ExerciseTable.TABLE_NAME);
        builder.appendQueryParameter(Const.EXERCISE_ID, exerciseId);
        return builder.build();
    }

    public Uri programsByExerciseId(String exerciseId) {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(ProgramTable.TABLE_NAME);
        builder.appendPath(ExerciseTable.TABLE_NAME);
        builder.appendQueryParameter(Const.EXERCISE_ID, exerciseId);
        return builder.build();
    }

    public Uri authorByExerciseId(String exerciseId) {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(ExerciseTable.TABLE_NAME);
        builder.appendPath(AuthorTable.TABLE_NAME);
        builder.appendQueryParameter(Const.EXERCISE_ID, exerciseId);
        return builder.build();
    }

    public Uri exerciseSessionState(long id, ExerciseState state) {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(ExerciseSessionTable.TABLE_NAME);
        builder.appendPath(ExerciseSessionTable.COLUMN_STATE);
        builder.appendPath(Long.toString(id));
        builder.appendQueryParameter(Const.STATE, state.name());
        return builder.build();
    }

    public Uri mostRecentLocationInfoState(long exerciseSessionId, LocationInfo.LocationType type) {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(LocationInfoTable.TABLE_NAME);
        builder.appendPath(CyberFitUriMatcher.URI_SEGMENT_MOST_RECENT);
        builder.appendPath(LocationInfoTable.COLUMN_TYPE);
        builder.appendPath(Long.toString(exerciseSessionId));
        builder.appendQueryParameter(Const.STATE, type.name());
        return builder.build();
    }

    public Uri allLocationInfo() {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(LocationInfoTable.TABLE_NAME);
        return builder.build();
    }

    public Uri locationInfo(long id) {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(LocationInfoTable.TABLE_NAME);
        builder.appendPath(Long.toString(id));

        return builder.build();
    }

    public Uri locationInfo(long exerciseHistoryRecordId, LocationInfo.LocationType... types) {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(LocationInfoTable.TABLE_NAME);
        builder.appendQueryParameter(LocationInfoTable.COLUMN_EXERCISE_SESSION_ID, Long.toString(exerciseHistoryRecordId));

        if (types != null) {
            builder.appendQueryParameter(LocationInfoTable.COLUMN_TYPE, Joiner.on(",").join(types));
        }

        return builder.build();
    }

    public Uri completedExerciseHistoryRecord(long exerciseHistoryRecordId) {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(ExerciseSessionTable.TABLE_NAME);
        builder.appendPath(CyberFitUriMatcher.URI_SEGMENT_DONE);
        builder.appendPath(Long.toString(exerciseHistoryRecordId));
        return builder.build();
    }

    public Uri allMapExerciseHistoryRecordsInProgressAndPaused() {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(ExerciseSessionTable.TABLE_NAME);
        builder.appendPath(ExerciseTable.COLUMN_MAP_REQUIRED);
        builder.appendQueryParameter(CyberFitUriMatcher.URI_SEGMENT_IN_PROGRESS, Boolean.TRUE.toString());
        builder.appendQueryParameter(CyberFitUriMatcher.URI_SEGMENT_PAUSED, Boolean.TRUE.toString());
        return builder.build();
    }

    public Uri countLocationInfoByExerciseHistoryRecordId(long exerciseHistoryRecordId) {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(LocationInfoTable.TABLE_NAME);
        builder.appendPath(CyberFitUriMatcher.URI_SEGMENT_COUNT);
        builder.appendPath(Long.toString(exerciseHistoryRecordId));
        return builder.build();
    }

    public Uri mostRecentLocationInfoState(long exerciseHistoryRecordId) {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(LocationInfoTable.TABLE_NAME);
        builder.appendPath(CyberFitUriMatcher.URI_SEGMENT_MOST_RECENT);
        builder.appendQueryParameter(LocationInfoTable.COLUMN_EXERCISE_SESSION_ID, Long.toString(exerciseHistoryRecordId));
        return builder.build();
    }

    public Uri user() {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(UserTable.TABLE_NAME);
        return builder.build();
    }

    public Uri userById(long id) {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(UserTable.TABLE_NAME);
        builder.appendPath(Long.toString(id));
        return builder.build();
    }

    public Uri socialProfile() {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(SocialProfileTable.TABLE_NAME);
        return builder.build();
    }

    public Uri socialProfileById(long id) {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(SocialProfileTable.TABLE_NAME);
        builder.appendPath(Long.toString(id));
        return builder.build();
    }

    public Uri socialProfileByUserId(long userId) {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(SocialProfileTable.TABLE_NAME);
        builder.appendQueryParameter(SocialProfileTable.COLUMN_USER_ID, Long.toString(userId));
        return builder.build();
    }

    public Uri socialProfileBySocialId(String socialId) {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(SocialProfileTable.TABLE_NAME);
        builder.appendQueryParameter(SocialProfileTable.COLUMN_SOCIAL_ID, socialId);
        return builder.build();
    }

    public Uri numberOfExercisesCompletedToday(Long userId) {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(ExerciseSessionTable.TABLE_NAME);
        builder.appendPath(CyberFitUriMatcher.URI_SEGMENT_NUM_COMPLETED_TODAY);
        if (userId != null) {
            builder.appendQueryParameter(ExerciseSessionTable.COLUMN_USER_ID, Long.toString(userId));
        }
        return builder.build();
    }

    public Uri authorById(long authorId) {
        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(AuthorTable.TABLE_NAME);
        builder.appendPath(Long.toString(authorId));
        return builder.build();
    }
}
