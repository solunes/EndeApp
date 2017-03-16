package com.solunes.endeapp.utils;

import android.content.Context;
import android.util.Log;

import com.solunes.endeapp.dataset.DBAdapter;
import com.solunes.endeapp.models.DataModel;
import com.solunes.endeapp.models.DetalleFactura;
import com.solunes.endeapp.models.Parametro;
import com.solunes.endeapp.models.Tarifa;

import java.util.ArrayList;

/**
 * Esta clase tiene metodos para obtener importes y hace calculos de los subtotales y otros importes
 */
public class GenLecturas {

    private static final String TAG = "GenLecturas";

    /**
     * Este metodo hace la diferencia entre la lectura actual y la anterior, tambien calcula cuando
     * es giro de medidor
     *
     * @param lecturaAnterior lectura anterior
     * @param lecturaActual   tectura actual
     * @param nroDig          numero de digotos maximo
     * @return retorna el kwh de consumo para hacer los calculos
     */
    public static int lecturaNormal(int lecturaAnterior, int lecturaActual, int nroDig) {
        if (lecturaActual < lecturaAnterior) {
            String.valueOf(lecturaAnterior).length();
            double pow = Math.pow(10, nroDig);
            return (int) (lecturaActual + (pow - lecturaAnterior));
        }
        return lecturaActual - lecturaAnterior;
    }

    /**
     * Se hace el calculo del subtotal en los rangos definidos por en las tarifas.
     * Se utiliza para calcular el subtotal en energia y en potencia
     *
     * @param context    contexto de la aplicacion
     * @param kWhConsumo el kwh de consumo
     * @param categoria  la categoria
     * @param idData     un id de un DataModel
     * @return retorna un importe usado para posteriores calculos de los importes
     */
    private static double subTotal(Context context, int kWhConsumo, int categoria, int idData) {
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
        dbAdapter.close();
        for (int i = 0; i < cargoEnergia.size(); i++) {
            Tarifa tarifa = cargoEnergia.get(i);
            // se define si el consumo restante es mayor a todo el rango o no
            if (kWhConsumo > (tarifa.getKwh_hasta() - descuento)) {
                int diferencia = tarifa.getKwh_hasta() - tarifa.getKwh_desde() + 1;
                res = tarifa.getImporte() * diferencia;
                kWhConsumo -= diferencia;
                descuento += diferencia;
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

    /**
     * Calculo del importe de energia
     *
     * @param context    contexto de la aplicacion
     * @param kWhConsumo el kwh de consumo
     * @param categoria  la categoria
     * @param idData     identificador de un objeto DataModel
     * @return devuelve el subtotal y lo redondea
     */
    public static double importeEnergia(Context context, int kWhConsumo, int categoria, int idData) {
        return round(subTotal(context, kWhConsumo, categoria, idData));
    }

    /**
     * Calcula el descuento por tarifa dignidad
     *
     * @param kWhConsumo     el kwh de consumo
     * @param importeConsumo el importe por consumo
     * @return si en kwh de consumo es menor o igual a 70 se multiplica el importe por consumo por -0.25
     * se redondea y se devuelve, sino se retorna 0
     */
    public static double tarifaDignidad(Context context, int kWhConsumo, double importeConsumo) {
        DBAdapter dbAdapter = new DBAdapter(context);
        int limite = (int) dbAdapter.getParametroValor(Parametro.Values.dignidad_limite.name());
        double descuento = dbAdapter.getParametroValor(Parametro.Values.dignidad_descuento.name()) / 100;
        if (kWhConsumo <= limite) {
            return round(importeConsumo * -descuento);
        } else {
            return 0;
        }
    }

    /**
     * Calculo del descuento de Ley 1886
     *
     * @param context    contexto de la aplicacion
     * @param kWhConsumo los kwh de consumo
     * @param categoria  la categoria
     * @return hace el calculo correspondiente y lo devuelve redondeado
     */
    public static double ley1886(Context context, int kWhConsumo, int categoria) {
        DBAdapter dbAdapter = new DBAdapter(context);
        int limite = (int) dbAdapter.getParametroValor(Parametro.Values.limite_1886.name());
        double descuento = (dbAdapter.getParametroValor(Parametro.Values.descuento_1886.name()) / 100);
        if (kWhConsumo <= limite) {
            return round(-descuento * (dbAdapter.getCargoFijo(categoria) + subTotal(context, kWhConsumo, categoria, 0)));
        } else {
            return round(-descuento * (dbAdapter.getCargoFijo(categoria) + subTotal(context, 100, categoria, 0)));
        }
    }

    /**
     * Calcula el total suministro
     *
     * @param totalConsumo el valor del total por consumo
     * @param ley1886      el valor de ley1886
     * @param cargoExtras  cargos extras pre-calculados
     * @return suma todos los parametros y los devuelve redondeados
     */
    public static double totalSuministro(double totalConsumo, double ley1886, double cargoExtras) {
        return round(totalConsumo + cargoExtras + ley1886);
    }

    /**
     * Calcula el total del suministro al alumbrado publico
     *
     * @param dataModel      objeto DataModel para consultar a la base de datos
     * @param context        contexto de la aplicacion
     * @param importeEnergia importe por energia
     * @return si hay TAP busca el valor del TAP, y sino devuelve 0
     */
    public static double totalSuministroTap(DataModel dataModel, Context context, double importeEnergia) {
        DBAdapter dbAdapter = new DBAdapter(context);
        double valorTAP = 0;
        if (dataModel.getTlxTap() != 0) {
            valorTAP = dbAdapter.getValorTAP(dataModel.getTlxAre(), dataModel.getTlxCtgTap(), dataModel.getTlxMes(), dataModel.getTlxAno());
        }
        dbAdapter.close();
        return round(importeEnergia * valorTAP);
    }

    /**
     * Calcula la tarifa del aseo.
     *
     * @param dataModel  un objeto DataModel para obtener datos y hacer una consulta a la base de datos
     * @param context    contexto de la aplicacion
     * @param kWhConsumo los Kwh de consumo
     * @return si hay aseo, se obtiene la tarifa de aseo de la tabla tarifa_aseo, si no hay se devuelve 0
     */
    public static double totalSuministroAseo(DataModel dataModel, Context context, double kWhConsumo) {
        DBAdapter dbAdapter = new DBAdapter(context);
        double importeAseo = 0;
        if (dataModel.getTlxCotaseo() != 0) {
            importeAseo = dbAdapter.getImporteAseo(dataModel.getTlxCtgAseo(), dataModel.getTlxMes(), dataModel.getTlxAno(), kWhConsumo);
        }
        dbAdapter.close();
        return round(importeAseo);
    }

    /**
     * Calcula el monto total a facturar.
     *
     * @param totalSuministro el valor del total por suministro
     * @param tap             la tarifa del alumbrado publico
     * @param aseo            la tarifa de la tasa de aseo
     * @return hace una suma de los parametros y el redondeo
     */
    public static double totalFacturar(double totalSuministro, double tap, double aseo) {
        return round(totalSuministro + tap + aseo);
    }

    /**
     * Calcula el consumo total
     *
     * @param importeConsumo el importe por consumo
     * @param tarifaDignidad el valor de la tarifa dignidad
     * @return suma los dos parametros y los devuelve redondeado
     */
    public static double totalConsumo(double importeConsumo, double tarifaDignidad) {
        return round(importeConsumo + tarifaDignidad);
    }

    /**
     * Este metodo redondea a dos decimales
     *
     * @param value el valor double a redondear
     * @return retorna un double redondeado a dos decimales
     */
    public static double round(double value) {
        long factor = (long) Math.pow(10, 2);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    /**
     * Este metodo recondea decimales.
     *
     * @param value   el valor a redondear
     * @param decimal la cantidad de decimales a redondear
     * @return retorna un decimal redondeado
     */
    public static double roundDecimal(double value, int decimal) {
        boolean isNegative = false;
        if (value < 0) {
            value = Math.abs(value);
            isNegative = true;
        }
        long factor = (long) Math.pow(10, decimal);
        value = value * factor;
        long tmp = Math.round(value);
        double res = (double) tmp / factor;
        if (isNegative) {
            res = -res;
        }
        return res;
    }
}
