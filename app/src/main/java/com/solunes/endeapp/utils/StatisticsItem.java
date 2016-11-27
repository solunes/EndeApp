package com.solunes.endeapp.utils;

/**
 * Created by jhonlimaster on 10-10-16.
 */

/**
 * Clase para las listas de estadisticas
 */
public class StatisticsItem {
    private String value;
    private int count;

    public StatisticsItem(String value, int count){
        this.value = value;
        this.count = count;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
