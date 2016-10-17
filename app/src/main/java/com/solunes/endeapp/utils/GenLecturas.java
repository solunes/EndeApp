package com.solunes.endeapp.utils;

import android.content.Context;
import android.util.Log;

import com.solunes.endeapp.Constants;
import com.solunes.endeapp.dataset.DBAdapter;
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

    public static double subTotal(Context context, int kWhConsumo, int categoria) {
        DBAdapter dbAdapter = new DBAdapter(context);
        Log.e(TAG, "subTotal: kwh " + kWhConsumo);
        int descuento = dbAdapter.getCargoFijoDescuento(categoria);
        if (kWhConsumo <= descuento) {
            return 0;
        }
        kWhConsumo = kWhConsumo - descuento;
        double res = 0;
        ArrayList<Tarifa> cargoEnergia = dbAdapter.getCargoEnergia(categoria);
        for (int i = 0; i < cargoEnergia.size(); i++) {
            Tarifa tarifa = cargoEnergia.get(i);
            if (kWhConsumo > (tarifa.getKwh_hasta() - descuento)) {
                res = res + tarifa.getImporte() * (tarifa.getKwh_hasta() - descuento);
                kWhConsumo -= (tarifa.getKwh_hasta() - descuento);
                descuento += tarifa.getKwh_hasta();
            } else {
                res = res + tarifa.getImporte() * kWhConsumo;
                Log.e(TAG, "subTotal: " + i + " - " + res);
                return round(res);
            }
//            }
            Log.e(TAG, "subTotal: " + i + " - " + res);
        }
        return 0;
    }

    public static double importeEnergia(Context context, int kWhConsumo, int categoria) {
        return round(subTotal(context, kWhConsumo, categoria));
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
            return round(-0.2 * (dbAdapter.getCargoFijo(categoria) + subTotal(context, kWhConsumo, categoria)));
        } else {
            ArrayList<Tarifa> cargoEnergia = dbAdapter.getCargoEnergia(categoria);
            return round(-0.2 * (dbAdapter.getCargoFijo(categoria) + (cargoEnergia.get(0).getImporte() * 30) + (cargoEnergia.get(1).getImporte() * 50)));
        }
    }

    public static double totalSuministro(double totalConsumo, double ley1886) {
        return round(totalConsumo + Constants.CONEXION_RECONEXION + Constants.MORA + Constants.MAS_DEBITO - Constants.MENOS_CREDITO - ley1886);
    }

    public static double totalSuministroTap(int kWhConsumo) {
        return round(kWhConsumo * Constants.ALUMBRADO_PUBLICO);
    }

    public static double totalSuministroAseo(int kWhConsumo) {
        return round(kWhConsumo * Constants.TASA_ASEO);
    }

    public static double totalFacturar(double totalSuministro, double tap, double aseo) {
        return round(totalSuministro + tap + aseo);
    }

    public static double totalConsumo(double importeConsumo, double tarifaDignidad) {
        return round(importeConsumo - tarifaDignidad);
    }

    public static double round(double value) {
        long factor = (long) Math.pow(10, 2);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
