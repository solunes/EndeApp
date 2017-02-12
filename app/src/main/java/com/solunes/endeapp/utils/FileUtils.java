package com.solunes.endeapp.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.Excluder;
import com.solunes.endeapp.dataset.DBHelper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;

/**
 * Created by jhonlimaster on 08-02-17.
 */

public class FileUtils {

    private static final String TAG = "FileUtils";
    private static final String SHARED_PREFERENCES_NAME = "com.solunes.endeapp_preferences.xml";
    private static final String INTERNAL_STORAGE = "/storage/emulated/0/endeapp";
    private static final String DATABSE_STORAGE = "/data/data/com.solunes.endeapp/databases";
    private static final String SHARED_PREFERENCES_STORAGE = "/data/data/com.solunes.endeapp/shared_prefs";

    public static void exportDB(String data, FileUtilsCallback fileUtilsCallback) {
        File file = new File(INTERNAL_STORAGE);
        if (!file.exists()) {
            file.mkdir();
        }
        boolean copyDB = copyFile(DATABSE_STORAGE + "/" + DBHelper.DATABASE_NAME, INTERNAL_STORAGE, DBHelper.DATABASE_NAME);
        boolean copySP = copyFile(INTERNAL_STORAGE + "/" + SHARED_PREFERENCES_NAME, data);
        if (copyDB && copySP) {
            fileUtilsCallback.suceess();
        } else {
            fileUtilsCallback.error();
        }
    }

    public static void importDB(Context context, FileUtilsCallback fileUtilsCallback) {
        boolean copyDB = copyFile(INTERNAL_STORAGE + "/" + DBHelper.DATABASE_NAME, DATABSE_STORAGE, DBHelper.DATABASE_NAME);
        boolean copySP = copyFile(INTERNAL_STORAGE + "/" + SHARED_PREFERENCES_NAME, context);
        if (copyDB && copySP) {
            fileUtilsCallback.suceess();
        } else {
            fileUtilsCallback.error();
        }
    }

    private static boolean copyFile(String dstPath, String data) {
        FileOutputStream outputStream;

        try {
            outputStream = new FileOutputStream(dstPath);
            outputStream.write(data.getBytes());
            outputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean copyFile(String srcPath, Context context) {
        String data = "";

        try {
            FileInputStream inputStream = new FileInputStream(srcPath);

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((receiveString = bufferedReader.readLine()) != null) {
                stringBuilder.append(receiveString);
            }

            inputStream.close();
            data = stringBuilder.toString();
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
            return false;
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
            return false;
        }

        Log.e(TAG, "copyFile: " + data);
        try {
            Gson gson = new Gson();
            AppPreferences appPreferences = gson.fromJson(data, AppPreferences.class);
            appPreferences.sendPreferences(context);
        } catch (Exception e) {
            Log.e(TAG, "copyFile: ex ", e);
        }

        return true;
    }

    private static boolean copyFile(String srcPath, String dstPath, String fileName) {
        Log.e(TAG, "copyFile: " + fileName);
        try {
            File srcFile = new File(srcPath);
            Log.e(TAG, "copyFile: srcfile " + srcFile.getAbsolutePath());
            File dstFile = new File(dstPath, fileName);
            Log.e(TAG, "copyFile: dstFile " + dstFile.getAbsolutePath());
            Log.e(TAG, "copyFile: dstFile " + dstFile.isFile());

            if (srcFile.exists()) {
                FileChannel src = new FileInputStream(srcFile).getChannel();
                FileChannel dst = new FileOutputStream(dstFile).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Log.e(TAG, "copyFile: finish " + fileName);
                return true;
            } else {
                Log.e(TAG, "copyFile: srcfile not exist");
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "copyFile: ", e);
            return false;
        }
    }

    public interface FileUtilsCallback {
        void suceess();

        void error();
    }
}
