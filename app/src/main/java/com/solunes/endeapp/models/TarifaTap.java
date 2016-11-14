package com.solunes.endeapp.models;

import android.database.Cursor;

/**
 * Created by jhonlimaster on 09-11-16.
 */

public class TarifaTap {
    private int id;
    private int area_id;
    private int categoria_tarifa_id;
    private int anio;
    private int mes;
    private double valor;

    public enum Columns {
        id,
        area_id,
        categoria_tarifa_id,
        anio,
        mes,
        valor
    }

    public static TarifaTap fromCursor(Cursor cursor){
        TarifaTap tarifaTap = new TarifaTap();
        tarifaTap.setId(cursor.getInt(Columns.id.ordinal()));
        tarifaTap.setArea_id(cursor.getInt(Columns.area_id.ordinal()));
        tarifaTap.setCategoria_tarifa_id(cursor.getInt(Columns.categoria_tarifa_id.ordinal()));
        tarifaTap.setAnio(cursor.getInt(Columns.anio.ordinal()));
        tarifaTap.setMes(cursor.getInt(Columns.mes.ordinal()));
        tarifaTap.setValor(cursor.getDouble(Columns.valor.ordinal()));
        return tarifaTap;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getArea_id() {
        return area_id;
    }

    public void setArea_id(int area_id) {
        this.area_id = area_id;
    }

    public int getCategoria_tarifa_id() {
        return categoria_tarifa_id;
    }

    public void setCategoria_tarifa_id(int categoria_tarifa_id) {
        this.categoria_tarifa_id = categoria_tarifa_id;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
