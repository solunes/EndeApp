package com.solunes.endeapp.utils;

import android.util.Log;

import com.solunes.endeapp.models.DataModel;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by jhonlimaster on 05-10-16.
 */
public class PrintGenerator {

    private static final String TAG = "PrintGenerator";

    public static String creator(DataModel dataModel, ArrayList<String> printTitles, ArrayList<Double> printValues, double importeTotalFactura, double importeMesCancelar) {
        calcDays(dataModel.getTlxFecAnt(), dataModel.getTlxFecLec());
//        String toLetter = NumberToLetterConverter.convertNumberToLetter(459.5);
        String deudasEnergia = "";

        if (dataModel.getTlxDeuEneC() > 0) {
            deudasEnergia = "T CONSO2.CPF 0 45 925 Mas deuda(s) pendiente(s) de energia  (" + dataModel.getTlxDeuEneC() + ") Bs\r\n";
            deudasEnergia = deudasEnergia + "T CONSO2.CPF 0 45 925 " + dataModel.getTlxDeuEneI() + "\r\n";
        }
        String deudasAseo = "";
        if (dataModel.getTlxDeuAseC() > 0) {
            deudasAseo = "T CONSO2.CPF 0 45 945 Deuda(s) pendiente(s) de tasa de aseo (" + dataModel.getTlxDeuAseC() + ") Bs\r\n";
            deudasAseo = deudasAseo + "T CONSO2.CPF 0 45 945 " + dataModel.getTlxDeuAseI() + "\r\n";
        }

        String cpclConfigLabel = "! 0 200 200 1570 1\r\n" +
//                "ENCODING UTF-8\r\n" +

                "RIGHT 782\r\n" +
                "T CONSO2.CPF 0 10 30 21348181\r\n" +
                "T CONSO2.CPF 0 10 50 296401600000835\r\n" +

                "CENTER\r\n" +
                "T CONSO2.CPF 0 10 70 13\r\n" +

                // BLOQUE 1: DATOS DEL CONSUMIDOR
                "LEFT\r\n" +
                "T CONSO2.CPF 0 40 135 FECHA EMISIÓN: \r\n" +
                "CENTER\r\n" +
                "T CONSO2.CPF 0 50 135 " + dataModel.getTlxCiudad() + " " + dataModel.getTlxRem() + " DE " + mesString(dataModel.getTlxMes()) + " DE " + dataModel.getTlxAno() + "\r\n" +

                "LEFT\r\n" +
                "T CONSO2.CPF 0 40 154 Nombre: " + dataModel.getTlxNom() + " \r\n" +

                "LEFT\r\n" +
                "T CONSO2.CPF 0 40 173 NIT/CI:\r\n" +
                "T CONSO2.CPF 0 280 173 N° CLIENTE:\r\n" +
                "T CONSO2.CPF 0 575 173 N° MEDIDOR:\r\n" +
                "RIGHT 100\r\n" +
                "T CONSO2.CPF 0 150 173 " + dataModel.getTlxCliNit() + "\r\n" +
                "T CONSO2.CPF 0 445 173 " + dataModel.getTlxCli() + "\r\n" +
                "T CONSO2.CPF 0 720 173 " + dataModel.getTlxNroMed() + "\r\n" +

                "LEFT\r\n" +
                "T CONSO2.CPF 0 40 192 DIRECCIÓN: " + dataModel.getTlxDir() + "\r\n" +

                "T CONSO2.CPF 0 40 211 CIUDAD/LOCALIDAD: " + dataModel.getTlxCiudad().toUpperCase() + "\r\n" +
                "T CONSO2.CPF 0 450 211 ACTIVIDAD: " + dataModel.getTlxActivi() + "\r\n" +

                "T CONSO2.CPF 0 40 230 REMESA/RUTA: " + dataModel.getTlxRem() + "/" + dataModel.getTlxRutO() + "\r\n" +
                "T CONSO2.CPF 0 450 230 CARTA FACTURA:  \r\n" +

                // BLOQUE 2: DATOS DE MEDICION
                "T CONSO2.CPF 0 45 260 MES DE LA FACTURA: " + mesString(dataModel.getTlxMes()).toUpperCase() + "-" + dataModel.getTlxAno() + "\r\n" +
                "T CONSO2.CPF 0 430 260 CATEGORÍA: " + dataModel.getTlxSgl() + "\r\n" +

                "T CONSO2.CPF 0 45 280 FECHA DE LECTURA:\r\n" +
                "T CONSO2.CPF 0 250 280 ANTERIOR:\r\n" +
                "T CONSO2.CPF 0 530 280 ACTUAL:\r\n" +
                "RIGHT 100\r\n" +
                "T CONSO2.CPF 0 375 280 " + formatedDate(dataModel.getTlxFecAnt()) + "\r\n" +
                "RIGHT 782\r\n" +
                "T CONSO2.CPF 0 720 280 " + formatedDate(dataModel.getTlxFecLec()) + "\r\n" +

                "LEFT\r\n" +
                "T CONSO2.CPF 0 45 300 LECTURA MEDIDOR:\r\n" +
                "T CONSO2.CPF 0 250 300 ANTERIOR:\r\n" +
                "T CONSO2.CPF 0 530 300 ACTUAL:\r\n" +
                "RIGHT 100\r\n" +
                "T CONSO2.CPF 0 375 300 " + dataModel.getTlxUltInd() + "\r\n" +
                "RIGHT 782\r\n" +
                "T CONSO2.CPF 0 720 300 " + dataModel.getTlxNvaLec() + "\r\n" +

                "LEFT\r\n" +
                // TODO: 05-10-16 especificar el tipo de lectura
                "T CONSO2.CPF 0 45 320 TIPO LECTURA:  Lectura Normal\r\n" +

                "T CONSO2.CPF 0 45 340 Energía consumida en (" + calcDays(dataModel.getTlxFecAnt(), dataModel.getTlxFecLec()) + ") dias\r\n" +
                "RIGHT 782\r\n" +
                "T CONSO2.CPF 0 720 340 " + dataModel.getTlxConsumo() + " kWh\r\n" +

                "LEFT\r\n" +
                "T CONSO2.CPF 0 45 360 Total energía a facturar\r\n" +
                "RIGHT 782\r\n" +
                "T CONSO2.CPF 0 720 360 " + dataModel.getTlxConsFacturado() + " kWh\r\n" +

//
//                // BLOQUE 3: DETALLE DE FACTURACION
//                "LEFT\r\n" +
//                "T CONSO3.CPF 0 575 480 Bs\r\n" +
//                "T CONSO3.CPF 0 575 500 Bs\r\n" +
//                "T CONSO3.CPF 0 575 520 Bs\r\n" +
//                "T CONSO3.CPF 0 575 540 Bs\r\n" +
//                "T CONSO3.CPF 0 575 560 Bs\r\n" +
//                "T CONSO3.CPF 0 575 620 Bs\r\n" +
//                "T CONSO3.CPF 0 575 640 Bs\r\n" +
//                "T CONSO3.CPF 0 575 680 Bs\r\n" +
//
//                "T CONSO3.CPF 0 40 480 Cargo Fijo\r\n" +
//                "T CONSO3.CPF 0 40 500 Importe por energia\r\n" +
//                "T CONSO3.CPF 0 40 520 Importe por consumo\r\n" +
//                "T CONSO3.CPF 0 40 540 Importe total por consumo\r\n" +
//                "T CONSO3.CPF 0 40 560 Importe totla por el suministro\r\n" +
//                "T CONSO3.CPF 0 40 600 Tasas para el Gobierno Municipal\r\n" +
//                "T CONSO3.CPF 0 40 620 Por alumbrado publico\r\n" +
//                "T CONSO3.CPF 0 40 640 Por aseo urbano\r\n" +
//                "T CONSO3.CPF 0 40 680 Importe total factura\r\n" +
//
//                "RIGHT 782\r\n" +
//                "T CONSO3.CPF 0 720 480 " + dataModel.getTlxCarFij() + "\r\n" +
//                "T CONSO3.CPF 0 720 500 " + dataModel.getTlxImpEn() + "\r\n" +
//                "T CONSO3.CPF 0 720 520 " + dataModel.getTlxImpEn() + "\r\n" +
//                "T CONSO3.CPF 0 720 540 " + dataModel.getTlxImpEn() + "\r\n" +
//                "T CONSO3.CPF 0 720 560 " + dataModel.getTlxImpFac() + "\r\n" +
//                "T CONSO3.CPF 0 720 620 " + dataModel.getTlxTap() + "\r\n" +
//                "T CONSO3.CPF 0 720 640 " + dataModel.getTlxImpAse() + "\r\n" +
//                "T CONSO3.CPF 0 720 680 " + impTotFac + "\r\n" +
//                // + deposito de garantia (opcional)

                "LEFT\r\n" +
//                "T CONSO3.CPF 0 40 775 Son: " + NumberToLetterConverter.convertNumberToLetter(impTotFac) + "\r\n" +
                "T CONSO3.CPF 0 40 1004 Son: " + NumberToLetterConverter.convertNumberToLetter(dataModel.getTlxImpTot()) + "\r\n" +


                "T CONSO3.CPF 0 40 881 Importe del mes a cancelar:Bs\r\n" +
                "T CONSO3.CPF 0 40 968 Importe total a cancelar: Bs\r\n" +
                "T CONSO3.CPF 0 40 1035 Importe base para crédito fiscal: Bs\r\n" +

                "RIGHT 782\r\n" +
                "T CONSO3.CPF 0 45 881 " + importeMesCancelar + "\r\n" +
                "T CONSO3.CPF 0 45 968 " + dataModel.getTlxImpTot() + "\r\n" +
                "T CONSO3.CPF 0 45 1035 " + dataModel.getTlxImpFac() + "\r\n" +

                deudasEnergia +
                deudasAseo +


                // BLOQUE 4: QR, control code
                "LEFT\r\n" +
                "B QR 485 1070 M 2 U 4\r\n" +
                "MA,280048029|2228155|392401600024900|11/10/2016|3.00|3.00|E5-F8-BE-B3|0|0.00|0.00|0.00|0.00\r\n" +
                "ENDQR\r\n" +

                "T CONSO1.CPF 0 40 1100 CÓDIGO DE CONTROL:\r\n" +
                "T CONSO1.CPF 0 40 1130 FECHA LÍMITE DE EMISIÓN: \r\n" +

                "T CONSO2.CPF 0 270 1100 38-89-FC-GA-DT\r\n" +
                "T CONSO2.CPF 0 270 1130 21/02/12 \r\n" +

                "CENTER\r\n" +
                "T CONSO4.CPF 0 0 1230 'ESTA FACTURA CONTRIBUYE AL DESARROLLO DEL PAÍS. EL USO ILÍCITO DE ÉSTA SERÁ SANCIONADO DE ACUERDO A LA LEY'\n\r\n" +
                "T CONSO4.CPF 0 0 1244 Ley Nº 453: El proveedor debe exhibir certificaciones de habilitación o documentos que acrediten las capacidades\n\r\n" +
                "T CONSO4.CPF 0 0 1258 u ofertas de servicios especializados.\n\r\n" +

                "LEFT\r\n" +
                //BLOQUE FINAL: historico y talon
                "T CONSO1.CPF 0 80 1293 HISTÓRICO\r\n" +
                "T CONSO1.CPF 0 275 1293 HISTÓRICO\r\n" +
                "T CONSO1.CPF 0 475 1293 HISTÓRICO\r\n" +
                "T CONSO1.CPF 0 675 1293 HISTÓRICO\r\n" +

                "T CONSO0.CPF 0 40  1308 Mes/Año  Consumo kWh\r\n" +
                "T CONSO0.CPF 0 235 1308 Mes/Año  Consumo kWh\r\n" +
                "T CONSO0.CPF 0 435 1308 Mes/Año  Consumo kWh\r\n" +
                "T CONSO0.CPF 0 635 1308 Mes/Año  Consumo kWh\r\n" +

                "T CONSO1.CPF 0 40 1328 09/1015\r\n" +
                "T CONSO1.CPF 0 160 1328 123\r\n" +
                "T CONSO1.CPF 0 235 1328 09/1015\r\n" +
                "T CONSO1.CPF 0 360 1328 123\r\n" +
                "T CONSO1.CPF 0 435 1328 09/1015\r\n" +
                "T CONSO1.CPF 0 560 1328 123\r\n" +
                "T CONSO1.CPF 0 635 1328 09/1015\r\n" +
                "T CONSO1.CPF 0 755 1328 123\r\n" +

                "T CONSO1.CPF 0 40 1343 09/1015\r\n" +
                "T CONSO1.CPF 0 160 1343 123\r\n" +
                "T CONSO1.CPF 0 235 1343 09/1015\r\n" +
                "T CONSO1.CPF 0 360 1343 123\r\n" +
                "T CONSO1.CPF 0 435 1343 09/1015\r\n" +
                "T CONSO1.CPF 0 560 1343 123\r\n" +
                "T CONSO1.CPF 0 635 1343 09/1015\r\n" +
                "T CONSO1.CPF 0 755 1343 123\r\n" +

                "T CONSO1.CPF 0 40 1358 09/1015\r\n" +
                "T CONSO1.CPF 0 160 1358 123\r\n" +
                "T CONSO1.CPF 0 235 1358 09/1015\r\n" +
                "T CONSO1.CPF 0 360 1358 123\r\n" +
                "T CONSO1.CPF 0 435 1358 09/1015\r\n" +
                "T CONSO1.CPF 0 560 1358 123\r\n" +
                "T CONSO1.CPF 0 635 1358 09/1015\r\n" +
                "T CONSO1.CPF 0 755 1358 123\r\n" +

                "T CONSO0.CPF 0 55 1378 Fecha Vencimiento: \r\n" +
                "T CONSO1.CPF 0 210 1378 24/10/16\r\n" +
                "T CONSO0.CPF 0 300 1378 Fecha Est.Prox.Med: \r\n" +
                "T CONSO1.CPF 0 460 1378 25/10/16\r\n" +
                "T CONSO0.CPF 0 560 1378 Fecha Est.Prox.Emi: \r\n" +
                "T CONSO1.CPF 0 710 1378 25/10/16\r\n" +

                "T CONSO1.CPF 0 135 1430 SEP-2016\r\n" +
                "T CONSO1.CPF 0 300 1430 123123-1-1\r\n" +
                "T CONSO1.CPF 0 510 1430 999000\r\n" +
                "T CONSO1.CPF 0 720 1430 123.32\r\n";

        cpclConfigLabel += detalleFacturacion(printTitles, printValues, null, importeTotalFactura, importeMesCancelar, dataModel.getTlxImpTap(), dataModel.getTlxImpAse());
        cpclConfigLabel += "" +
                "FORM\r\n" +
                "PRINT\r\n";

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
                return "Enero";
            case 2:
                return "Febrero";
            case 3:
                return "Marzo";
            case 4:
                return "Abril";
            case 5:
                return "Mayo";
            case 6:
                return "Junio";
            case 7:
                return "Julio";
            case 8:
                return "Agosto";
            case 9:
                return "Septiembre";
            case 10:
                return "Octubre";
            case 11:
                return "Noviembre";
            case 12:
                return "Diciembre";
        }
        return "";
    }

    private static String formatedDate(String fecha) {
        String[] split = fecha.split("-");
        String year = split[0];
        String month = split[1];
        String day = split[2];
        return day + "-" + mesString(Integer.parseInt(month)).toUpperCase().substring(0, 3) + "-" + year.substring(2);
    }

    /**
     * Esta funcion se encarga de generar el bloque de detalle de facturacion
     *
     * @param titles   es la lista que tiene los campos para generar el bloque
     * @param garantia este parametro tiene el valir del deposito de garantia
     * @return el String generado para la impresion
     */
    public static String detalleFacturacion(ArrayList<String> titles, ArrayList<Double> values, String garantia, double impTotFac, double importeMes, double tap, double impAse) {
        String res = "";
//        String[] strings = (String[]) list.keySet().toArray();
        int yValue = 480;
//        String[] values = new String[]{"123.2", "123.4", "123.1", "123.3", "123.5"};
        for (int i = 0; i < titles.size(); i++) {
            Log.e(TAG, "detalleFacturacion: " + yValue);
            res += "LEFT\r\n";
            res += "T CONSO3.CPF 0 575 " + yValue + " Bs\r\n";
            res += "T CONSO3.CPF 0 40 " + yValue + " " + titles.get(i) + "\r\n";
            res += "RIGHT 782\r\n";
            res += "T CONSO3.CPF 0 720 " + yValue + " " + values.get(i) + "\r\n";
            yValue += 20;
        }

        yValue += 20;
        res += "LEFT\r\n";
        res += "T CONSO3.CPF 0 40 " + yValue + " Tasas para el Gobierno Municipal\r\n";

        yValue += 20;
        res += "T CONSO3.CPF 0 40 " + yValue + " Por alumbrado público\r\n";
        res += "T CONSO3.CPF 0 575 " + yValue + " Bs\r\n";
        res += "RIGHT 782\r\n";
        res += "T CONSO3.CPF 0 720 " + yValue + " " + tap + "\r\n";

        yValue += 20;
        res += "LEFT\r\n";
        res += "T CONSO3.CPF 0 575 " + yValue + " Bs\r\n";
        res += "T CONSO3.CPF 0 40 " + yValue + " Por aseo urbano\r\n";
        res += "RIGHT 782\r\n";
        res += "T CONSO3.CPF 0 720 " + yValue + " " + impAse + "\r\n";

        yValue += 40;
        res += "LEFT\r\n";
        res += "T CONSO3.CPF 0 575 " + yValue + " Bs\r\n";
        res += "T CONSO3.CPF 0 40 " + yValue + " Importe total factura\r\n";
        res += "RIGHT 782\r\n";
        res += "T CONSO3.CPF 0 720 " + yValue + " " + impTotFac + "\r\n";

        if (garantia != null) {
            yValue += 20;
            res += "LEFT\r\n";
            res += "T CONSO3.CPF 0 575 " + yValue + " Bs\r\n";
            res += "T CONSO3.CPF 0 40 " + yValue + " Más deposito de garantia\r\n";
            res += "RIGHT 782\r\n";
            res += "T CONSO3.CPF 0 720 " + yValue + " " + garantia + "\r\n";
        }

        yValue += 55;
        res += "LEFT\r\n";
        res += "T CONSO3.CPF 0 40 " + yValue + " Son: " + NumberToLetterConverter.convertNumberToLetter(importeMes) + "\r\n";

        return res;
    }
}