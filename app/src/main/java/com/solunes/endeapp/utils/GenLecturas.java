package com.solunes.endeapp.utils;

import com.solunes.endeapp.Constants;

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

    public static double subTotal(int kWhConsumo) {
        if (kWhConsumo <= 20) {
            return 0;
        }
        kWhConsumo = kWhConsumo - 20;
        double res = 0;
        if (kWhConsumo > 30) {
            res = Constants.CARGO_ENERGIA_21_50 * 30;
            kWhConsumo = kWhConsumo - 30;
        } else {
            res = Constants.CARGO_ENERGIA_21_50 * kWhConsumo;
            return round(res);
        }
        if (kWhConsumo > 250) {
            res = res + (Constants.CARGO_ENERGIA_51_300 * 250);
            kWhConsumo = kWhConsumo - 250;
        } else {
            res = res + (Constants.CARGO_ENERGIA_51_300 * kWhConsumo);
            return round(res);
        }
        if (kWhConsumo > 200) {
            res = res + (Constants.CARGO_ENERGIA_301_500 * 200);
            kWhConsumo = kWhConsumo - 200;
        } else {
            res = res + (Constants.CARGO_ENERGIA_301_500 * kWhConsumo);
            return round(res);
        }
        return round(res + (Constants.CARGO_ENERGIA_501_ * kWhConsumo));
    }

    public static double importeConsumo(int kWhConsumo) {
        return round(subTotal(kWhConsumo) + Constants.CARGO_MINIMO);
    }

    public static double tarifaDignidad(int kWhConsumo, double importeConsumo) {
        if (kWhConsumo <= 70) {
            return round(importeConsumo * -0.25);
        } else {
            return 0;
        }
    }

    public static double ley1886(int kWhConsumo) {
        if (kWhConsumo <= 100) {
            return round(-0.2 * (Constants.CARGO_MINIMO + subTotal(kWhConsumo)));
        } else {
            return round(-0.2 * (Constants.CARGO_MINIMO + (Constants.CARGO_ENERGIA_21_50 * 30) + (Constants.CARGO_ENERGIA_51_300 * 50)));
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

    public static double totalFacturar(double totalSuministroTap) {
        return round(totalSuministroTap + Constants.TASA_ASEO);
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
