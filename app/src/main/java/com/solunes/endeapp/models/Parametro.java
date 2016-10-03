package com.solunes.endeapp.models;

import android.database.Cursor;

/**
 * Created by jhonlimaster on 29-09-16.
 */
public class Parametro {
    private int id;
    private int valor;
    private String texto;

    public enum Columns{
        id, valor, texto
    }

    public static Parametro fromCursor(Cursor cursor){
        Parametro parametro = new Parametro();
        parametro.setId(cursor.getInt(Columns.id.ordinal()));
        parametro.setValor(cursor.getInt(Columns.valor.ordinal()));
        parametro.setTexto(cursor.getString(Columns.texto.ordinal()));
        return parametro;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
