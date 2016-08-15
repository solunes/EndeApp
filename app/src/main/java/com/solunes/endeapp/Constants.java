package com.solunes.endeapp;

/**
 * Created by jhonlimaster on 15-08-16.
 */
public class Constants {

    private static final String TAG = "Constants";

    // Con derecho a 20 kWh de consumo
    public static final double CARGO_MINIMO = 18.7850;

    // CARGO POR ENERGIA
    public static final double CARGO_ENERGIA_21_50 = 0.517;
    public static final double CARGO_ENERGIA_51_300 = 0.527;
    public static final double CARGO_ENERGIA_301_500= 0.548;
    public static final double CARGO_ENERGIA_501_ = 0.570;

    // MÁS CARGO POR CONEXIÓN (O RECONEXIÓN)
    public static final double CONEXION_RECONEXION = 55.0000;

    // MÁS INTERESES POR MORA
    public static final double MORA = 5.0000;

    // MÁS DÉBITO APLICADO POR AJUSTE ANTERIOR
    public static final double MAS_DEBITO = 0.0000;

    // MENOS CRÉDITO APLICADO POR AJUSTE ANTERIOR
    public static final double MENOS_CREDITO = 0.0000;

    // TASA ALUMBRADO PUBLICO
    public static final double ALUMBRADO_PUBLICO = 0.0615;

    // TASA DE ASEO
    public static final double TASA_ASEO = 0.0000;
}
