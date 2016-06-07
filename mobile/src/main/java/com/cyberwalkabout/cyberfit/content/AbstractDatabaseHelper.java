package com.cyberwalkabout.cyberfit.content;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Andrii Kovalov
 */
public abstract class AbstractDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getSimpleName();

    private Context context;

    private String dbPath;

    private SQLiteDatabase database;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     *
     * @param context
     */
    public AbstractDatabaseHelper(Context context, String dbName, int dbVersion) {
        super(context, dbName, null, dbVersion);
        this.context = context;
        this.dbPath = DatabaseUtil.getDatabaseDirPath(context);
    }

    public boolean create() throws IOException {
        //If database not exists copy it from the assets
        if (!isDatabaseExists()) {
            this.getReadableDatabase();
            this.close();
            //Copy the database from assets
            copyDatabase();
            Log.e(TAG, "database created");
            return true;
        }
        return false;
    }

    //Open the database, so we can execute it
    public boolean open() throws SQLException {
        String mPath = dbPath + getDatabaseName();
        //Log.v("mPath", mPath);
        database = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        return database != null;
    }

    //Check that the database exists here: /data/data/your package/databases/Da Name
    private boolean isDatabaseExists() {
        File dbFile = new File(dbPath + getDatabaseName());
        //Log.v("dbFile", dbFile + "   "+ dbFile.exists());
        return dbFile.exists();
    }

    //Copy the database from assets
    private void copyDatabase() throws IOException {
        InputStream in = context.getAssets().open(getDatabaseName());
        OutputStream out = new FileOutputStream(dbPath + getDatabaseName());

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
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: " + oldVersion + " -> " + newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onDowngrade: " + oldVersion + " -> " + newVersion);
    }
}
