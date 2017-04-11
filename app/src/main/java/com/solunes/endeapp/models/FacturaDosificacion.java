package com.solunes.endeapp.models;

import android.database.Cursor;

/**
 * Created by jhonlimaster on 04-11-16.
 */

public class FacturaDosificacion {
    private int id;
    private int numero;
    private int comprobante;
    private String fechaInicial;
    private String fechaLimiteEmision;
    private String numeroAutorizacion;
    private String llaveDosificacion;
    private int numeroFactura;
    private int estado;
    private String leyenda1;
    private String leyenda2;
    private String actividadEconomica;

    public enum Columns{
        id,
        numero,
        comprobante,
        fecha_inicial,
        fecha_limite_emision,
        numero_autorizacion,
        llave_dosificacion,
        numero_factura,
        estado,
        leyenda1,
        leyenda2,
        actividad_economica
    }

    public static FacturaDosificacion fromCursor(Cursor cursor){
        FacturaDosificacion dosificacion = new FacturaDosificacion();
        dosificacion.setId(cursor.getInt(Columns.id.ordinal()));
        dosificacion.setNumero(cursor.getInt(Columns.numero.ordinal()));
        dosificacion.setComprobante(cursor.getInt(Columns.comprobante.ordinal()));
        dosificacion.setFechaInicial(cursor.getString(Columns.fecha_inicial.ordinal()));
        dosificacion.setFechaLimiteEmision(cursor.getString(Columns.fecha_limite_emision.ordinal()));
        dosificacion.setNumeroAutorizacion(cursor.getString(Columns.numero_autorizacion.ordinal()));
        dosificacion.setLlaveDosificacion(cursor.getString(Columns.llave_dosificacion.ordinal()));
        dosificacion.setNumeroFactura(cursor.getInt(Columns.numero_factura.ordinal()));
        dosificacion.setEstado(cursor.getInt(Columns.estado.ordinal()));
        dosificacion.setLeyenda1(cursor.getString(Columns.leyenda1.ordinal()));
        dosificacion.setLeyenda2(cursor.getString(Columns.leyenda2.ordinal()));
        dosificacion.setActividadEconomica(cursor.getString(Columns.actividad_economica.ordinal()));
        return dosificacion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getComprobante() {
        return comprobante;
    }

    public void setComprobante(int comprobante) {
        this.comprobante = comprobante;
    }

    public String getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(String fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public String getFechaLimiteEmision() {
        return fechaLimiteEmision;
    }

    public void setFechaLimiteEmision(String fechaLimiteEmision) {
        this.fechaLimiteEmision = fechaLimiteEmision;
    }

    public String getNumeroAutorizacion() {
        return numeroAutorizacion;
    }

    public void setNumeroAutorizacion(String numeroAutorizacion) {
        this.numeroAutorizacion = numeroAutorizacion;
    }

    public String getLlaveDosificacion() {
        return llaveDosificacion;
    }

    public void setLlaveDosificacion(String llaveDosificacion) {
        this.llaveDosificacion = llaveDosificacion;
    }

    public int getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(int numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getLeyenda1() {
        return leyenda1;
    }

    public void setLeyenda1(String leyenda1) {
        this.leyenda1 = leyenda1;
    }

    public String getLeyenda2() {
        return leyenda2;
    }

    public void setLeyenda2(String leyenda2) {
        this.leyenda2 = leyenda2;
    }

    public String getActividadEconomica() {
        return actividadEconomica;
    }

    public void setActividadEconomica(String actividadEconomica) {
        this.actividadEconomica = actividadEconomica;
    }
}
