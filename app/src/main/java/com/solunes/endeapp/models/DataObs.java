package com.solunes.endeapp.models;

import android.database.Cursor;

/**
 * Created by jhonlimaster on 16-09-16.
 */
public class DataObs {
    private int obsRem;
    private int obsAre;
    private int obsCli;
    private int obsCod;

    public enum Columns {
        ObsRem, ObsAre, ObsCli, ObsCod
    }

    public int getObsRem() {
        return obsRem;
    }

    public void setObsRem(int obsRem) {
        this.obsRem = obsRem;
    }

    public int getObsAre() {
        return obsAre;
    }

    public void setObsAre(int obsAre) {
        this.obsAre = obsAre;
    }

    public int getObsCli() {
        return obsCli;
    }

    public void setObsCli(int obsCli) {
        this.obsCli = obsCli;
    }

    public int getObsCod() {
        return obsCod;
    }

    public void setObsCod(int obsCod) {
        this.obsCod = obsCod;
    }

    public static DataObs fromCursor(Cursor cursor) {
        DataObs dataObs = new DataObs();
        dataObs.setObsRem(cursor.getInt(Columns.ObsRem.ordinal()));
        dataObs.setObsAre(cursor.getInt(Columns.ObsAre.ordinal()));
        dataObs.setObsCli(cursor.getInt(Columns.ObsCli.ordinal()));
        dataObs.setObsCod(cursor.getInt(Columns.ObsCod.ordinal()));
        return dataObs;
    }
}
