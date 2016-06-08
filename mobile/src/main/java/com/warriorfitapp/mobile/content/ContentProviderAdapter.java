package com.warriorfitapp.mobile.content;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;

import com.google.common.collect.ObjectArrays;
import com.warriorfitapp.db.sqlite.schema.table.ExerciseSessionTable;
import com.warriorfitapp.db.sqlite.schema.table.ExerciseTable;
import com.warriorfitapp.db.sqlite.schema.table.LocationInfoTable;
import com.warriorfitapp.db.sqlite.schema.table.ProgramTable;
import com.warriorfitapp.db.sqlite.schema.table.SubscribedProgramTable;
import com.warriorfitapp.mobile.model.v2.factory.AuthorFactory;
import com.warriorfitapp.mobile.model.v2.factory.ExerciseCursorFactory;
import com.warriorfitapp.mobile.model.v2.factory.ExerciseSessionCursorFactory;
import com.warriorfitapp.mobile.model.v2.factory.LocationInfoCursorFactory;
import com.warriorfitapp.mobile.model.v2.factory.SocialProfileCursorFactory;
import com.warriorfitapp.mobile.model.v2.factory.UserCursorFactory;
import com.warriorfitapp.mobile.util.Const;
import com.warriorfitapp.model.v2.AccountType;
import com.warriorfitapp.model.v2.Exercise;

import java.util.ArrayList;
import java.util.List;

// TODO: convert this class to multiple DAO objects

/**
 * @author Andrii Kovalov
 */
public class ContentProviderAdapter {
    private static final String TAG = ContentProviderAdapter.class.getSimpleName();

    public static final int LOADER_EXERCISES = 101;
    public static final int LOADER_PROGRAMS_FILTER = 102;
    public static final int LOADER_EXERCISES_BY_PROGRAM_ID = 103;
    public static final int LOADER_PROGRAMS = 201;
    public static final int LOADER_MOST_RECENT_COMPLETED_EXERCISE = 301;
    public static final int LOADER_EXERCISE_IN_PROGRESS = 302;
    public static final int LOADER_PROGRAMS_BY_EXERCISE = 303;
    public static final int LOADER_EXERCISE_COMPLETED = 304;

    public static final int LOADER_EXERCISE_HISTORY = 401;

    public static final int LOADER_AUTHOR_BY_EXERCISE = 501;

    public static final int LOADER_USER = 601;

    private static final ContentProviderAdapter INSTANCE = new ContentProviderAdapter();

    private UriHelper uriHelper = UriHelper.getInstance();

    public static ContentProviderAdapter getInstance() {
        return INSTANCE;
    }

    private ContentProviderAdapter() {
    }

    public Loader<Cursor> loaderExercises(Context context, Bundle args) {
        String searchKeyword = null;
        boolean favoritesOnly = false;

        if (args != null) {
            if (args.containsKey(Const.SEARCH_KEYWORD) && !TextUtils.isEmpty(args.getString(Const.SEARCH_KEYWORD))) {
                searchKeyword = args.getString(Const.SEARCH_KEYWORD);
            }
            if (args.containsKey(Const.FAVORITES_ONLY)) {
                favoritesOnly = args.getBoolean(Const.FAVORITES_ONLY);
            }
        }

        Uri uri = uriHelper.allExercisesWithProgramNames(searchKeyword, favoritesOnly);
        String[] columns = ObjectArrays.concat(ExerciseTable.instance().getColumnNames(), ExerciseTable.ALIAS_PROGRAM_NAMES);
        return new CursorLoader(context, uri, columns, null, null, null);
    }

    public Loader<Cursor> loaderProgramsFilter(Context context, Bundle args) {
        String[] columns = ObjectArrays.concat(
                ProgramTable.ALL_COLUMNS_JOIN_AUTHOR_NAME,
                new String[]{
                        com.warriorfitapp.db.sqlite.schema.table.SelectedProgramTable.instance().qualifiedColumnName(com.warriorfitapp.db.sqlite.schema.table.SelectedProgramTable.COLUMN_PROGRAM_ID) + " as " + ProgramTable.ALIAS_SELECTED_PROGRAM_ID
                }, String.class
        );
        return new CursorLoader(context, uriHelper.allProgramsSelected(), columns, null, null, null);
    }

    public Loader<Cursor> loaderProgramsWithAuthorNames(Context context, Bundle args) {
        String searchKeyword = null;
        if (args != null && args.containsKey(Const.SEARCH_KEYWORD) && !TextUtils.isEmpty(args.getString(Const.SEARCH_KEYWORD))) {
            searchKeyword = args.getString(Const.SEARCH_KEYWORD);
        }

        Uri uri = uriHelper.allProgramsWithAuthors(searchKeyword);
        return new CursorLoader(context, uri, ObjectArrays.concat(ProgramTable.ALL_COLUMNS_JOIN_AUTHOR_NAME, SubscribedProgramTable.instance().qualifiedColumnName(SubscribedProgramTable.COLUMN_PROGRAM_ID) + " as " + ProgramTable.ALIAS_SUBSCRIBED_PROGRAM_ID), null, null, null);

    }

    public void selectedProgram(Context context, long id) {
        context.getContentResolver().insert(uriHelper.selectedProgram(id), null);
    }

    public void unselectedProgram(Context context, long id) {
        context.getContentResolver().delete(uriHelper.selectedProgram(id), null, null);
    }

    public Loader<Cursor> loaderExercisesByProgramId(Context context, Bundle args) {
        long programId = args.getLong(Const.PROGRAM_ID);
        String searchKeyword = null;
        if (args.containsKey(Const.SEARCH_KEYWORD) && !TextUtils.isEmpty(args.getString(Const.SEARCH_KEYWORD))) {
            searchKeyword = args.getString(Const.SEARCH_KEYWORD);
        }

        Uri uri = uriHelper.exercisesByProgramId(programId, searchKeyword);
        String[] projection = ObjectArrays.concat(ExerciseTable.ALL_COLUMNS_QUALIFIED,
                com.warriorfitapp.db.sqlite.schema.table.FavoriteExerciseTable.instance().qualifiedColumnName(com.warriorfitapp.db.sqlite.schema.table.FavoriteExerciseTable.COLUMN_EXERCISE_ID) + " as " + ExerciseTable.ALIAS_FAVORITE_EXERCISE_ID);
        return new CursorLoader(context, uri, projection, null, null, null);
    }

    public void subscribeProgram(Context context, long id) {
        context.getContentResolver().insert(uriHelper.subscribedProgram(id), null);
    }

    public void unsubscribeProgram(Context context, long id) {
        context.getContentResolver().delete(uriHelper.subscribedProgram(id), null, null);
    }

    public boolean isProgramSubscribed(Context context, long id) {
        boolean isSubscribed = false;

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uriHelper.subscribedProgram(id), null, null, null, null);

            if (cursor.moveToNext()) {
                isSubscribed = cursor.getInt(0) != 0;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return isSubscribed;
    }

    public void favoriteExercise(Context context, String id) {
        context.getContentResolver().insert(uriHelper.favoriteExercise(id), null);
    }

    public void unfavoriteExercise(Context context, String id) {
        context.getContentResolver().delete(uriHelper.favoriteExercise(id), null, null);
    }

    public boolean isExerciseFavorite(Context context, String id) {
        boolean isFavorite = false;

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uriHelper.favoriteExercise(id), null, null, null, null);

            if (cursor != null && cursor.moveToNext()) {
                isFavorite = cursor.getInt(0) != 0;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return isFavorite;
    }

    public void insertExerciseSession(Context context, com.warriorfitapp.model.v2.ExerciseSession exerciseSession) {
        // TODO: replace with sqlite trigger
        if (exerciseSession.getUserId() == null) {
            com.warriorfitapp.model.v2.User currentUser = getCurrentUser(context);
            if (currentUser != null) {
                exerciseSession.setUserId(currentUser.getId());
            }
        }

        ContentValues contentValues = toContentValues(exerciseSession);
        Uri uri = context.getContentResolver().insert(uriHelper.allExerciseSessions(), contentValues);
        if (uri != null) {
            exerciseSession.setId(Long.valueOf(uri.getLastPathSegment()));
        }
    }

    public Uri insertLocationInfo(Context context, com.warriorfitapp.model.v2.LocationInfo locationInfo) {
        return insertLocationInfo(context, locationInfo, null);
    }

    public Uri insertLocationInfo(Context context, com.warriorfitapp.model.v2.LocationInfo locationInfo, String exerciseId) {
        ContentValues contentValues = toContentValues(locationInfo);

        if (locationInfo.getType() != null) {
            contentValues.put(LocationInfoTable.COLUMN_TYPE, locationInfo.getType().name());
        }

        contentValues.put(LocationInfoTable.COLUMN_CURRENT_DISTANCE, locationInfo.getCurrentDistance());
        contentValues.put(LocationInfoTable.COLUMN_EXERCISE_SESSION_ID, locationInfo.getExerciseHistoryRecordId());

        Uri insertUri = uriHelper.allLocationInfo();
        if (!TextUtils.isEmpty(exerciseId)) {
            insertUri = insertUri.buildUpon().appendQueryParameter(ExerciseSessionTable.COLUMN_EXERCISE_ID, exerciseId).build();
        }

        Uri uri = context.getContentResolver().insert(insertUri, contentValues);
        if (uri != null) {
            locationInfo.setId(Long.valueOf(uri.getLastPathSegment()));
        }
        return uri;
    }

    public void updateExerciseSession(Context context, com.warriorfitapp.model.v2.ExerciseSession exerciseSession, boolean updateMaxAndAvgValues) {
        if (exerciseSession.getId() == null) {
            insertExerciseSession(context, exerciseSession);
        } else {
            ContentValues contentValues = toContentValues(exerciseSession);

            updateExerciseSession(context, contentValues, updateMaxAndAvgValues);
        }
    }

    public void updateExerciseSession(Context context, ContentValues contentValues, boolean updateMaxAndAvgValues) {
        Uri uri = uriHelper.exerciseHistoryRecord(contentValues.getAsLong(ExerciseSessionTable.COLUMN_ID));

        if (updateMaxAndAvgValues) {
            uri = uri.buildUpon().appendQueryParameter(Const.UPDATE_MAX_AND_AVG_VALUES, Boolean.TRUE.toString()).build();
        }

        contentValues.remove(ExerciseSessionTable.COLUMN_ID);

        context.getContentResolver().update(uri, contentValues, null, null);
    }

    public void deleteExerciseSessionById(Context context, long id) {
        context.getContentResolver().delete(uriHelper.exerciseHistoryRecord(id), null, null);
    }

    public Loader<Cursor> loaderMostRecentCompletedExerciseSession(Context context, Bundle args) {
        Uri uri = uriHelper.mostRecentCompletedExerciseHistoryRecord(args.getString(Const.EXERCISE_ID));
        return new CursorLoader(context, uri, ExerciseSessionTable.ALL_COLUMNS, null, null, null);
    }

    public Loader<Cursor> loaderInProgressExerciseHistoryRecord(Context context, Bundle args) {
        Uri uri = uriHelper.inProgressExerciseSession(args.getString(Const.EXERCISE_ID));
        return new CursorLoader(context, uri, ExerciseSessionTable.ALL_COLUMNS, null, null, null);
    }

    public Loader<Cursor> loaderCompletedExerciseHistoryRecord(Context context, Bundle args) {
        long exerciseHistoryRecordId = args.getLong(Const.EXERCISE_SESSION_ID);
        Uri uri = uriHelper.completedExerciseHistoryRecord(exerciseHistoryRecordId);
        return new CursorLoader(context, uri, ExerciseSessionTable.ALL_COLUMNS, null, null, null);
    }

    public com.warriorfitapp.model.v2.ExerciseSession getInProgressExerciseHistoryRecord(Context context, String exerciseId) {
        Uri uri = uriHelper.inProgressExerciseSession(exerciseId);


        com.warriorfitapp.model.v2.ExerciseSession exerciseSession = null;

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, ExerciseSessionTable.ALL_COLUMNS, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                exerciseSession = ExerciseSessionCursorFactory.getInstance().create(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return exerciseSession;
    }

    public Loader<Cursor> loaderExerciseSessions(Context context, com.warriorfitapp.model.v2.ExerciseState exerciseState) {
        Uri uri = uriHelper.allExerciseSessions(exerciseState);

        return new CursorLoader(
                context,
                uri,
                ObjectArrays.concat(ExerciseSessionTable.ALL_COLUMNS_QUALIFIED, new String[]
                        {
                                ExerciseTable.instance().qualifiedColumnName(ExerciseTable.COLUMN_NAME),
                                ExerciseTable.instance().qualifiedColumnName(ExerciseTable.COLUMN_TRACK_DISTANCE),
                                ExerciseTable.instance().qualifiedColumnName(ExerciseTable.COLUMN_TRACK_TIME),
                                ExerciseTable.instance().qualifiedColumnName(ExerciseTable.COLUMN_TRACK_WEIGHT),
                                ExerciseTable.instance().qualifiedColumnName(ExerciseTable.COLUMN_TRACK_REPETITIONS),
                        }, String.class)

                , null, null, null);
    }

    public Cursor cursorExerciseSessionsCompletedToday(Context context) {
        Uri uri = uriHelper.allExerciseSessions(com.warriorfitapp.model.v2.ExerciseState.DONE)
                .buildUpon().appendQueryParameter(Const.TODAY_ONLY, Boolean.TRUE.toString()).build();

        return context.getContentResolver().query(uri, ObjectArrays.concat(ExerciseSessionTable.ALL_COLUMNS_QUALIFIED, new String[]
                {
                        ExerciseTable.instance().qualifiedColumnName(ExerciseTable.COLUMN_NAME),
                        ExerciseTable.instance().qualifiedColumnName(ExerciseTable.COLUMN_TRACK_DISTANCE),
                        ExerciseTable.instance().qualifiedColumnName(ExerciseTable.COLUMN_TRACK_TIME),
                        ExerciseTable.instance().qualifiedColumnName(ExerciseTable.COLUMN_TRACK_WEIGHT),
                        ExerciseTable.instance().qualifiedColumnName(ExerciseTable.COLUMN_TRACK_REPETITIONS),
                }, String.class), null, null, null);
    }

    public Exercise getExerciseById(Context context, String id) {
        Uri uri = uriHelper.exerciseById(id);

        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(uri, ExerciseTable.ALL_COLUMNS, ExerciseTable.COLUMN_ID + " = ?", new String[]{id}, null);
            if (cursor != null && cursor.moveToFirst()) {
                return ExerciseCursorFactory.getInstance().create(cursor);
            } else {
                return null;
            }
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception ignore) {
                }
            }
        }
    }

    public Loader<Cursor> loaderProgramsByExerciseId(Context context, Bundle args) {
        Uri uri = uriHelper.programsByExerciseId(args.getString(Const.EXERCISE_ID));

        return new CursorLoader(context, uri, new String[]{ProgramTable.instance().qualifiedColumnName(ProgramTable.COLUMN_ID), ProgramTable.COLUMN_NAME}, null, null, null);
    }

    public Loader<Cursor> loaderAuthorByExerciseId(Context context, Bundle args) {
        Uri uri = uriHelper.authorByExerciseId(args.getString(Const.EXERCISE_ID));

        return new CursorLoader(context, uri, com.warriorfitapp.db.sqlite.schema.table.AuthorTable.ALL_COLUMNS_QUALIFIED, null, null, null);
    }

    public Loader<Cursor> loaderUser(Context context) {
        Uri uri = uriHelper.user();

        return new CursorLoader(context, uri, com.warriorfitapp.db.sqlite.schema.table.UserTable.ALL_COLUMNS, null, null, null);
    }

    public com.warriorfitapp.model.v2.SocialProfile getSocialProfileBySocialId(Context context, String socialId) {
        Cursor cursor = null;
        try {
            Uri uri = uriHelper.socialProfileBySocialId(socialId);
            cursor = context.getContentResolver().query(uri, com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.ALL_COLUMNS, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return SocialProfileCursorFactory.getInstance().create(cursor);
            } else {
                return null;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public com.warriorfitapp.model.v2.User getCurrentUser(Context context) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uriHelper.user(), com.warriorfitapp.db.sqlite.schema.table.UserTable.ALL_COLUMNS, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return UserCursorFactory.getInstance().create(cursor);
            } else {
                return null;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void setCurrentUser(Context context, com.warriorfitapp.model.v2.User user, com.warriorfitapp.model.v2.SocialProfile socialProfile) {
        com.warriorfitapp.model.v2.User currentUser = getCurrentUser(context);

        // there always must be user in db (default local)
        if (currentUser != null) {
            // social profile provided (GOOGLE or FACEBOOK (in future))
            if (socialProfile != null) {
                // if user switching from local user to social network user it will override local account
                if (currentUser.getAccountType() == AccountType.LOCAL && user.getAccountType() != AccountType.LOCAL) {
                    overrideLocalUser(context, currentUser, user, socialProfile);
                } else {
                    // not local user
                    com.warriorfitapp.model.v2.SocialProfile currentSocialProfile = getSocialProfileBySocialId(context, socialProfile.getSocialId());
                    if (currentSocialProfile != null) {
                        updateExistingSocialNetworkUser(context, user, socialProfile, currentUser, currentSocialProfile);
                    } else {
                        newSocialNetworkUser(context, user, socialProfile);
                    }
                }
            } else {
                // social profile is missing which means user is local
                if (user.getAccountType() == AccountType.LOCAL) {
                    // TODO: update
                } else {
                    // illegal state, social profile isn't provided and user isn't local
                    Log.w(TAG, "Social profile isn't provided and user type is " + user.getAccountType());
                }
            }
        } else {
            // should never happen because by default there is local user
        }
    }

    public void updateUser(Context context, long userId, ContentValues values) {
        context.getContentResolver().update(uriHelper.userById(userId), values, null, null);
    }

    private void newSocialNetworkUser(Context context, com.warriorfitapp.model.v2.User user, com.warriorfitapp.model.v2.SocialProfile socialProfile) {
        // new social network account
        user.setActive(true);
        ContentValues contentValues = toContentValues(user);

        Uri uri = context.getContentResolver().insert(uriHelper.user(), contentValues);
        if (uri != null) {
            long userId = Long.parseLong(uri.getLastPathSegment());

            contentValues = toContentValues(socialProfile, null);
            contentValues.remove(com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.COLUMN_ID);
            contentValues.put(com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.COLUMN_USER_ID, userId);
            contentValues.put(com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.COLUMN_IS_PRIMARY, true);

            context.getContentResolver().insert(uriHelper.socialProfile(), contentValues);
        }
    }

    private void updateExistingSocialNetworkUser(Context context, com.warriorfitapp.model.v2.User user, com.warriorfitapp.model.v2.SocialProfile socialProfile, com.warriorfitapp.model.v2.User currentUser, com.warriorfitapp.model.v2.SocialProfile currentSocialProfile) {
        // existing social profile...
        Long userId = currentSocialProfile.getUserId();
        ContentValues contentValues = toContentValues(socialProfile, null);
        contentValues.remove(com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.COLUMN_ID);
        contentValues.put(com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.COLUMN_USER_ID, userId);
        contentValues.put(com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.COLUMN_IS_PRIMARY, true);

        context.getContentResolver().update(uriHelper.socialProfileById(currentSocialProfile.getId()), contentValues, null, null);

        user.setId(userId);
        user.setActive(true);
        contentValues = toContentValues(user);

        context.getContentResolver().update(uriHelper.userById(currentUser.getId()), contentValues, null, null);
    }

    private void overrideLocalUser(Context context, com.warriorfitapp.model.v2.User currentUser, com.warriorfitapp.model.v2.User newUser, com.warriorfitapp.model.v2.SocialProfile socialProfile) {
        // signed in with social network, in this case we override LOCAL newUser with social network account but keep references to existing exercise history
        newUser.setActive(true);
        ContentValues contentValues = toContentValues(newUser);

        int update = context.getContentResolver().update(uriHelper.userById(currentUser.getId()), contentValues, null, null);

        if (update > 0) {
            socialProfile.setPrimary(true);
            contentValues = toContentValues(socialProfile, currentUser.getId());
            context.getContentResolver().insert(uriHelper.socialProfile(), contentValues);
        }
    }

    public void updateExerciseHistoryRecordState(Context context, long id, com.warriorfitapp.model.v2.ExerciseState state) {
        Uri uri = uriHelper.exerciseSessionState(id, state);
        context.getContentResolver().update(uri, null, null, null);
    }

    public void updateMostRecentLocationInfoState(Context context, long exerciseHistoryRecordId, com.warriorfitapp.model.v2.LocationInfo.LocationType type) {
        Uri uri = uriHelper.mostRecentLocationInfoState(exerciseHistoryRecordId, type);
        context.getContentResolver().update(uri, null, null, null);
    }

    public long countLocationInfoByExerciseHistoryRecordId(Context context, long exerciseHistoryRecordId) {
        long locationsCount = 0;

        Cursor countCursor = null;
        try {
            countCursor = context.getContentResolver().query(uriHelper.countLocationInfoByExerciseHistoryRecordId(exerciseHistoryRecordId), null, null, null, null);

            if (countCursor != null && countCursor.moveToFirst()) {
                locationsCount = countCursor.getLong(0);
            }
        } finally {
            if (countCursor != null) {
                countCursor.close();
            }
        }
        return locationsCount;
    }

    public com.warriorfitapp.model.v2.LocationInfo getLocationInfoById(Context context, long id) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uriHelper.locationInfo(id), LocationInfoTable.ALL_COLUMNS, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                return LocationInfoCursorFactory.getInstance().create(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public int countMapExercisesInProgress(Context context) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uriHelper.allMapExerciseHistoryRecordsInProgressAndPaused(), new String[]{"count(*)"}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    public com.warriorfitapp.model.v2.LocationInfo getMostRecentLocationInfo(Context context, long exerciseHistoryRecordId) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uriHelper.mostRecentLocationInfoState(exerciseHistoryRecordId), LocationInfoTable.ALL_COLUMNS, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                return LocationInfoCursorFactory.getInstance().create(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public Cursor cursorLocationInfo(Context context, long exerciseHistoryRecordId, com.warriorfitapp.model.v2.LocationInfo.LocationType... types) {
        Uri uri = uriHelper.locationInfo(exerciseHistoryRecordId, types);

        return context.getContentResolver().query(uri, LocationInfoTable.ALL_COLUMNS, null, null, LocationInfoTable.COLUMN_TIMESTAMP + " asc");
    }

    public List<com.warriorfitapp.model.v2.LocationInfo> getLocationInfoList(Context context, long exerciseHistoryRecordId, com.warriorfitapp.model.v2.LocationInfo.LocationType... types) {
        List<com.warriorfitapp.model.v2.LocationInfo> locationInfoList = new ArrayList<>();

        Cursor cursor = null;
        try {
            cursor = cursorLocationInfo(context, exerciseHistoryRecordId, types);

            while (cursor.moveToNext()) {
                locationInfoList.add(LocationInfoCursorFactory.getInstance().create(cursor));
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return locationInfoList;
    }

    public int getNumberOfExercisesCompletedToday(Context context, Long userId) {
        int num = 0;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uriHelper.numberOfExercisesCompletedToday(userId), null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                num = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return num;
    }

    public com.warriorfitapp.model.v2.Author getAuthorById(Context context, long authorId) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uriHelper.authorById(authorId), null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                return AuthorFactory.getInstance().create(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    @NonNull
    private ContentValues toContentValues(com.warriorfitapp.model.v2.ExerciseSession exerciseSession) {
        ContentValues contentValues = new ContentValues();
        if (exerciseSession.getId() != null) {
            contentValues.put(ExerciseSessionTable.COLUMN_ID, exerciseSession.getId());
        }
        if (exerciseSession.getRepetitions() != null) {
            contentValues.put(ExerciseSessionTable.COLUMN_REPETITIONS, exerciseSession.getRepetitions());
        }
        if (exerciseSession.getDistance() != null) {
            contentValues.put(ExerciseSessionTable.COLUMN_DISTANCE, exerciseSession.getDistance());
        }
        if (exerciseSession.getWeight() != null) {
            contentValues.put(ExerciseSessionTable.COLUMN_WEIGHT, exerciseSession.getWeight());
        }
        if (exerciseSession.getTime() != null) {
            contentValues.put(ExerciseSessionTable.COLUMN_TIME, exerciseSession.getTime());
        }
        if (exerciseSession.getExerciseId() != null) {
            contentValues.put(ExerciseSessionTable.COLUMN_EXERCISE_ID, exerciseSession.getExerciseId());
        }
        if (exerciseSession.getState() != null) {
            contentValues.put(ExerciseSessionTable.COLUMN_STATE, exerciseSession.getState().name());
        }
        if (exerciseSession.getTimestampStarted() != null) {
            contentValues.put(ExerciseSessionTable.COLUMN_TIMESTAMP_STARTED, exerciseSession.getTimestampStarted());
        }
        if (exerciseSession.getLastTimestampStarted() != null) {
            contentValues.put(ExerciseSessionTable.COLUMN_LAST_TIMESTAMP_STARTED, exerciseSession.getLastTimestampStarted());
        }
        if (exerciseSession.getTimestampCompleted() != null) {
            contentValues.put(ExerciseSessionTable.COLUMN_TIMESTAMP_COMPLETED, exerciseSession.getTimestampCompleted());
        }
        if (exerciseSession.getAvgPace() != null) {
            contentValues.put(ExerciseSessionTable.COLUMN_AVG_PACE, exerciseSession.getAvgPace());
        }
        if (exerciseSession.getAvgSpeed() != null) {
            contentValues.put(ExerciseSessionTable.COLUMN_AVG_SPEED, exerciseSession.getAvgSpeed());
        }
        if (exerciseSession.getAvgAltitude() != null) {
            contentValues.put(ExerciseSessionTable.COLUMN_AVG_ALTITUDE, exerciseSession.getAvgAltitude());
        }
        if (exerciseSession.getTopAltitude() != null) {
            contentValues.put(ExerciseSessionTable.COLUMN_TOP_ALTITUDE, exerciseSession.getTopAltitude());
        }
        if (exerciseSession.getTopPace() != null) {
            contentValues.put(ExerciseSessionTable.COLUMN_TOP_PACE, exerciseSession.getTopPace());
        }
        if (exerciseSession.getTopSpeed() != null) {
            contentValues.put(ExerciseSessionTable.COLUMN_TOP_SPEED, exerciseSession.getTopSpeed());
        }
        if (exerciseSession.getUserNote() != null) {
            contentValues.put(ExerciseSessionTable.COLUMN_USER_NOTE, exerciseSession.getUserNote());
        }
        if (exerciseSession.getUserId() != null) {
            contentValues.put(ExerciseSessionTable.COLUMN_USER_ID, exerciseSession.getUserId());
        }
        // contentValues.put(ExerciseSessionTable.COLUMN_YOUTUBE_ID, exerciseSession.getYoutubeId());
        return contentValues;
    }

    @NonNull
    private ContentValues toContentValues(com.warriorfitapp.model.v2.LocationInfo locationInfo) {
        ContentValues contentValues = new ContentValues();
        if (locationInfo.getLatitude() != null) {
            contentValues.put(LocationInfoTable.COLUMN_LATITUDE, locationInfo.getLatitude());
        }
        if (locationInfo.getLongitude() != null) {
            contentValues.put(LocationInfoTable.COLUMN_LONGITUDE, locationInfo.getLongitude());
        }
        if (locationInfo.getAltitude() != null) {
            contentValues.put(LocationInfoTable.COLUMN_ALTITUDE, locationInfo.getAltitude());
        }
        if (locationInfo.getAccuracy() != null) {
            contentValues.put(LocationInfoTable.COLUMN_ACCURACY, locationInfo.getAccuracy());
        }
        if (locationInfo.getBearing() != null) {
            contentValues.put(LocationInfoTable.COLUMN_BEARING, locationInfo.getBearing());
        }
        if (locationInfo.getSpeed() != null) {
            contentValues.put(LocationInfoTable.COLUMN_SPEED, locationInfo.getSpeed());
        }
        if (locationInfo.getPace() != null) {
            contentValues.put(LocationInfoTable.COLUMN_PACE, locationInfo.getPace());
        }
        if (locationInfo.getTimestamp() != null) {
            contentValues.put(LocationInfoTable.COLUMN_TIMESTAMP, locationInfo.getTimestamp());
        }
        return contentValues;
    }

    @NonNull
    private ContentValues toContentValues(com.warriorfitapp.model.v2.User newUser) {
        ContentValues contentValues = new ContentValues();
        if (newUser.getId() != null) {
            contentValues.put(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_ID, newUser.getId());
        }
        if (newUser.getUsername() != null) {
            contentValues.put(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_USERNAME, newUser.getUsername());
        }
        if (newUser.getDisplayName() != null) {
            contentValues.put(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_DISPLAY_NAME, newUser.getDisplayName());
        }
        if (newUser.getWeight() != null) {
            contentValues.put(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_WEIGHT, newUser.getWeight());
        }
        if (newUser.getHeight() != null) {
            contentValues.put(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_HEIGHT, newUser.getHeight());
        }
        if (newUser.getAge() != null) {
            contentValues.put(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_AGE, newUser.getAge());
        }
        contentValues.put(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_IS_MALE, newUser.isMale());
        if (newUser.getBirthday() != null) {
            contentValues.put(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_BIRTHDAY, newUser.getBirthday());
        }
        if (newUser.getAccountType() != null) {
            contentValues.put(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_ACCOUNT_TYPE, newUser.getAccountType().name());
        }
        contentValues.put(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_ACTIVE, newUser.isActive());
        if (newUser.getImageUri() != null) {
            contentValues.put(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_IMAGE_URI, newUser.getImageUri());
        }
        if (newUser.getCurrentBodyFat() != null) {
            contentValues.put(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_CURRENT_BODY_FAT, newUser.getCurrentBodyFat());
        }
        if (newUser.getDesiredBodyFat() != null) {
            contentValues.put(com.warriorfitapp.db.sqlite.schema.table.UserTable.COLUMN_DESIRED_BODY_FAT, newUser.getDesiredBodyFat());
        }
        return contentValues;
    }

    @NonNull
    private ContentValues toContentValues(com.warriorfitapp.model.v2.SocialProfile socialProfile, Long userId) {
        ContentValues contentValues = new ContentValues();
        if (socialProfile.getId() != null) {
            contentValues.put(com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.COLUMN_ID, socialProfile.getId());
        }
        if (userId != null) {
            contentValues.put(com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.COLUMN_USER_ID, userId);
        } else if (socialProfile.getUserId() != null) {
            contentValues.put(com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.COLUMN_USER_ID, socialProfile.getUserId());
        }
        if (socialProfile.getSocialId() != null) {
            contentValues.put(com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.COLUMN_SOCIAL_ID, socialProfile.getSocialId());
        }
        if (socialProfile.getType() != null) {
            contentValues.put(com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.COLUMN_TYPE, socialProfile.getType().name());
        }
        if (socialProfile.getUrl() != null) {
            contentValues.put(com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.COLUMN_URL, socialProfile.getUrl());
        }
        if (socialProfile.getEmail() != null) {
            contentValues.put(com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.COLUMN_EMAIL, socialProfile.getEmail());
        }
        contentValues.put(com.warriorfitapp.db.sqlite.schema.table.SocialProfileTable.COLUMN_IS_PRIMARY, socialProfile.isPrimary());
        return contentValues;
    }
}