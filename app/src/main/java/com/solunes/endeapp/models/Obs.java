package com.solunes.endeapp.models;

import android.database.Cursor;

/**
 * Created by jhonlimaster on 07-09-16.
 */
public class Obs {
    private int obsCod;
    private String obsDes;
    private int obsTip;
    private int obsLec;
    private int obsFac;

    public enum Columns{
        ObsCod,
        ObsDes,
        ObsTip,
        ObsLec,
        ObsFac
    }

    public static Obs fromCursor(Cursor cursor){
        Obs obs = new Obs();
        obs.setObsCod(cursor.getInt(Columns.ObsCod.ordinal()));
        obs.setObsDes(cursor.getString(Columns.ObsDes.ordinal()));
        obs.setObsTip(cursor.getInt(Columns.ObsTip.ordinal()));
        obs.setObsLec(cursor.getInt(Columns.ObsLec.ordinal()));
        obs.setObsFac(cursor.getInt(Columns.ObsFac.ordinal()));
        return obs;
    }

    public int getObsCod() {
        return obsCod;
    }

    public void setObsCod(int obsCod) {
        this.obsCod = obsCod;
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
