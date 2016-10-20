package com.solunes.endeapp.models;

import android.database.Cursor;

/**
 * Created by jhonlimaster on 12-09-16.
 */
public class Historico {

    private int id;
    private int ConRem;
    private int ConAre;
    private int ConCli;
    private String ConMes01;
    private int ConKwh01;
    private String ConMes02;
    private int ConKwh02;
    private String ConMes03;
    private int ConKwh03;
    private String ConMes04;
    private int ConKwh04;
    private String ConMes05;
    private int ConKwh05;
    private String ConMes06;
    private int ConKwh06;
    private String ConMes07;
    private int ConKwh07;
    private String ConMes08;
    private int ConKwh08;
    private String ConMes09;
    private int ConKwh09;
    private String ConMes10;
    private int ConKwh10;
    private String ConMes11;
    private int ConKwh11;
    private String ConMes12;
    private int ConKwh12;

    public enum Columns {
        id, ConRem, ConAre, ConCli,
        ConMes01, ConKwh01, ConMes02, ConKwh02, ConMes03, ConKwh03, ConMes04, ConKwh04, ConMes05, ConKwh05, ConMes06, ConKwh06,
        ConMes07, ConKwh07, ConMes08, ConKwh08, ConMes09, ConKwh09, ConMes10, ConKwh10, ConMes11, ConKwh11, ConMes12, ConKwh12
    }

    public static Historico fromCursor(Cursor cursor) {
        Historico historico = new Historico();
        historico.setId(cursor.getInt(Columns.id.ordinal()));
        historico.setConRem(cursor.getInt(Columns.ConRem.ordinal()));
        historico.setConAre(cursor.getInt(Columns.ConAre.ordinal()));
        historico.setConCli(cursor.getInt(Columns.ConCli.ordinal()));
        historico.setConMes01(cursor.getString(Columns.ConMes01.ordinal()));
        historico.setConMes02(cursor.getString(Columns.ConMes02.ordinal()));
        historico.setConMes03(cursor.getString(Columns.ConMes03.ordinal()));
        historico.setConMes04(cursor.getString(Columns.ConMes04.ordinal()));
        historico.setConMes05(cursor.getString(Columns.ConMes05.ordinal()));
        historico.setConMes06(cursor.getString(Columns.ConMes06.ordinal()));
        historico.setConMes07(cursor.getString(Columns.ConMes07.ordinal()));
        historico.setConMes08(cursor.getString(Columns.ConMes08.ordinal()));
        historico.setConMes09(cursor.getString(Columns.ConMes09.ordinal()));
        historico.setConMes10(cursor.getString(Columns.ConMes10.ordinal()));
        historico.setConMes11(cursor.getString(Columns.ConMes11.ordinal()));
        historico.setConMes12(cursor.getString(Columns.ConMes12.ordinal()));
        historico.setConKwh01(cursor.getInt(Columns.ConKwh01.ordinal()));
        historico.setConKwh02(cursor.getInt(Columns.ConKwh02.ordinal()));
        historico.setConKwh03(cursor.getInt(Columns.ConKwh03.ordinal()));
        historico.setConKwh04(cursor.getInt(Columns.ConKwh04.ordinal()));
        historico.setConKwh05(cursor.getInt(Columns.ConKwh05.ordinal()));
        historico.setConKwh06(cursor.getInt(Columns.ConKwh06.ordinal()));
        historico.setConKwh07(cursor.getInt(Columns.ConKwh07.ordinal()));
        historico.setConKwh08(cursor.getInt(Columns.ConKwh08.ordinal()));
        historico.setConKwh09(cursor.getInt(Columns.ConKwh09.ordinal()));
        historico.setConKwh10(cursor.getInt(Columns.ConKwh10.ordinal()));
        historico.setConKwh11(cursor.getInt(Columns.ConKwh11.ordinal()));
        historico.setConKwh12(cursor.getInt(Columns.ConKwh12.ordinal()));
        return historico;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getConRem() {
        return ConRem;
    }

    public void setConRem(int conRem) {
        ConRem = conRem;
    }

    public int getConAre() {
        return ConAre;
    }

    public void setConAre(int conAre) {
        ConAre = conAre;
    }

    public int getConCli() {
        return ConCli;
    }

    public void setConCli(int conCli) {
        ConCli = conCli;
    }

    public String getConMes01() {
        return ConMes01;
    }

    public void setConMes01(String conMes01) {
        ConMes01 = conMes01;
    }

    public int getConKwh01() {
        return ConKwh01;
    }

    public void setConKwh01(int conKwh01) {
        ConKwh01 = conKwh01;
    }

    public String getConMes02() {
        return ConMes02;
    }

    public void setConMes02(String conMes02) {
        ConMes02 = conMes02;
    }

    public int getConKwh02() {
        return ConKwh02;
    }

    public void setConKwh02(int conKwh02) {
        ConKwh02 = conKwh02;
    }

    public String getConMes03() {
        return ConMes03;
    }

    public void setConMes03(String conMes03) {
        ConMes03 = conMes03;
    }

    public int getConKwh03() {
        return ConKwh03;
    }

    public void setConKwh03(int conKwh03) {
        ConKwh03 = conKwh03;
    }

    public String getConMes04() {
        return ConMes04;
    }

    public void setConMes04(String conMes04) {
        ConMes04 = conMes04;
    }

    public int getConKwh04() {
        return ConKwh04;
    }

    public void setConKwh04(int conKwh04) {
        ConKwh04 = conKwh04;
    }

    public String getConMes05() {
        return ConMes05;
    }

    public void setConMes05(String conMes05) {
        ConMes05 = conMes05;
    }

    public int getConKwh05() {
        return ConKwh05;
    }

    public void setConKwh05(int conKwh05) {
        ConKwh05 = conKwh05;
    }

    public String getConMes06() {
        return ConMes06;
    }

    public void setConMes06(String conMes06) {
        ConMes06 = conMes06;
    }

    public int getConKwh06() {
        return ConKwh06;
    }

    public void setConKwh06(int conKwh06) {
        ConKwh06 = conKwh06;
    }

    public String getConMes07() {
        return ConMes07;
    }

    public void setConMes07(String conMes07) {
        ConMes07 = conMes07;
    }

    public int getConKwh07() {
        return ConKwh07;
    }

    public void setConKwh07(int conKwh07) {
        ConKwh07 = conKwh07;
    }

    public String getConMes08() {
        return ConMes08;
    }

    public void setConMes08(String conMes08) {
        ConMes08 = conMes08;
    }

    public int getConKwh08() {
        return ConKwh08;
    }

    public void setConKwh08(int conKwh08) {
        ConKwh08 = conKwh08;
    }

    public String getConMes09() {
        return ConMes09;
    }

    public void setConMes09(String conMes09) {
        ConMes09 = conMes09;
    }

    public int getConKwh09() {
        return ConKwh09;
    }

    public void setConKwh09(int conKwh09) {
        ConKwh09 = conKwh09;
    }

    public String getConMes10() {
        return ConMes10;
    }

    public void setConMes10(String conMes10) {
        ConMes10 = conMes10;
    }

    public int getConKwh10() {
        return ConKwh10;
    }

    public void setConKwh10(int conKwh10) {
        ConKwh10 = conKwh10;
    }

    public String getConMes11() {
        return ConMes11;
    }

    public void setConMes11(String conMes11) {
        ConMes11 = conMes11;
    }

    public int getConKwh11() {
        return ConKwh11;
    }

    public void setConKwh11(int conKwh11) {
        ConKwh11 = conKwh11;
    }

    public String getConMes12() {
        return ConMes12;
    }

    public void setConMes12(String conMes12) {
        ConMes12 = conMes12;
    }

    public int getConKwh12() {
        return ConKwh12;
    }

    public void setConKwh12(int conKwh12) {
        ConKwh12 = conKwh12;
    }
}
