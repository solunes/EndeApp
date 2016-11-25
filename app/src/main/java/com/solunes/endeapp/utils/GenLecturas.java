package com.solunes.endeapp.utils;

import android.content.Context;

import com.solunes.endeapp.dataset.DBAdapter;
import com.solunes.endeapp.models.DataModel;
import com.solunes.endeapp.models.DetalleFactura;
import com.solunes.endeapp.models.Tarifa;

import java.util.ArrayList;

/**
 * Created by jhonlimaster on 15-08-16.
 */
public class GenLecturas {

    private static final String TAG = "GenLecturas";

    public static int lecturaNormal(int lecturaAnterior, int lecturaActual, int nroDig) {
        if (lecturaActual < lecturaAnterior) {

            String.valueOf(lecturaAnterior).length();
            double pow = Math.pow(10, nroDig);
            return (int) (lecturaActual + (pow - lecturaAnterior));
        }
        return lecturaActual - lecturaAnterior;
    }

    public static double subTotal(Context context, int kWhConsumo, int categoria, int idData) {
        DBAdapter dbAdapter = new DBAdapter(context);
        // se define la cantidad de kwh que ya se contabilizaron en el cargo fijo
        int descuento = dbAdapter.getCargoFijoDescuento(categoria);
        // si el consumo es menor que el consumo del cargo fijo se devuelve 0
        if (kWhConsumo <= descuento) {
            return 0;
        }
        kWhConsumo = kWhConsumo - descuento;
        double res = 0;
        double resTotal = 0;
        boolean finish = false;
        // se obtienen los rangos de energia para la categoria
        ArrayList<Tarifa> cargoEnergia = dbAdapter.getCargoEnergia(categoria);
        for (int i = 0; i < cargoEnergia.size(); i++) {
            Tarifa tarifa = cargoEnergia.get(i);
            // se define si el consumo restante es mayor a todo el rango o no
            if (kWhConsumo > (tarifa.getKwh_hasta() - descuento)) {
                res = tarifa.getImporte() * (tarifa.getKwh_hasta() - descuento);
                kWhConsumo -= (tarifa.getKwh_hasta() - descuento);
                descuento += tarifa.getKwh_hasta();
            } else {
                res = tarifa.getImporte() * kWhConsumo;
                finish = true;
            }
            // se registra el detalle de factura para el rango
            if (idData > 0) {
                res = DetalleFactura.crearDetalle(context, idData, tarifa.getItem_facturacion_id(), res);
            }
            resTotal += res;
            // se finaliza el loop ya que no quedan rangos a descontar
            if (finish) {
                return round(resTotal);
            }
        }
        return 0;
    }

    public static double importeEnergia(Context context, int kWhConsumo, int categoria, int idData) {
        return round(subTotal(context, kWhConsumo, categoria, idData));
    }

    public static double tarifaDignidad(int kWhConsumo, double importeConsumo) {
        if (kWhConsumo <= 70) {
            return round(importeConsumo * -0.25);
        } else {
            return 0;
        }
    }

    public static double ley1886(Context context, int kWhConsumo, int categoria) {
        DBAdapter dbAdapter = new DBAdapter(context);
        if (kWhConsumo <= 100) {
            return round(-0.2 * (dbAdapter.getCargoFijo(categoria) + subTotal(context, kWhConsumo, categoria, 0)));
        } else {
            return round(-0.2 * (dbAdapter.getCargoFijo(categoria) + subTotal(context, 100, categoria, 0)));
        }
    }

    public static double totalSuministro(double totalConsumo, double ley1886, double cargoExtras) {
        return round(totalConsumo + cargoExtras + ley1886);
    }

    public static double totalSuministroTap(DataModel dataModel, Context context, double importeConsumo) {
        DBAdapter dbAdapter = new DBAdapter(context);
        double valorTAP = 0;
        if (dataModel.getTlxTap() != 0) {
            valorTAP = dbAdapter.getValorTAP(dataModel.getTlxAre(), dataModel.getTlxCtgTap(), dataModel.getTlxMes(), dataModel.getTlxAno());
        }
        dbAdapter.close();
        return round(importeConsumo * valorTAP);
    }

    public static double totalSuministroAseo(DataModel dataModel, Context context, int kWhConsumo) {
        DBAdapter dbAdapter = new DBAdapter(context);
        double importeAseo = 0;
        if (dataModel.getTlxCotaseo() != 0) {
            importeAseo = dbAdapter.getImporteAseo(dataModel.getTlxCtgAseo(), dataModel.getTlxMes(), dataModel.getTlxAno(), kWhConsumo);
        }
        dbAdapter.close();
        return round(importeAseo);
    }

    public static double totalFacturar(double totalSuministro, double tap, double aseo) {

        return round(totalSuministro + tap + aseo);
    }

    public static double totalConsumo(double importeConsumo, double tarifaDignidad) {
        return round(importeConsumo + tarifaDignidad);
    }

    public static double round(double value) {
        long factor = (long) Math.pow(10, 2);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static double roundDecimal(double value, int decimal) {
        long factor = (long) Math.pow(10, decimal);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
