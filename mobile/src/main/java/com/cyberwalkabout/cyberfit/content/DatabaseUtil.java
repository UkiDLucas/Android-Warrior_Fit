package com.cyberwalkabout.cyberfit.content;

import android.content.Context;

/**
 * @author Andrii Kovalov
 */
public class DatabaseUtil {
    public static String getDatabaseDirPath(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            return context.getApplicationInfo().dataDir + "/databases/";
        } else {
            return context.getFilesDir() + context.getPackageName() + "/databases/";
        }
    }
}
