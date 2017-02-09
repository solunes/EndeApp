package com.solunes.endeapp.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.solunes.endeapp.dataset.DBHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by jhonlimaster on 08-02-17.
 */

public class FileUtils {

    public static void exportDB() {
        File file = new File("/storage/emulated/0/endeapp");
        if (!file.exists()) {
            file.mkdir();
        }
        copyFile("/data/data/com.solunes.endeapp/databases/" + DBHelper.DATABASE_NAME, "/storage/emulated/0/endeapp");
    }

    public static void importDB() {
        copyFile("/storage/emulated/0/endeapp/" + DBHelper.DATABASE_NAME, "/data/data/com.solunes.endeapp/databases");
    }

    private static void copyFile(String srcPath, String dstPath) {
        try {
            File srcFile = new File(srcPath);

            if (srcFile.canWrite()) {
                String backupDBPath = DBHelper.DATABASE_NAME;
                File dstFile = new File(dstPath, backupDBPath);
                Log.e(TAG, "copyFile: src: " + srcFile.getAbsolutePath());
                Log.e(TAG, "copyFile: dst: " + dstFile.getAbsolutePath());

                if (srcFile.exists()) {
                    FileChannel src = new FileInputStream(srcFile).getChannel();
                    FileChannel dst = new FileOutputStream(dstFile).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                } else {
                    Log.e(TAG, "copyFile: no existe srcFile");
                }
                Log.e(TAG, "copyFile: copiado");
            } else {
                Log.e(TAG, "copyFile: no se puede escribir");
            }
        } catch (Exception e) {
            Log.e(TAG, "copyFile: ", e);
        }
    }

    private static final String TAG = "FileUtils";
}
