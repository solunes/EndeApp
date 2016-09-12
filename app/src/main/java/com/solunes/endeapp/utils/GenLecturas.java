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

    public static int lecturaNormal(int lecturaAnterior, int lecturaActual) {
        if (lecturaActual < lecturaAnterior) {
            String.valueOf(lecturaAnterior).length();
            double pow = Math.pow(10, String.valueOf(lecturaAnterior).length());
            return (int) (lecturaActual + (pow - lecturaAnterior));
        }
        return lecturaActual - lecturaAnterior;
    }

    public static double subTotal(Context context, int kWhConsumo) {
        if (kWhConsumo <= 20) {
            return 0;
        }
        int descuento = 20;
        kWhConsumo = kWhConsumo - descuento;
        double res = 0;
        DBAdapter dbAdapter = new DBAdapter(context);
        ArrayList<Tarifa> cargoEnergia = dbAdapter.getCargoEnergia();
        for (int i = 0; i < cargoEnergia.size(); i++) {
            Tarifa tarifa = cargoEnergia.get(i);
            if (i == 0) {
                if (kWhConsumo > 30) {
                    res = tarifa.getImporte() * 30;
                    kWhConsumo = kWhConsumo - 30;
                } else {
                    res = tarifa.getImporte() * kWhConsumo;
                    Log.e(TAG, "subTotal: " + i + " - " + res);
                    return round(res);
                }
                descuento = 50;
            } else {
                if (tarifa.getKwh_hasta() == 0) {
                    Log.e(TAG, "subTotal: " + i + " - " + res);
                    return round(res + (tarifa.getImporte() * kWhConsumo));
                }
                if (kWhConsumo > (tarifa.getKwh_hasta() - descuento)) {
                    res = res + tarifa.getImporte() * (tarifa.getKwh_hasta() - descuento);
                    kWhConsumo -= (tarifa.getKwh_hasta() - descuento);
                    descuento += tarifa.getKwh_hasta();
                } else {
                    res = res + tarifa.getImporte() * kWhConsumo;
                    Log.e(TAG, "subTotal: "+ i +" - " + res);
                    return round(res);
                }
            }
            Log.e(TAG, "subTotal: " + i + " - " + res);
        }
        return 0;
    }

    public static double importeConsumo(Context context, int kWhConsumo) {
        return round(subTotal(context, kWhConsumo) + Constants.CARGO_MINIMO);
    }

    public static double tarifaDignidad(int kWhConsumo, double importeConsumo) {
        if (kWhConsumo <= 70) {
            return round(importeConsumo * -0.25);
        } else {
            return 0;
        }
    }

    public static double ley1886(Context context, int kWhConsumo) {
        if (kWhConsumo <= 100) {
            return round(-0.2 * (Constants.CARGO_MINIMO + subTotal(context, kWhConsumo)));
        } else {
            DBAdapter dbAdapter = new DBAdapter(context);
            ArrayList<Tarifa> cargoEnergia = dbAdapter.getCargoEnergia();
            return round(-0.2 * (Constants.CARGO_MINIMO + (cargoEnergia.get(0).getImporte() * 30) + (cargoEnergia.get(1).getImporte() * 50)));
        }
    }

    public static double totalSuministro(double totalConsumo) {
        return round(totalConsumo + Constants.CONEXION_RECONEXION + Constants.MORA + Constants.MAS_DEBITO - Constants.MENOS_CREDITO);
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

    public static double totalConsumo(double importeConsumo, double tarifaDignidad, double ley1886) {
        return round(importeConsumo + tarifaDignidad + ley1886);
    }

    private static double round(double value) {
        long factor = (long) Math.pow(10, 2);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
