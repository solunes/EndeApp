package com.solunes.endeapp.utils;

import android.util.Log;

import com.solunes.endeapp.models.DataModel;

import java.util.Calendar;
import java.util.StringTokenizer;

/**
 * Created by jhonlimaster on 05-10-16.
 */
public class PrintGenerator {

    public static String creator(DataModel dataModel) {
        calcDays(dataModel.getTlxFecAnt(), dataModel.getTlxFecLec());
//        String toLetter = NumberToLetterConverter.convertNumberToLetter(459.5);

        String deudasEnergia = "";
        Double impTotFac = dataModel.getTlxImpFac() + dataModel.getTlxTap() + dataModel.getTlxImpAse();
        Double totalCancelar = impTotFac;

        if (dataModel.getTlxDeuEneC() > 0) {
            deudasEnergia = "T 7 0 45 925 Mas deuda(s) pendiente(s) de energia  (" + dataModel.getTlxDeuEneC() + ") Bs\r\n";
            deudasEnergia = deudasEnergia + "T 7 0 45 925 " + dataModel.getTlxDeuEneI() + "\r\n";
            totalCancelar += dataModel.getTlxDeuEneI();
        }
        String deudasAseo = "";
        if (dataModel.getTlxDeuAseC() > 0) {
            deudasAseo = "T 7 0 45 945 Deuda(s) pendiente(s) de tasa de aseo (" + dataModel.getTlxDeuAseC() + ") Bs\r\n";
            deudasAseo = deudasAseo + "T 7 0 45 945 " + dataModel.getTlxDeuAseI() + "\r\n";
            totalCancelar += dataModel.getTlxDeuAseI();
        }

        String cpclConfigLabel = "! 0 200 200 1670 1\r\n" +
                "ENCODING UTF-8\r\n" +

                "RIGHT 782\r\n" +
                "T 7 0 10 30 21348181\r\n" +
                "T 7 0 10 50 296401600000835\r\n" +

                "CENTER\r\n" +
                "T 7 0 10 70 13\r\n" +

                "LEFT\r\n" +
                "T 7 0 45 135 Fecha emision: \r\n" +
                "CENTER\r\n" +
                // TODO: 05-10-16 funciona para este tipo de fecha
                "T 7 0 50 135 " + dataModel.getTlxCiudad() + " " + dataModel.getTlxRem() + " DE " + mesString(dataModel.getTlxMes()) + " DE " + dataModel.getTlxAno() + "\r\n" +

                "LEFT\r\n" +
                "T 7 0 45 155 Nombre: " + dataModel.getTlxNom() + " \r\n" +

                "LEFT\r\n" +
                "T 7 0 45 175 NIT/CI:\r\n" +
                "T 7 0 280 175 Nro CLIENTE:\r\n" +
                "T 7 0 575 175 Nro MEDIDOR:\r\n" +
                "RIGHT 100\r\n" +
                "T 7 0 150 175 " + dataModel.getTlxCliNit() + "\r\n" +
                "T 7 0 445 175 " + dataModel.getTlxCli() + "\r\n" +
                "T 7 0 720 175 " + dataModel.getTlxNroMed() + "\r\n" +

                "LEFT\r\n" +
                "T 7 0 45 195 DIRECCION: " + dataModel.getTlxDir() + "\r\n" +

                "T 7 0 45 215 CIUDAD/LOCALIDAD: " + dataModel.getTlxCiudad() + "\r\n" +
                "T 7 0 450 215 ACTIVIDAD: " + dataModel.getTlxActivi() + "\r\n" +

                "T 7 0 45 235 REMESA/RUTA: " + dataModel.getTlxRem() + "/" + dataModel.getTlxRutO() + "\r\n" +
                "T 7 0 450 235 CARTA FACTURA:  \r\n" +

                "T 7 0 45 260 MES DE LA FACTURA: " + mesString(dataModel.getTlxMes()) + "-" + dataModel.getTlxAno() + "\r\n" +
                "T 7 0 430 260 CATEGORIA: " + dataModel.getTlxCtg() + "\r\n" +

                "T 7 0 45 280 FECHA DE LECTURA:\r\n" +
                "T 7 0 250 280 ANTERIOR:\r\n" +
                "T 7 0 530 280 ACTUAL:\r\n" +
                "RIGHT 100\r\n" +
                "T 7 0 375 280 " + formatedDate(dataModel.getTlxFecAnt()) + "\r\n" +
                "RIGHT 782\r\n" +
                "T 7 0 720 280 " + formatedDate(dataModel.getTlxFecLec()) + "\r\n" +

                "LEFT\r\n" +
                "T 7 0 45 300 LECTURA MEDIDOR:\r\n" +
                "T 7 0 250 300 ANTERIOR:\r\n" +
                "T 7 0 530 300 ACTUAL:\r\n" +
                "RIGHT 100\r\n" +
                "T 7 0 375 300 " + dataModel.getTlxUltInd() + "\r\n" +
                "RIGHT 782\r\n" +
                "T 7 0 720 300 " + dataModel.getTlxNvaLec() + "\r\n" +

                "LEFT\r\n" +
                // TODO: 05-10-16 especificar el tipo de lectura
                "T 7 0 45 320 TIPO LECTURA:  LECTURA NORMAL\r\n" +

                "T 7 0 45 340 ENERGIA CONSUMIDO EN (" + calcDays(dataModel.getTlxFecAnt(), dataModel.getTlxFecLec()) + ") DIAS\r\n" +
                "RIGHT 782\r\n" +
                "T 7 0 720 340 " + dataModel.getTlxConsumo() + " kWh\r\n" +

                "LEFT\r\n" +
                "T 7 0 45 360 TOTAL ENERGIA A FACTURAR:\r\n" +
                "RIGHT 782\r\n" +
                "T 7 0 720 360 " + dataModel.getTlxConsFacturado() + " kWh\r\n" +

                "LEFT\r\n" +
                "T 7 0 575 480 Bs\r\n" +
                "T 7 0 575 500 Bs\r\n" +
                "T 7 0 575 520 Bs\r\n" +
                "T 7 0 575 540 Bs\r\n" +
                "T 7 0 575 560 Bs\r\n" +
                "T 7 0 575 620 Bs\r\n" +
                "T 7 0 575 640 Bs\r\n" +
                "T 7 0 575 700 Bs\r\n" +

                "T 7 0 45 480 Cargo Fijo\r\n" +
                "T 7 0 45 500 Importe por energia\r\n" +
                "T 7 0 45 520 Importe por consumo\r\n" +
                "T 7 0 45 540 Importe total por consumo\r\n" +
                "T 7 0 45 560 Importe totla por el suministro\r\n" +
                "T 7 0 45 600 Tasas para el Gobierno Municipal\r\n" +
                "T 7 0 45 620 Por alumbrado publico\r\n" +
                "T 7 0 45 640 Por aseo urbano\r\n" +
                "T 7 0 45 700 Importe total factura\r\n" +

                "RIGHT 782\r\n" +
                "T 7 0 720 480 " + dataModel.getTlxCarFij() + "\r\n" +
                "T 7 0 720 500 " + dataModel.getTlxImpEn() + "\r\n" +
                "T 7 0 720 520 " + dataModel.getTlxImpEn() + "\r\n" +
                "T 7 0 720 540 " + dataModel.getTlxImpEn() + "\r\n" +
                "T 7 0 720 560 " + dataModel.getTlxImpFac() + "\r\n" +
                "T 7 0 720 620 " + dataModel.getTlxTap() + "\r\n" +
                "T 7 0 720 640 " + dataModel.getTlxImpAse() + "\r\n" +
                "T 7 0 720 700 " + impTotFac + "\r\n" +

                "LEFT\r\n" +
                "T 7 0 45 765 Son: " + NumberToLetterConverter.convertNumberToLetter(impTotFac) + "\r\n" +
                "T 7 0 45 1004 Son: " + NumberToLetterConverter.convertNumberToLetter(totalCancelar) + "\r\n" +


                "T 7 0 45 885 Importe del mes a cancelar:Bs\r\n" +
                "T 7 0 45 972 Importe total a cancelar: Bs\r\n" +
                "T 7 0 45 1035 Importe base para credito fiscal: Bs\r\n" +

                "RIGHT 782\r\n" +
                "T 7 0 45 885 " + impTotFac + "\r\n" +
                "T 7 0 45 972 " + totalCancelar + "\r\n" +
                "T 7 0 45 1035 " + dataModel.getTlxImpFac() + "\r\n" +

                deudasEnergia +
                deudasAseo +

//                    "FORM\r\n"+
                "PRINT\r\n";

        Log.e("TAG", "creator: " + cpclConfigLabel);
        return cpclConfigLabel;
    }

    private static String calcDays(String dateAnt, String dateLec) {
        Calendar calendarAnt = Calendar.getInstance();
        calendarAnt.setTime(StringUtils.formateStringFromDate(StringUtils.DATE_FORMAT, dateAnt));
        Calendar calendarLec = Calendar.getInstance();
        calendarLec.setTime(StringUtils.formateStringFromDate(StringUtils.DATE_FORMAT, dateLec));

        int days = calendarAnt.getActualMaximum(Calendar.DAY_OF_MONTH) - calendarAnt.get(Calendar.DAY_OF_MONTH) + calendarLec.get(Calendar.DAY_OF_MONTH);
        if (27 <= days && days <= 33) {
            return String.valueOf(days);
        } else {
            return "--";
        }
    }

    private static String mesString(int mes) {
        switch (mes) {
            case 1:
                return "ENERO";
            case 2:
                return "FEBRERO";
            case 3:
                return "MARZO";
            case 4:
                return "ABRIL";
            case 5:
                return "MAYO";
            case 6:
                return "JUNIO";
            case 7:
                return "JULIO";
            case 8:
                return "AGOSTO";
            case 9:
                return "SEPTIEMBRE";
            case 10:
                return "OCTUBRE";
            case 11:
                return "NOVIEMBRE";
            case 12:
                return "DICIEMBRE";
        }
        return "";
    }

    private static String formatedDate(String fecha) {
        String[] split = fecha.split("-");
        String year = split[0];
        String month = split[1];
        String day = split[2];
        return day + "-" + mesString(Integer.parseInt(month)).substring(0, 3) + "-" + year.substring(2);
    }
}
