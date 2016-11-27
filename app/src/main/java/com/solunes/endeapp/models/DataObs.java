package com.solunes.endeapp.models;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jhonlimaster on 16-09-16.
 */
public class DataObs {
    private int id;
    private int idData;
    private int obgCod;

    public enum Columns {
        id, general_id, observacion_id
    }

    public static DataObs fromCursor(Cursor cursor) {
        DataObs dataObs = new DataObs();
        dataObs.setId(cursor.getInt(Columns.id.ordinal()));
        dataObs.setIdData(cursor.getInt(Columns.general_id.ordinal()));
        dataObs.setObgCod(cursor.getInt(Columns.observacion_id.ordinal()));
        return dataObs;
    }

    /**
     * COnvierte el objeto en json
     */
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Columns.id.name(), getId());
            jsonObject.put(Columns.general_id.name(), getIdData());
            jsonObject.put(Columns.observacion_id.name(), getObgCod());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public int getIdData() {
        return idData;
    }

    public void setIdData(int idData) {
        this.idData = idData;
    }

    public int getObgCod() {
        return obgCod;
    }

    public void setObgCod(int obgCod) {
        this.obgCod = obgCod;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
