package com.solunes.endeapp.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.solunes.endeapp.dataset.DBAdapter;
import com.solunes.endeapp.dataset.DBHelper;
import com.solunes.endeapp.utils.GenLecturas;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jhonlimaster on 17-11-16.
 */

public class DetalleFactura {

    private static final String TAG = "DetalleFactura";
    private int id;
    private int generalId;
    private int itemFacturacionId;
    private double importe;
    private double impRedondeo;

    public enum Columns {
        id, general_id, item_facturacion_id, importe, imp_redondeo
    }

    public static DetalleFactura fromCursor(Cursor cursor) {
        DetalleFactura detalleFactura = new DetalleFactura();
        detalleFactura.setId(cursor.getInt(Columns.id.ordinal()));
        detalleFactura.setGeneralId(cursor.getInt(Columns.general_id.ordinal()));
        detalleFactura.setItemFacturacionId(cursor.getInt(Columns.item_facturacion_id.ordinal()));
        detalleFactura.setImporte(cursor.getDouble(Columns.importe.ordinal()));
        detalleFactura.setImpRedondeo(cursor.getDouble(Columns.imp_redondeo.ordinal()));
        return detalleFactura;
    }

    /**
     * Convierte el objeto en json
     */
    public String toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Columns.id.name(), getId());
            jsonObject.put(Columns.general_id.name(), getGeneralId());
            jsonObject.put(Columns.item_facturacion_id.name(), getItemFacturacionId());
            jsonObject.put(Columns.importe.name(), getImporte());
            jsonObject.put(Columns.imp_redondeo.name(), getImpRedondeo());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    /**
     * metodo para crear un nuevo detalle de factura y redondear el importe
     *
     * @param context
     * @param idData
     * @param idItem
     * @param importe
     * @return importe redondeado
     */
    public static double crearDetalle(Context context, int idData, int idItem, double importe) {
        //double importeRedondeado = GenLecturas.roundDecimal(importe, 1);
        double importeRedondeado = GenLecturas.roundDecimal(importe, 2);
        //double diferencia = importe - importeRedondeado;
        double diferencia = 0;

        DBAdapter dbAdapter = new DBAdapter(context);
        DetalleFactura detalleFactura = dbAdapter.getDetalleFactura(idData, idItem);

        ContentValues values = new ContentValues();
        values.put(Columns.general_id.name(), idData);
        values.put(Columns.item_facturacion_id.name(), idItem);
        values.put(Columns.importe.name(), importeRedondeado);
        values.put(Columns.imp_redondeo.name(), diferencia);
        if (detalleFactura == null) {
            dbAdapter.saveObject(DBHelper.DETALLE_FACTURA_TABLE, values);
        } else {
            dbAdapter.updateObject(DBHelper.DETALLE_FACTURA_TABLE, Columns.id.name(), detalleFactura.getId(), values);
        }
        dbAdapter.close();
        return importeRedondeado;
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

    public int getItemFacturacionId() {
        return itemFacturacionId;
    }

    public void setItemFacturacionId(int itemFacturacionId) {
        this.itemFacturacionId = itemFacturacionId;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public double getImpRedondeo() {
        return impRedondeo;
    }

    public void setImpRedondeo(double impRedondeo) {
        this.impRedondeo = impRedondeo;
    }
}
