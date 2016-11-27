package com.solunes.endeapp.models;

import android.database.Cursor;

/**
 * Created by jhonlimaster on 29-09-16.
 */
public class ItemFacturacion {
    private int id;
    private int codigo;
    private int concepto;
    private String descripcion;
    private int estado;
    private int creditoFiscal;

    public enum Columns{
        id, codigo, concepto, descripcion, estado, credito_fiscal
    }

    public static ItemFacturacion fromCursor(Cursor cursor){
        ItemFacturacion facturacion = new ItemFacturacion();
        facturacion.setId(cursor.getInt(Columns.id.ordinal()));
        facturacion.setCodigo(cursor.getInt(Columns.codigo.ordinal()));
        facturacion.setConcepto(cursor.getInt(Columns.concepto.ordinal()));
        facturacion.setDescripcion(cursor.getString(Columns.descripcion.ordinal()));
        facturacion.setEstado(cursor.getInt(Columns.estado.ordinal()));
        facturacion.setCreditoFiscal(cursor.getInt(Columns.credito_fiscal.ordinal()));
        return facturacion ;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public int getConcepto() {
        return concepto;
    }

    public void setConcepto(int concepto) {
        this.concepto = concepto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getCreditoFiscal() {
        return creditoFiscal;
    }

    public void setCreditoFiscal(int creditoFiscal) {
        this.creditoFiscal = creditoFiscal;
    }
}
