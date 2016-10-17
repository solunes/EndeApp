package com.solunes.endeapp.models;

import android.database.Cursor;

/**
 * Created by jhonlimaster on 11-10-16.
 */

public class PrintObsData {
    private int id;
    private int oigRem;
    private int oigAre;
    private int oigCli;
    private int oigObs;

    public enum Columns {
        id,
        OigRem,
        OigAre,
        OigCli,
        OigObs
    }

    public static PrintObsData fromCursor(Cursor cursor) {
        PrintObsData obsData = new PrintObsData();
        obsData.setId(cursor.getInt(Columns.id.ordinal()));
        obsData.setOigRem(cursor.getInt(Columns.OigRem.ordinal()));
        obsData.setOigAre(cursor.getInt(Columns.OigAre.ordinal()));
        obsData.setOigCli(cursor.getInt(Columns.OigCli.ordinal()));
        obsData.setOigObs(cursor.getInt(Columns.OigObs.ordinal()));
        return obsData;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOigRem() {
        return oigRem;
    }

    public void setOigRem(int oigRem) {
        this.oigRem = oigRem;
    }

    public int getOigAre() {
        return oigAre;
    }

    public void setOigAre(int oigAre) {
        this.oigAre = oigAre;
    }

    public int getOigCli() {
        return oigCli;
    }

    public void setOigCli(int oigCli) {
        this.oigCli = oigCli;
    }

    public int getOigObs() {
        return oigObs;
    }

    public void setOigObs(int oigObs) {
        this.oigObs = oigObs;
    }
}
