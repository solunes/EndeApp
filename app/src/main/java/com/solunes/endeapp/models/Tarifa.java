package com.solunes.endeapp.models;

import android.database.Cursor;

/**
 * Created by jhonlimaster on 06-09-16.
 */
public class Tarifa {
    private int id;
    private int categoria_tarifa_id;
    private int item_facturacion_id;
    private int kwh_desde;
    private int kwh_hasta;
    private double importe;

    public enum Columns {
        id, categoria_tarifa_id, item_facturacion_id, kwh_desde, kwh_hasta, importe
    }

    public static Tarifa fromCursor(Cursor cursor) {
        Tarifa tarifa = new Tarifa();
        tarifa.setId(cursor.getInt(Columns.id.ordinal()));
        tarifa.setCategoria_tarifa_id(cursor.getInt(Columns.categoria_tarifa_id.ordinal()));
        tarifa.setItem_facturacion_id(cursor.getInt(Columns.item_facturacion_id.ordinal()));
        tarifa.setKwh_desde(cursor.getInt(Columns.kwh_desde.ordinal()));
        tarifa.setKwh_hasta(cursor.getInt(Columns.kwh_hasta.ordinal()));
        tarifa.setImporte(cursor.getDouble(Columns.importe.ordinal()));
        return tarifa;
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

    public int getItem_facturacion_id() {
        return item_facturacion_id;
    }

    public void setItem_facturacion_id(int item_facturacion_id) {
        this.item_facturacion_id = item_facturacion_id;
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
