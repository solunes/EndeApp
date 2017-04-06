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
    private int obsInd;
    private int obsLec;
    private int obsFac;
    private int obsCond;

    public enum Columns {
        id,
        ObsDes,
        ObsTip,
        ObsInd,
        ObsLec,
        ObsFac,
        ObsCond
    }

    public static Obs fromCursor(Cursor cursor) {
        Obs obs = new Obs();
        obs.setId(cursor.getInt(Columns.id.ordinal()));
        obs.setObsDes(cursor.getString(Columns.ObsDes.ordinal()));
        obs.setObsTip(cursor.getInt(Columns.ObsTip.ordinal()));
        obs.setObsInd(cursor.getInt(Columns.ObsInd.ordinal()));
        obs.setObsLec(cursor.getInt(Columns.ObsLec.ordinal()));
        obs.setObsFac(cursor.getInt(Columns.ObsFac.ordinal()));
        return obs;
    }

    public int getObsCond() {
        return obsCond;
    }

    public void setObsCond(int obsCond) {
        this.obsCond = obsCond;
    }

    public int getObsInd() {
        return obsInd;
    }

    public void setObsInd(int obsInd) {
        this.obsInd = obsInd;
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
