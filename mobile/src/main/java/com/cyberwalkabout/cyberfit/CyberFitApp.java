package com.cyberwalkabout.cyberfit;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.cyberwalkabout.cyberfit.db.sqlite.schema.DBSchema;
import com.cyberwalkabout.cyberfit.flurry.FlurryAdapter;
import com.cyberwalkabout.cyberfit.model.v2.Exercise;
import com.cyberwalkabout.cyberfit.model.v2.ExerciseSession;
import com.cyberwalkabout.cyberfit.model.v2.LocationInfo;
import com.cyberwalkabout.cyberfit.model.v2.Program;
import com.facebook.FacebookSdk;

import org.parceler.ParcelClass;
import org.parceler.ParcelClasses;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * @author Maria Dzyokh
 */
@ParcelClasses({
        @ParcelClass(Program.class),
        @ParcelClass(Exercise.class),
        @ParcelClass(ExerciseSession.class),
        @ParcelClass(LocationInfo.class)
})
public class CyberFitApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FlurryAdapter.getInstance().startSession(this); // We want analytics from beginning.
        FlurryAdapter.getInstance().logEventAppOpened();
        FacebookSdk.sdkInitialize(getApplicationContext()); //TODO Uki: init Fb only when needed
        //exportDbToSdCard();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * TODO: Provide explanation: if no longer used, then remove.
     */
    @Deprecated
    private void exportDbToSdCard() {
        try {
            File root = getFilesDir();//Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);//Environment.getDataDirectory(); // Environment.getExternalStorageDirectory();

            if (root.canWrite()) {
                String backupDbName = DBSchema.DEFAULT_NAME + DBSchema.DEFAULT_EXT;
                File currentDB = getDatabasePath(backupDbName);
                File backupDB = new File(root, backupDbName);

                Log.d("DB_EXPORT", backupDB.getAbsolutePath());

                backupDB.delete();

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            } else {
                Log.d("DB_EXPORT", "No write permissions to " + root.getAbsolutePath());
            }
        } catch (Exception e) {
            Log.e("DB_EXPORT", e.getMessage(), e);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}