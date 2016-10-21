package com.solunes.endeapp.models;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jhonlimaster on 19-10-16.
 */

public class MedEntreLineas {
    private int id;
    private int melRem;
    private int melAre;
    private int melMed;
    private int melLec;

    public static MedEntreLineas fromCursor(Cursor cursor) {
        MedEntreLineas entreLineas = new MedEntreLineas();
        entreLineas.setMelRem(cursor.getInt(Columns.MelRem.ordinal()));
        entreLineas.setMelAre(cursor.getInt(Columns.MelAre.ordinal()));
        entreLineas.setMelMed(cursor.getInt(Columns.MelMed.ordinal()));
        entreLineas.setMelLec(cursor.getInt(Columns.MelLec.ordinal()));
        return entreLineas;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Columns.MelRem.name(), getMelRem());
            jsonObject.put(Columns.MelAre.name(), getMelAre());
            jsonObject.put(Columns.MelMed.name(), getMelMed());
            jsonObject.put(Columns.MelLec.name(), getMelLec());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    public enum Columns {
        MelRem,
        MelAre,
        MelMed,
        MelLec
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMelRem() {
        return melRem;
    }

    public void setMelRem(int melRem) {
        this.melRem = melRem;
    }

    public int getMelAre() {
        return melAre;
    }

    public void setMelAre(int melAre) {
        this.melAre = melAre;
    }

    public int getMelMed() {
        return melMed;
    }

    public void setMelMed(int melMed) {
        this.melMed = melMed;
    }

    public int getMelLec() {
        return melLec;
    }

    public void setMelLec(int melLec) {
        this.melLec = melLec;
    }
}
