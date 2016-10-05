package com.solunes.endeapp.dataset;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.solunes.endeapp.activities.MainActivity;
import com.solunes.endeapp.models.DataModel;
import com.solunes.endeapp.models.DataObs;
import com.solunes.endeapp.models.Obs;
import com.solunes.endeapp.models.Parametro;
import com.solunes.endeapp.models.Tarifa;
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

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context context) {
        dbHelper = new DBHelper(context);
    }

    public Cursor checkUser(String username, String password) {
        open();
        Cursor cursor = db.query(DBHelper.USER_TABLE, null,
                User.Columns.LecCod.name() + " = '" + username + "' AND " + User.Columns.LecPas.name() + " = '" + password + "'",
                null, null, null, null, null);
        cursor.moveToFirst();

        return cursor;
    }

    public User getUser(int id) {
        open();
        Cursor query = db.query(DBHelper.USER_TABLE, null, User.Columns.id.name() + " = " + id, null, null, null, null);
        query.moveToFirst();
        User user = User.fromCursor(query);
        query.close();
        return user;
    }

    public DBAdapter open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void clearTables() {
        open();
        db.delete(DBHelper.OBS_TABLE, null, null);
        db.delete(DBHelper.USER_TABLE, null, null);
        db.delete(DBHelper.TARIFA_TABLE, null, null);
        db.delete(DBHelper.ITEM_FACTURACION_TABLE, null, null);
        db.delete(DBHelper.PARAMETRO_TABLE, null, null);
    }

    public void saveObject(String table, ContentValues values) {
        open();
        db.insert(table, null, values);
    }

    public void updateData(int client, ContentValues contentValues) {
        open();
        db.update(DBHelper.DATA_TABLE, contentValues, DataModel.Columns.TlxCli.name() + " = " + client, null);
    }

    public ArrayList<DataModel> getAllData() {
        open();
        ArrayList<DataModel> dataModels = new ArrayList<>();
        Cursor query = db.query(DBHelper.DATA_TABLE, null, null, null, null, null, null);
        while (query.moveToNext()) {
            dataModels.add(DataModel.fromCursor(query));
        }
        query.close();
        return dataModels;
    }

    public ArrayList<DataModel> getReady() {
        open();
        ArrayList<DataModel> dataModels = new ArrayList<>();
        Cursor query = db.query(DBHelper.DATA_TABLE, null, DataModel.Columns.estado_lectura.name() + " = 1 " +
                "OR " + DataModel.Columns.estado_lectura.name() + " = 2", null, null, null, null);
        while (query.moveToNext()) {
            dataModels.add(DataModel.fromCursor(query));
        }
        query.close();
        return dataModels;
    }

    public ArrayList<DataModel> getState(int state) {
        open();
        ArrayList<DataModel> dataModels = new ArrayList<>();
        Cursor query = db.query(DBHelper.DATA_TABLE, null, DataModel.Columns.estado_lectura.name() + " = " + state, null, null, null, null);
        while (query.moveToNext()) {
            dataModels.add(DataModel.fromCursor(query));
        }
        query.close();
        return dataModels;
    }

    public int deleteAllData() {
        open();
        int delete = db.delete(DBHelper.DATA_TABLE, null, null);
        db.delete(DBHelper.HISTORICO_TABLE, null, null);
        return delete;
    }

    public DataModel getData(int id) {
        open();
        Cursor query = db.query(DBHelper.DATA_TABLE, null, DataModel.Columns._id.name() + " = " + id, null, null, null, null);
        query.moveToNext();
        DataModel dataModel = DataModel.fromCursor(query);
        query.close();
        return dataModel;
    }

    public int getSizeData() {
        open();
        Cursor query = db.rawQuery("select count(*) from " + DBHelper.DATA_TABLE, null);
        query.moveToNext();
        int size = query.getInt(0);
        query.close();
        return size;
    }

    public int getCountSave() {
        open();
        long ini = System.currentTimeMillis();
        Cursor cursor = db.rawQuery("select count(*) from " + DBHelper.DATA_TABLE + " " +
                "where " + DataModel.Columns.estado_lectura.name() + " = 1 OR " +
                DataModel.Columns.estado_lectura.name() + " = 2", null);
        long fin = System.currentTimeMillis();
        Log.e(TAG, "getCountSave: " + (fin - ini));
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public int getCountPrinted() {
        open();
        long ini = System.currentTimeMillis();
        Cursor query = db.query(DBHelper.DATA_TABLE, null, DataModel.Columns.estado_lectura.name() + " = 1", null, null, null, null);
        long fin = System.currentTimeMillis();
        Log.e(TAG, "getCountPrinted: " + (fin - ini));
        return query.getCount();
    }

    public int getCountPostponed() {
        open();
        long ini = System.currentTimeMillis();
        Cursor query = db.query(DBHelper.DATA_TABLE, null, DataModel.Columns.estado_lectura.name() + " = 2", null, null, null, null);
        long fin = System.currentTimeMillis();
        Log.e(TAG, "getCountPostponed: " + (fin - ini));
        return query.getCount();
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

    public Cursor getObs() {
        open();
        Cursor query = db.query(DBHelper.OBS_TABLE, null, null, null, null, null, null);
        return query;
    }

    public Cursor getObs(int obsCod) {
        open();
        Cursor query = db.query(DBHelper.OBS_TABLE, null, Obs.Columns.ObsCod.name() + " = " + obsCod, null, null, null, null);
        query.moveToNext();
        return query;
    }

    public Cursor getObs(String desc) {
        open();
        Cursor query = db.query(DBHelper.OBS_TABLE, null, Obs.Columns.ObsDes.name() + " = '" + desc + "'", null, null, null, null);
        query.moveToNext();
        return query;
    }

    public ArrayList<DataObs> getObsByCli(int cli) {
        open();
        ArrayList<DataObs> objects = new ArrayList<>();
        Cursor cursor = db.query(DBHelper.DATA_OBS_TABLE, null, DataObs.Columns.ObsCli.name() + " = " + cli, null, null, null, null);
        while (cursor.moveToNext()) {
            objects.add(DataObs.fromCursor(cursor));
        }
        return objects;
    }

    public ArrayList<Tarifa> getCargoEnergia(int categoria) {
        open();
        Cursor query = db.query(DBHelper.TARIFA_TABLE,
                null, Tarifa.Columns.categoria_tarifa_id.name() + " = " + categoria + "" +
                        " AND " + Tarifa.Columns.item_facturacion_id.name() + " > 1" +
                        " AND " + Tarifa.Columns.item_facturacion_id.name() + " < 8", null, null,
                null, Tarifa.Columns.kwh_desde.name() + " ASC");
        ArrayList<Tarifa> arrayList = new ArrayList<>();
        while (query.moveToNext()) {
            arrayList.add(Tarifa.fromCursor(query));
        }
        return arrayList;
    }

    public Cursor searchClienteMedidor(String filter, boolean isCli, int currentState) {
        open();
        String query;
        if (isCli) {
            query = DataModel.Columns.TlxCli.name() + " = '" + filter + "'";
        } else {
            query = DataModel.Columns.TlxNroMed.name() + " = '" + filter + "'";
        }
        if (currentState >= 0) {
            query = query + " AND ";
            switch (currentState) {
                case MainActivity.KEY_READY:
                    query = query + DataModel.Columns.estado_lectura.name() + " = 1 " +
                            "OR " + DataModel.Columns.estado_lectura.name() + " = 2";
                    break;
                case MainActivity.KEY_MISSING:
                    query = query + DataModel.Columns.estado_lectura.name() + " = 0";
                    break;
                case MainActivity.KEY_PRINT:
                    query = query + DataModel.Columns.estado_lectura.name() + " = 1";
                    break;
                case MainActivity.KEY_POSTPONED:
                    query = query + DataModel.Columns.estado_lectura.name() + " = 2";
                    break;
            }
        }
        Cursor cursor = db.query(DBHelper.DATA_TABLE, null, query, null, null, null, null);
        cursor.moveToNext();
        return cursor;
    }

    private static final int CONSUMO_ELEVADO = 1;
    private static final int CONSUMO_BAJO = 2;

    public double getConsumoElevado() {
        open();
        Cursor query = db.query(DBHelper.PARAMETRO_TABLE, null, Parametro.Columns.id.name() + " = " + CONSUMO_ELEVADO, null, null, null, null);
        query.moveToNext();
        return (query.getInt(Parametro.Columns.valor.ordinal()) / 100);
    }

    public double getBajoElevado() {
        open();
        Cursor query = db.query(DBHelper.PARAMETRO_TABLE, null, Parametro.Columns.id.name() + " = " + CONSUMO_BAJO, null, null, null, null);
        query.moveToNext();
        return (query.getInt(Parametro.Columns.valor.ordinal()) / 100);
    }

    public double getCargoFijo(int categoria) {
        open();
        Cursor cursor = db.query(DBHelper.TARIFA_TABLE, null, Tarifa.Columns.categoria_tarifa_id + " = " + categoria
                + " AND " + Tarifa.Columns.item_facturacion_id + " = 1", null, null, null, null);
        cursor.moveToNext();
        return cursor.getDouble(Tarifa.Columns.importe.ordinal());
    }

    public int getCargoFijoDescuento(int categoria) {
        open();
        Cursor cursor = db.query(DBHelper.TARIFA_TABLE, null, Tarifa.Columns.categoria_tarifa_id + " = " + categoria
                + " AND " + Tarifa.Columns.item_facturacion_id + " = 1", null, null, null, null);
        cursor.moveToNext();
        return cursor.getInt(Tarifa.Columns.kwh_desde.ordinal());
    }
}
