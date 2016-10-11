package com.solunes.endeapp.models;

import android.database.Cursor;

/**
 * Created by jhonlimaster on 11-10-16.
 */

public class PrintObs {
    private int obiId;
    private String obiDes;

    public enum Columns {
        id, ObiDes
    }

    public static PrintObs fromCursor(Cursor cursor) {
        PrintObs printObs = new PrintObs();
        printObs.setObiId(cursor.getInt(Columns.id.ordinal()));
        printObs.setObiDes(cursor.getString(Columns.ObiDes.ordinal()));
        return printObs;
    }

    public int getObiId() {
        return obiId;
    }

    public void setObiId(int obiId) {
        this.obiId = obiId;
    }

    public String getObiDes() {
        return obiDes;
    }

    public void setObiDes(String obiDes) {
        this.obiDes = obiDes;
    }
}
