package com.cyberwalkabout.cyberfit.util;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Maria Dzyokh
 *         Writes/reads an object to/from a private local file
 */
public class LocalPersistStorage {

    private static final LocalPersistStorage INSTANCE = new LocalPersistStorage();

    public static LocalPersistStorage getInstance() {
        return INSTANCE;
    }

    private LocalPersistStorage() {
    }

    /**
     * @param context
     * @param object
     * @param filename
     */
    public void writeObjectToFile(Context context, Object object, String filename) {

        ObjectOutputStream objectOut = null;
        try {

            FileOutputStream fileOut = context.openFileOutput(filename, Context.MODE_PRIVATE);
            objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(object);
            fileOut.getFD().sync();

        } catch (IOException e) {
            Log.e(LocalPersistStorage.class.getSimpleName(), e.getMessage(), e);
        } finally {
            if (objectOut != null) {
                try {
                    objectOut.close();
                } catch (IOException e) {
                    Log.e(LocalPersistStorage.class.getSimpleName(), e.getMessage(), e);
                }
            }
        }
    }

    /**
     * @param context
     * @param filename
     * @return
     */
    public Object readObjectFromFile(Context context, String filename) {

        ObjectInputStream objectIn = null;
        Object object = null;
        try {

            FileInputStream fileIn = context.getApplicationContext().openFileInput(filename);
            objectIn = new ObjectInputStream(fileIn);
            object = objectIn.readObject();

        } catch (FileNotFoundException e) {
            Log.e(LocalPersistStorage.class.getSimpleName(), e.getMessage(), e);
        } catch (IOException e) {
            Log.e(LocalPersistStorage.class.getSimpleName(), e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            Log.e(LocalPersistStorage.class.getSimpleName(), e.getMessage(), e);
        } finally {
            if (objectIn != null) {
                try {
                    objectIn.close();
                } catch (IOException e) {
                    Log.e(LocalPersistStorage.class.getSimpleName(), e.getMessage(), e);
                }
            }
        }
        return object;
    }

    public boolean renameFile(Context context, String from, String to) {
        File oldFile = context.getFileStreamPath(from);
        File newFile = context.getFileStreamPath(to);
        return oldFile.renameTo(newFile);
    }

    public boolean fileExists(Context context, String fname) {
        File file = context.getFileStreamPath(fname);
        return file.exists();
    }

    public void deleteFile(Context context, String fileName) {
        File dir = context.getFilesDir();
        File file = new File(dir, fileName);
        file.delete();
    }

}
