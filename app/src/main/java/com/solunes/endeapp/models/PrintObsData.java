package com.solunes.endeapp.models;

import android.database.Cursor;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jhonlimaster on 11-10-16.
 */

public class PrintObsData {
    private int id;
    private int idData;
    private int oigObs;

    public String toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Columns.id.name(), getId());
            jsonObject.put(Columns.general_id.name(), getIdData());
            jsonObject.put(Columns.observacion_imp_id.name(), getOigObs());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("TAG", "toJson: " + jsonObject.toString());
        return jsonObject.toString();
    }

    public enum Columns {
        id,
        general_id,
        observacion_imp_id
    }

    public static PrintObsData fromCursor(Cursor cursor) {
        PrintObsData obsData = new PrintObsData();
        obsData.setId(cursor.getInt(Columns.id.ordinal()));
        obsData.setIdData(cursor.getInt(Columns.general_id.ordinal()));
        obsData.setOigObs(cursor.getInt(Columns.observacion_imp_id.ordinal()));
        return obsData;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdData() {
        return idData;
    }

    public void setIdData(int idData) {
        this.idData = idData;
    }

    public int getOigObs() {
        return oigObs;
    }

    public void setOigObs(int oigObs) {
        this.oigObs = oigObs;
    }
}
