package com.solunes.endeapp.dataset;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.solunes.endeapp.models.DataModel;
import com.solunes.endeapp.models.User;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by jhonlimaster on 11-08-16.
 */
public class DBAdapter {

    private static final String TAG = "DBAdapter";
    private static final String TABLE_USER = "user_table";
    private static final String TABLE_DATA = "data_table";

    private DBHelper dbHelper;
    private Context context;
    private SQLiteDatabase db;

    public DBAdapter(Context context) {
        dbHelper = new DBHelper(context);
        this.context = context;
    }

    public boolean checkUser(String username, String password) {
        open();
        try {
            password = SHA1(password);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Cursor cursor = db.query(true, TABLE_USER, null,
                User.Columns.username.name() + " = '" + username + "' AND " + User.Columns.password.name() + " = '" + password + "'",
                null, null, null, null, null);
        cursor.moveToFirst();

        return cursor.getCount() > 0;
    }


    public DBAdapter open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void saveDataObject(ContentValues values) {
        open();
        long insert = db.insert(TABLE_DATA, null, values);
        Log.e(TAG, "saveDataObject: " + insert);
    }

    public void updateData(int client, ContentValues contentValues){
        open();
        int update = db.update(TABLE_DATA, contentValues, "TlxCli = " + client, null);
        Log.e(TAG, "updateData: " + update);
    }

    public ArrayList<DataModel> getAllData() {
        open();
        ArrayList<DataModel> dataModels = new ArrayList<>();
        Cursor query = db.query(TABLE_DATA, null, null, null, null, null, null);
        while (query.moveToNext()) {
            dataModels.add(DataModel.fromCursor(query));
        }
        query.close();
        return dataModels;
    }

    public int deleteAllData() {
        open();
        int delete = db.delete(TABLE_DATA, null, null);
        return delete;
    }

    public DataModel getData(int id) {
        open();
        Cursor query = db.query(TABLE_DATA, null, DataModel.Columns._id.name() + " = " + id, null, null, null, null);
        query.moveToNext();
        DataModel dataModel = DataModel.fromCursor(query);
        query.close();
        return dataModel;
    }

    public int getSizeData() {
        open();
        Cursor query = db.rawQuery("select count(*) from " + TABLE_DATA, null);
        query.moveToNext();
        int size = query.getInt(0);
        query.close();
        return size;
    }

    public int getCountSave(){
        open();
        Cursor cursor = db.rawQuery("select count(*) from " + TABLE_DATA + " where not "+DataModel.Columns.TlxNvaLec.name()+" = 0", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public void close() {
        dbHelper.close();
    }

    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }
}
