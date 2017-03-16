package com.solunes.endeapp.models;

import android.database.Cursor;

/**
 * Created by jhonlimaster on 09-03-17.
 */

public class Resultados {
    private int id;
    private int generalId;
    private int lectura;
    private int lecturaPotencia;
    private int observacion;

    public enum Columns {
        id, general_id, lectura, lectura_potencia, observacion
    }

    public static Resultados fromCursor(Cursor cursor) {
        Resultados resultados = new Resultados();
        resultados.setId(cursor.getInt(Columns.id.ordinal()));
        resultados.setGeneralId(cursor.getInt(Columns.general_id.ordinal()));
        resultados.setLectura(cursor.getInt(Columns.lectura.ordinal()));
        resultados.setLecturaPotencia(cursor.getInt(Columns.lectura_potencia.ordinal()));
        resultados.setObservacion(cursor.getInt(Columns.observacion.ordinal()));
        return resultados;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGeneralId() {
        return generalId;
    }

    public void setGeneralId(int generalId) {
        this.generalId = generalId;
    }

    public int getLectura() {
        return lectura;
    }

    public void setLectura(int lectura) {
        this.lectura = lectura;
    }

    public int getLecturaPotencia() {
        return lecturaPotencia;
    }

    public void setLecturaPotencia(int lecturaPotencia) {
        this.lecturaPotencia = lecturaPotencia;
    }

    public int getObservacion() {
        return observacion;
    }

    public void setObservacion(int observacion) {
        this.observacion = observacion;
    }
}
