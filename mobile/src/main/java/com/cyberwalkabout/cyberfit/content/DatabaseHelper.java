package com.cyberwalkabout.cyberfit.content;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cyberwalkabout.cyberfit.SplashScreen;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.DBSchema;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.ExerciseSessionTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.FavoriteExerciseTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.LocationInfoTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.SelectedProgramTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.SocialProfileTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.SubscribedProgramTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.UserTable;
import com.google.common.base.Joiner;
import com.google.common.collect.ObjectArrays;
import com.google.common.io.Files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

/**
 * @author Andrii Kovalov
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getSimpleName();

    //The Android's default system path of your application database.
    private static final String DB_NAME = "cyberfit_v2.db";

    private static final String ASSETS_DB_NAME = "cyberfit_v2.db.bundle";
    private static final String BACKUP_DB_NAME = "cyberfit_v2.db.bak";

    private Context context;

    private String dbDirPath;

    private SQLiteDatabase database;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     *
     * @param context
     */
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DBSchema.VERSION);
        this.context = context;
        this.dbDirPath = DatabaseUtil.getDatabaseDirPath(context);
    }

    public void init() throws IOException {
        createIfNeeded();
        open();
    }

    private boolean createIfNeeded() throws IOException {
        //If database not exists copy it from the assets
        if (!isDatabaseExists()) {
            this.getReadableDatabase();
            this.close();
            //Copy the database from assets
            copyDatabaseFromAssets(DB_NAME);
            Log.e(TAG, "database created");
            return true;
        }
        return false;
    }

    //Open the database, so we can execute it
    private boolean open() throws SQLException {
        String dbPath = dbDirPath + DB_NAME;
        Log.d(TAG, "Open '" + dbPath + "'");
        //Log.v("dbPath", dbPath);
        int flags = SQLiteDatabase.CREATE_IF_NECESSARY | SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE;
        database = SQLiteDatabase.openDatabase(dbPath, null, flags, new DatabaseErrorHandler() {
            @Override
            public void onCorruption(SQLiteDatabase dbObj) {
                Log.d(TAG, "onCorruption(" + dbObj + ")");
            }
        });
        return database != null;
    }

    //Check that the database exists here: /data/data/your package/databases/Da Name
    private boolean isDatabaseExists() {
        File dbFile = new File(dbDirPath + DB_NAME);
        //Log.v("dbFile", dbFile + "   "+ dbFile.exists());
        return dbFile.exists();
    }

    //Copy the database from assets
    private void copyDatabaseFromAssets(String dbName) throws IOException {
        String path = dbDirPath + dbName;
        Log.d(TAG, "Copy db from assets to '" + path + "'");
        InputStream in = context.getAssets().open(DB_NAME);
        OutputStream out = new FileOutputStream(path);

        byte[] mBuffer = new byte[1024];
        int length;
        while ((length = in.read(mBuffer)) > 0) {
            out.write(mBuffer, 0, length);
        }

        out.flush();
        out.close();
        in.close();
    }

    @Override
    public synchronized void close() {
        if (database != null) {
            database.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, final int newVersion) {
        Log.d(TAG, "onUpgrade: " + oldVersion + " -> " + newVersion);

        if (newVersion > DBSchema.PRE_RELEASE_1_VERSION) {
            try {
                copyDatabaseFromAssets(ASSETS_DB_NAME);

                File currentDbFile = new File(dbDirPath, DB_NAME);
                File backupDbFile = new File(dbDirPath, BACKUP_DB_NAME);
                File assetsDbFile = new File(dbDirPath, ASSETS_DB_NAME);

                Log.d(TAG, "Backup current db '" + currentDbFile + "' -> '" + backupDbFile + "'");
                Files.copy(currentDbFile, backupDbFile);

                try {
                    Log.d(TAG, "Open db '" + assetsDbFile.getAbsolutePath() + "'");
                    SQLiteDatabase newDb = context.openOrCreateDatabase(ASSETS_DB_NAME, Context.MODE_PRIVATE, null);
                    newDb.setVersion(newVersion);
                    newDb.execSQL("attach '" + backupDbFile.getAbsolutePath() + "' as currentDb");

                    restoreUser(oldVersion, newDb);
                    restoreExerciseHistory(oldVersion, newDb);
                    restoreSubscribedPrograms(newDb);
                    restoreSelectedPrograms(newDb);
                    restoreFavoriteExercises(newDb);

                    try {
                        Log.d(TAG, "Close '" + assetsDbFile.getAbsolutePath() + "'");
                        newDb.close();
                    } catch (Exception ignore) {
                    }

                    if (database != null) {
                        Log.d(TAG, "Close '" + currentDbFile.getAbsolutePath() + "'");
                        database.close();
                    }

                    if (currentDbFile.delete()) {
                        Log.d(TAG, "Deleted '" + currentDbFile + "'");
                    }
                    if (assetsDbFile.renameTo(currentDbFile)) {
                        Log.d(TAG, "Renamed '" + assetsDbFile + "' -> '" + currentDbFile + "'");
                    }

                    restartApp();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    if (assetsDbFile.delete()) {
                        Log.d(TAG, "Deleted '" + assetsDbFile + "'");
                    }
                    if (backupDbFile.delete()) {
                        Log.d(TAG, "Deleted '" + backupDbFile + "'");
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Failed to copy database from assets to " + context.getDatabasePath(ASSETS_DB_NAME), e);
            } catch (Exception e) {
                Log.e(TAG, "Unexpected error", e);
            }
        }
    }

    private void restoreFavoriteExercises(SQLiteDatabase newDb) {
        newDb.beginTransaction();
        try {
            Log.d(TAG, "Restore '" + FavoriteExerciseTable.TABLE_NAME + "'");
            newDb.execSQL("delete from " + FavoriteExerciseTable.TABLE_NAME);
            newDb.execSQL("insert into " + FavoriteExerciseTable.TABLE_NAME + " select * from currentDb." + FavoriteExerciseTable.TABLE_NAME);
            newDb.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(TAG, "Failed to restore '" + FavoriteExerciseTable.TABLE_NAME + "' data");
        } finally {
            newDb.endTransaction();
        }
    }

    private void restoreSelectedPrograms(SQLiteDatabase newDb) {
        newDb.beginTransaction();
        try {
            Log.d(TAG, "Restore '" + SelectedProgramTable.TABLE_NAME + "'");
            newDb.execSQL("delete from " + SelectedProgramTable.TABLE_NAME);
            newDb.execSQL("insert into " + SelectedProgramTable.TABLE_NAME + " select * from currentDb." + SelectedProgramTable.TABLE_NAME);
            newDb.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(TAG, "Failed to restore '" + SelectedProgramTable.TABLE_NAME + "' data");
        } finally {
            newDb.endTransaction();
        }
    }

    private void restoreSubscribedPrograms(SQLiteDatabase newDb) {
        newDb.beginTransaction();
        try {
            Log.d(TAG, "Restore '" + SubscribedProgramTable.TABLE_NAME + "'");
            newDb.execSQL("delete from " + SubscribedProgramTable.TABLE_NAME);
            newDb.execSQL("insert into " + SubscribedProgramTable.TABLE_NAME + " select * from currentDb." + SubscribedProgramTable.TABLE_NAME);
            newDb.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(TAG, "Failed to restore '" + SubscribedProgramTable.TABLE_NAME + "' data");
        } finally {
            newDb.endTransaction();
        }
    }

    private void restoreExerciseHistory(int oldVersion, SQLiteDatabase newDb) {
        newDb.beginTransaction();
        try {
            String[] exerciseSessionColumns = new String[]
                    {
                            ExerciseSessionTable.COLUMN_ID,
                            ExerciseSessionTable.COLUMN_REPETITIONS,
                            ExerciseSessionTable.COLUMN_DISTANCE,
                            ExerciseSessionTable.COLUMN_WEIGHT,
                            ExerciseSessionTable.COLUMN_TIME,
                            ExerciseSessionTable.COLUMN_EXERCISE_ID,
                            ExerciseSessionTable.COLUMN_TIMESTAMP_COMPLETED,
                            ExerciseSessionTable.COLUMN_STATE,
                            ExerciseSessionTable.COLUMN_TIMESTAMP_STARTED,
                            ExerciseSessionTable.COLUMN_LAST_TIMESTAMP_STARTED,
                            ExerciseSessionTable.COLUMN_AVG_PACE,
                            ExerciseSessionTable.COLUMN_AVG_SPEED,
                            ExerciseSessionTable.COLUMN_AVG_ALTITUDE,
                            ExerciseSessionTable.COLUMN_TOP_ALTITUDE,
                            ExerciseSessionTable.COLUMN_TOP_PACE,
                            ExerciseSessionTable.COLUMN_TOP_SPEED,
                            ExerciseSessionTable.COLUMN_USER_NOTE,
                            ExerciseSessionTable.COLUMN_USER_ID
                    };

            if (oldVersion >= DBSchema.RELEASE_1_VERSION) {
                exerciseSessionColumns = ObjectArrays.concat(exerciseSessionColumns, ExerciseSessionTable.COLUMN_LOWEST_ALTITUDE);
            }

            String exerciseSessionColumnsStr = Joiner.on(",").join(exerciseSessionColumns);

            Log.d(TAG, "Restore '" + ExerciseSessionTable.TABLE_NAME + "'");
            newDb.execSQL("insert into " + ExerciseSessionTable.TABLE_NAME +
                    " (" + exerciseSessionColumnsStr + ")" +
                    " select " + exerciseSessionColumnsStr + " from currentDb." + ExerciseSessionTable.TABLE_NAME);

            Log.d(TAG, "Restore '" + LocationInfoTable.TABLE_NAME + "'");
            newDb.execSQL("insert into " + LocationInfoTable.TABLE_NAME + " select * from currentDb." + LocationInfoTable.TABLE_NAME);

            newDb.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(TAG, "Failed to restore '" + ExerciseSessionTable.TABLE_NAME + "' data");
        } finally {
            newDb.endTransaction();
        }
    }

    private void restoreUser(int oldVersion, SQLiteDatabase newDb) {
        String[] userColumns = new String[]{
                UserTable.COLUMN_ID,
                UserTable.COLUMN_USERNAME,
                UserTable.COLUMN_DISPLAY_NAME,
                UserTable.COLUMN_WEIGHT,
                UserTable.COLUMN_HEIGHT,
                UserTable.COLUMN_AGE,
                UserTable.COLUMN_IS_MALE,
                UserTable.COLUMN_BIRTHDAY,
                UserTable.COLUMN_ACCOUNT_TYPE,
                UserTable.COLUMN_ACTIVE,
                UserTable.COLUMN_IMAGE_URI,
                UserTable.COLUMN_DATE_CREATED,
                UserTable.COLUMN_CURRENT_BODY_FAT,
                UserTable.COLUMN_DESIRED_BODY_FAT
        };

        if (oldVersion >= DBSchema.RELEASE_1_VERSION) {
            userColumns = ObjectArrays.concat(userColumns, new String[]{UserTable.COLUMN_WEIGHT, UserTable.COLUMN_HEIGHT, UserTable.COLUMN_UNITS_OF_MEASUREMENT, UserTable.COLUMN_DATE_FORMAT}, String.class);
        }

        newDb.beginTransaction();

        try {
            boolean success = true;

            try {
                String userColumnsStr = Joiner.on(",").join(userColumns);
                Log.d(TAG, "Restore '" + UserTable.TABLE_NAME + "'");
                newDb.execSQL("delete from " + UserTable.TABLE_NAME);
                newDb.execSQL("insert into " + UserTable.TABLE_NAME +
                        " (" + userColumnsStr + ") select " + userColumnsStr + " from currentDb." + UserTable.TABLE_NAME);
            } catch (SQLException e) {
                Log.e(TAG, "Failed to restore '" + UserTable.TABLE_NAME + "' data");
                success = false;
            }

            try {
                Log.d(TAG, "Restore '" + SocialProfileTable.TABLE_NAME + "'");
                newDb.execSQL("delete from " + SocialProfileTable.TABLE_NAME);
                newDb.execSQL("insert into " + SocialProfileTable.TABLE_NAME + " select * from currentDb." + SocialProfileTable.TABLE_NAME);
            } catch (SQLException e) {
                Log.e(TAG, "Failed to restore '" + SocialProfileTable.TABLE_NAME + "' data");
                success = false;
            }

            if (success) {
                newDb.setTransactionSuccessful();
            }
        } finally {
            newDb.endTransaction();
        }
    }

    private void restartApp() {
        Log.d(TAG, "Restart application to initialize upgraded database");

        Intent intent = new Intent(context, SplashScreen.class);
        int pendingIntentId = new Random().nextInt(Integer.MAX_VALUE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, pendingIntentId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
        System.exit(0);
    }


    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onDowngrade: " + oldVersion + " -> " + newVersion);
    }
}
