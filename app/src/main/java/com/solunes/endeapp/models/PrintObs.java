package com.solunes.endeapp.models;

import android.database.Cursor;

/**
 * Created by jhonlimaster on 11-10-16.
 */

public class PrintObs {
    private int id;
    private String obiDes;

    public enum Columns {
        id, ObiDes
    }

    public static PrintObs fromCursor(Cursor cursor) {
        PrintObs printObs = new PrintObs();
        printObs.setId(cursor.getInt(Columns.id.ordinal()));
        printObs.setObiDes(cursor.getString(Columns.ObiDes.ordinal()));
        return printObs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getObiDes() {
        return obiDes;
    }

    public void setObiDes(String obiDes) {
        this.obiDes = obiDes;
    }
}
