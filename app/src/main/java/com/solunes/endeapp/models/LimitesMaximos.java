package com.solunes.endeapp.models;

import android.database.Cursor;

/**
 * Created by jhonlimaster on 23-11-16.
 */

public class LimitesMaximos {
    private int id;
    private int categoriaTarifaId;
    private int maxKwh;
    private int maxBs;

    public enum Columns {
        id,
        categoria_tarifa_id,
        max_kwh,
        max_bs
    }

    public static LimitesMaximos fromCursor(Cursor cursor) {
        LimitesMaximos maximos = new LimitesMaximos();
        maximos.setId(cursor.getInt(Columns.id.ordinal()));
        maximos.setCategoriaTarifaId(cursor.getInt(Columns.categoria_tarifa_id.ordinal()));
        maximos.setMaxKwh(cursor.getInt(Columns.max_kwh.ordinal()));
        maximos.setMaxBs(cursor.getInt(Columns.max_bs.ordinal()));
        return maximos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoriaTarifaId() {
        return categoriaTarifaId;
    }

    public void setCategoriaTarifaId(int categoriaTarifaId) {
        this.categoriaTarifaId = categoriaTarifaId;
    }

    public int getMaxKwh() {
        return maxKwh;
    }

    public void setMaxKwh(int maxKwh) {
        this.maxKwh = maxKwh;
    }

    public int getMaxBs() {
        return maxBs;
    }

    public void setMaxBs(int maxBs) {
        this.maxBs = maxBs;
    }
}
