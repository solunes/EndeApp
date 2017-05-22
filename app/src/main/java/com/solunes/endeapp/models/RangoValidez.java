package com.solunes.endeapp.models;

import android.database.Cursor;

/**
 * Created by jhonlimaster on 19-05-17.
 */

public class RangoValidez {
    private int id;
    private int categoriaTarifaDd;
    private int valKwDesde;
    private int valKwHasta;
    private double valValor;
    private int valPorcentaje;

    public enum Columns {
        id,
        categoria_tarifa_id,
        val_kw_desde,
        val_kw_hasta,
        val_valor,
        val_porcentaje
    }

    public static RangoValidez fromCursor(Cursor cursor) {
        RangoValidez rangoValidez = new RangoValidez();
        rangoValidez.setId(cursor.getInt(Columns.id.ordinal()));
        rangoValidez.setCategoriaTarifaDd(cursor.getInt(Columns.categoria_tarifa_id.ordinal()));
        rangoValidez.setValKwDesde(cursor.getInt(Columns.val_kw_desde.ordinal()));
        rangoValidez.setValKwHasta(cursor.getInt(Columns.val_kw_hasta.ordinal()));
        rangoValidez.setValValor(cursor.getDouble(Columns.val_valor.ordinal()));
        rangoValidez.setValPorcentaje(cursor.getInt(Columns.val_porcentaje.ordinal()));
        return rangoValidez;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoriaTarifaDd() {
        return categoriaTarifaDd;
    }

    public void setCategoriaTarifaDd(int categoriaTarifaDd) {
        this.categoriaTarifaDd = categoriaTarifaDd;
    }

    public int getValKwDesde() {
        return valKwDesde;
    }

    public void setValKwDesde(int valKwDesde) {
        this.valKwDesde = valKwDesde;
    }

    public int getValKwHasta() {
        return valKwHasta;
    }

    public void setValKwHasta(int valKwHasta) {
        this.valKwHasta = valKwHasta;
    }

    public double getValValor() {
        return valValor;
    }

    public void setValValor(double valValor) {
        this.valValor = valValor;
    }

    public int getValPorcentaje() {
        return valPorcentaje;
    }

    public void setValPorcentaje(int valPorcentaje) {
        this.valPorcentaje = valPorcentaje;
    }
}
