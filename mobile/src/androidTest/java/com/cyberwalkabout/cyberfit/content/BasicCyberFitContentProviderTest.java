package com.cyberwalkabout.cyberfit.content;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.AuthorTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.ExerciseTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.ExerciseToProgramTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.ProgramTable;
import com.google.common.collect.ObjectArrays;

/**
 * @author Andrii Kovalov
 */
public class BasicCyberFitContentProviderTest extends AndroidTestCase {

    private ContentResolver contentResolver;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        contentResolver = getContext().getContentResolver();
    }

    public void testQueryAllPrograms() throws Exception {
        Uri uri = Uri.withAppendedPath(CyberFitContentProvider.CONTENT_URI, ProgramTable.instance().getName());

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, ProgramTable.instance().getColumnNames(), null, null, null);

            assertNotNull(cursor);
            assertTrue(cursor.getCount() > 0);

            while (cursor.moveToNext()) {
                verifyProgramRow(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void testQueryAllProgramsWithAuthorName() throws Exception {
        Uri uri = UriHelper.getInstance().allProgramsWithAuthors(null);

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, ProgramTable.ALL_COLUMNS_JOIN_AUTHOR_NAME, null, null, null);

            assertNotNull(cursor);
            assertTrue(cursor.getCount() > 0);

            while (cursor.moveToNext()) {
                verifyProgramWithAuthorName(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void testQueryProgramById() throws Exception {
        int programId = 1;
        Uri uri = Uri.withAppendedPath(CyberFitContentProvider.CONTENT_URI, ProgramTable.instance().getName() + "/" + programId);

        Cursor cursor = null;

        try {
            cursor = contentResolver.query(uri, ProgramTable.instance().getColumnNames(), null, null, null);

            assertNotNull(cursor);
            assertEquals(1, cursor.getCount());
            assertTrue(cursor.moveToFirst());

            verifyProgramRow(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void testQueryAllAuthors() throws Exception {
        Uri uri = Uri.withAppendedPath(CyberFitContentProvider.CONTENT_URI, AuthorTable.instance().getName());

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, AuthorTable.instance().getColumnNames(), null, null, null);

            assertNotNull(cursor);
            assertTrue(cursor.getCount() > 0);

            while (cursor.moveToNext()) {
                verifyAuthorRow(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void testQueryAuthorById() throws Exception {
        int authorId = 1;
        Uri uri = Uri.withAppendedPath(CyberFitContentProvider.CONTENT_URI, AuthorTable.instance().getName() + "/" + authorId);

        Cursor cursor = null;

        try {
            cursor = contentResolver.query(uri, AuthorTable.instance().getColumnNames(), null, null, null);

            assertNotNull(cursor);
            assertEquals(1, cursor.getCount());
            assertTrue(cursor.moveToFirst());

            verifyAuthorRow(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void testQueryAllExercises() throws Exception {
        Uri uri = Uri.withAppendedPath(CyberFitContentProvider.CONTENT_URI, ExerciseTable.instance().getName());

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, ExerciseTable.instance().getColumnNames(), null, null, null);

            assertNotNull(cursor);
            assertTrue(cursor.getCount() > 0);

            while (cursor.moveToNext()) {
                verifyExerciseRow(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void testQueryAllExercisesWithProgramNames() throws Exception {
        Uri uri = UriHelper.getInstance().allExercisesWithProgramNames(null, false);

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, ObjectArrays.concat(ExerciseTable.instance().getColumnNames(), ExerciseTable.ALIAS_PROGRAM_NAMES), null, null, null);

            assertNotNull(cursor);
            assertTrue(cursor.getCount() > 0);

            while (cursor.moveToNext()) {
                verifyExerciseRowWithProgramNames(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void testQuerySelectedExercisesWithProgramNamesFilteredByName() throws Exception {
        String searchKeyword = "Knee warm up";
        Uri uri = UriHelper.getInstance().allExercisesWithProgramNames(searchKeyword, false);

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, ObjectArrays.concat(ExerciseTable.instance().getColumnNames(), ExerciseTable.ALIAS_PROGRAM_NAMES), null, null, null);

            assertNotNull(cursor);
            assertEquals(1, cursor.getCount());

            assertTrue(cursor.moveToFirst());
            verifyExerciseRowWithProgramNames(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void testQueryExerciseById() throws Exception {
        int exerciseId = 1;
        Uri uri = Uri.withAppendedPath(CyberFitContentProvider.CONTENT_URI, ExerciseTable.instance().getName() + "/" + exerciseId);

        Cursor cursor = null;

        try {
            cursor = contentResolver.query(uri, ExerciseTable.instance().getColumnNames(), null, null, null);

            assertNotNull(cursor);
            assertEquals(1, cursor.getCount());
            assertTrue(cursor.moveToFirst());

            verifyExerciseRow(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void testQueryAllExerciseToProgramRelationships() throws Exception {
        Uri uri = Uri.withAppendedPath(CyberFitContentProvider.CONTENT_URI, ExerciseToProgramTable.instance().getName());

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, ExerciseToProgramTable.instance().getColumnNames(), null, null, null);

            assertNotNull(cursor);
            assertTrue(cursor.getCount() > 0);

            while (cursor.moveToNext()) {
                verifyExerciseToProgramRow(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void testQueryProgramsByNameWildcard() throws Exception {
        Uri uri = Uri.withAppendedPath(CyberFitContentProvider.CONTENT_URI, ProgramTable.instance().getName());

        Cursor cursor = null;
        try {
            String searchKeyword = "Warm";
            cursor = contentResolver.query(uri, ProgramTable.instance().getColumnNames(), ProgramTable.COLUMN_NAME + " like ?", new String[]{"%" + searchKeyword + "%"}, null);

            assertNotNull(cursor);
            assertEquals(1, cursor.getCount());

            assertTrue(cursor.moveToNext());

            verifyProgramRow(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void testQueryProgramByNameWithUri() throws Exception {
        String searchKeyword = "Warm";

        Uri.Builder builder = CyberFitContentProvider.CONTENT_URI.buildUpon();
        builder.appendPath(ProgramTable.TABLE_NAME);
        builder.appendQueryParameter(ProgramTable.COLUMN_NAME, searchKeyword);

        Uri uri = builder.build();

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, ProgramTable.instance().getColumnNames(), null, null, null);

            assertNotNull(cursor);
            assertEquals(1, cursor.getCount());

            assertTrue(cursor.moveToNext());

            verifyProgramRow(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void testQueryExercisesByProgramId() throws Exception {
        long programId = 10;
        Uri uri = UriHelper.getInstance().exercisesByProgramId(programId, null);

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, ExerciseTable.ALL_COLUMNS_QUALIFIED, null, null, null);

            assertNotNull(cursor);
            assertTrue(cursor.getCount() > 0);

            while (cursor.moveToNext()) {
                verifyExerciseRow(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void verifyProgramRow(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(ProgramTable.COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndex(ProgramTable.COLUMN_NAME));
        String description = cursor.getString(cursor.getColumnIndex(ProgramTable.COLUMN_DESCRIPTION));
        int isActive = cursor.getInt(cursor.getColumnIndex(ProgramTable.COLUMN_ACTIVE));
        int isPremium = cursor.getInt(cursor.getColumnIndex(ProgramTable.COLUMN_PREMIUM));
        long authorId = cursor.getLong(cursor.getColumnIndex(ProgramTable.COLUMN_AUTHOR_ID));

        assertTrue(id > 0);
        assertTrue(authorId > 0);
        assertNotNull(name);
        assertNotNull(description);
        assertTrue(isActive == 1 || isActive == 0);
        assertTrue(isPremium == 1 || isPremium == 0);
    }

    private void verifyProgramWithAuthorName(Cursor cursor) {
        verifyProgramRow(cursor);

        String name = cursor.getString(cursor.getColumnIndex(ProgramTable.instance().qualifiedColumnName(ProgramTable.COLUMN_NAME)));
        String authorName = cursor.getString(cursor.getColumnIndex(ProgramTable.ALIAS_AUTHOR_NAME));

        assertNotNull(authorName);
        assertFalse(name.equals(authorName));
    }

    private void verifyAuthorRow(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(AuthorTable.COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndex(AuthorTable.COLUMN_NAME));

        assertTrue(id > 0);
        assertNotNull(name);
    }

    private void verifyExerciseRow(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(ExerciseTable.COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndex(ExerciseTable.COLUMN_NAME));
        String description = cursor.getString(cursor.getColumnIndex(ExerciseTable.COLUMN_DESCRIPTION));
        int isActive = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_ACTIVE));
        int displayOrder = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_DISPLAY_ORDER));
        String youtubeId = cursor.getString(cursor.getColumnIndex(ExerciseTable.COLUMN_YOUTUBE_ID));
        int trackDistance = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_TRACK_DISTANCE));
        int trackRepetitions = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_TRACK_REPETITIONS));
        int trackTime = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_TRACK_TIME));
        int trackWeight = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_TRACK_WEIGHT));

        assertTrue(id > 0);
        assertNotNull(name);
        assertNotNull(description);
        assertNotNull(youtubeId);
        assertTrue(displayOrder >= 0);
        assertTrue(isActive == 1 || isActive == 0);
        assertTrue(trackDistance == 1 || trackDistance == 0);
        assertTrue(trackRepetitions == 1 || trackRepetitions == 0);
        assertTrue(trackTime == 1 || trackTime == 0);
        assertTrue(trackWeight == 1 || trackWeight == 0);
    }

    private void verifyExerciseRowWithProgramNames(Cursor cursor) {
        verifyExerciseRow(cursor);

        // verify that we can retrieve program names but it can be null because exercise may not belong to any program
        cursor.getString(cursor.getColumnIndex(ExerciseTable.ALIAS_PROGRAM_NAMES));
    }

    private void verifyExerciseToProgramRow(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(ExerciseToProgramTable.COLUMN_ID));
        long exerciseId = cursor.getLong(cursor.getColumnIndex(ExerciseToProgramTable.COLUMN_EXERCISE_ID));
        long programId = cursor.getLong(cursor.getColumnIndex(ExerciseToProgramTable.COLUMN_PROGRAM_ID));

        assertTrue(id > 0);
        assertTrue(exerciseId > 0);
        assertTrue(programId > 0);
    }
}
