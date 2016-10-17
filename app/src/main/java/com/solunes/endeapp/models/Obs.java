package com.solunes.endeapp.models;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jhonlimaster on 07-09-16.
 */
public class Obs {
    private int id;
    private String obsDes;
    private int obsTip;
    private int obsLec;
    private int obsFac;

    public enum Columns {
        id,
        ObsDes,
        ObsTip,
        ObsLec,
        ObsFac
    }

    public static Obs fromCursor(Cursor cursor) {
        Obs obs = new Obs();
        obs.setId(cursor.getInt(Columns.id.ordinal()));
        obs.setObsDes(cursor.getString(Columns.ObsDes.ordinal()));
        obs.setObsTip(cursor.getInt(Columns.ObsTip.ordinal()));
        obs.setObsLec(cursor.getInt(Columns.ObsLec.ordinal()));
        obs.setObsFac(cursor.getInt(Columns.ObsFac.ordinal()));
        return obs;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Columns.id.name(), getId());
            jsonObject.put(Columns.ObsDes.name(), getObsDes());
            jsonObject.put(Columns.ObsTip.name(), getObsTip());
            jsonObject.put(Columns.ObsLec.name(), getObsLec());
            jsonObject.put(Columns.ObsFac.name(), getObsFac());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getObsDes() {
        return obsDes;
    }

    public void setObsDes(String obsDes) {
        this.obsDes = obsDes;
    }

    public int getObsTip() {
        return obsTip;
    }

    public void setObsTip(int obsTip) {
        this.obsTip = obsTip;
    }

    public int getObsLec() {
        return obsLec;
    }

    public void setObsLec(int obsLec) {
        this.obsLec = obsLec;
    }

    public int getObsFac() {
        return obsFac;
    }

    public void setObsFac(int obsFac) {
        this.obsFac = obsFac;
    }
}
