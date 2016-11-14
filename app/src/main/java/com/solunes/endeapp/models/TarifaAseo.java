package com.solunes.endeapp.models;

import android.database.Cursor;

/**
 * Created by jhonlimaster on 09-11-16.
 */

public class TarifaAseo {

    private int id;
    private int categoria_tarifa_id;
    private int anio;
    private int mes;
    private int kwh_desde;
    private int kwh_hasta;
    private double importe;

    public enum Columns{
        id,
        categoria_tarifa_id,
        anio,
        mes,
        kwh_desde,
        kwh_hasta,
        importe
    }

    public static TarifaAseo fromCursor(Cursor cursor){
        TarifaAseo tarifaAseo = new TarifaAseo();
        tarifaAseo.setId(cursor.getInt(Columns.id.ordinal()));
        tarifaAseo.setCategoria_tarifa_id(cursor.getInt(Columns.categoria_tarifa_id.ordinal()));
        tarifaAseo.setAnio(cursor.getInt(Columns.anio.ordinal()));
        tarifaAseo.setMes(cursor.getInt(Columns.mes.ordinal()));
        tarifaAseo.setKwh_desde(cursor.getInt(Columns.kwh_desde.ordinal()));
        tarifaAseo.setKwh_hasta(cursor.getInt(Columns.kwh_hasta.ordinal()));
        tarifaAseo.setImporte(cursor.getDouble(Columns.importe.ordinal()));
        return tarifaAseo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getKwh_desde() {
        return kwh_desde;
    }

    public void setKwh_desde(int kwh_desde) {
        this.kwh_desde = kwh_desde;
    }

    public int getKwh_hasta() {
        return kwh_hasta;
    }

    public void setKwh_hasta(int kwh_hasta) {
        this.kwh_hasta = kwh_hasta;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }
}
