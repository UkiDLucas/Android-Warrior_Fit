package com.cyberwalkabout.cyberfit.content;

import android.database.Cursor;
import android.test.AndroidTestCase;

import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.AndroidMetadataTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrii Kovalov
 */
public class DatabaseHelperTest extends AndroidTestCase {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseHelperTest.class);

    private DatabaseHelper databaseHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        databaseHelper = new DatabaseHelper(getContext());
        databaseHelper.init();

        LOG.debug(DatabaseHelper.class.getSimpleName() + " opened database");
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testQueryAndroidMetadata() throws Exception {
        Cursor cursor = null;
        try {
            cursor = databaseHelper.getReadableDatabase().query(AndroidMetadataTable.instance().getName(), AndroidMetadataTable.instance().getColumnNames(), null, null, null, null, null);

            assertNotNull(cursor);
            assertEquals(1, cursor.getCount());

            assertTrue(cursor.moveToFirst());

            assertNotNull(cursor.getString(0));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
