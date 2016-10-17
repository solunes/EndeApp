package com.solunes.endeapp.models;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jhonlimaster on 16-09-16.
 */
public class DataObs {
    private int id;
    private int obgRem;
    private int obgAre;
    private int obgCli;
    private int obgCod;

    public enum Columns {
        id, ObgRem, ObgCli, ObgAre, ObgObs
    }

    public int getObgRem() {
        return obgRem;
    }

    public void setObgRem(int obgRem) {
        this.obgRem = obgRem;
    }

    public int getObgAre() {
        return obgAre;
    }

    public void setObgAre(int obgAre) {
        this.obgAre = obgAre;
    }

    public int getObgCli() {
        return obgCli;
    }

    public void setObgCli(int obgCli) {
        this.obgCli = obgCli;
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

    public static DataObs fromCursor(Cursor cursor) {
        DataObs dataObs = new DataObs();
        dataObs.setId(cursor.getInt(Columns.id.ordinal()));
        dataObs.setObgRem(cursor.getInt(Columns.ObgRem.ordinal()));
        dataObs.setObgAre(cursor.getInt(Columns.ObgAre.ordinal()));
        dataObs.setObgCli(cursor.getInt(Columns.ObgCli.ordinal()));
        dataObs.setObgCod(cursor.getInt(Columns.ObgObs.ordinal()));
        return dataObs;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Columns.id.name(), getId());
            jsonObject.put(Columns.ObgRem.name(), getObgRem());
            jsonObject.put(Columns.ObgAre.name(), getObgAre());
            jsonObject.put(Columns.ObgCli.name(), getObgCli());
            jsonObject.put(Columns.ObgObs.name(), getObgCod());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
