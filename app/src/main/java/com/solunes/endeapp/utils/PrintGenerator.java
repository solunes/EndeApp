package com.solunes.endeapp.utils;

import android.util.Log;

import com.solunes.endeapp.models.DataModel;
import com.solunes.endeapp.models.Historico;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by jhonlimaster on 05-10-16.
 */
public class PrintGenerator {

    private static final String TAG = "PrintGenerator";

    public static String creator(DataModel dataModel,
                                 ArrayList<String> printTitles,
                                 ArrayList<Double> printValues,
                                 Historico historico,
                                 double importeTotalFactura,
                                 double importeMesCancelar,
                                 String[] leyenda) {
        calcDays(dataModel.getTlxFecAnt(), dataModel.getTlxFecLec());
//        String toLetter = NumberToLetterConverter.convertNumberToLetter(459.5);
        String deudasEnergia = "";

        if (dataModel.getTlxDeuEneC() > 0) {
            deudasEnergia = "T CONSO2.CPF 0 45 925 Mas deuda(s) pendiente(s) de energia  (" + dataModel.getTlxDeuEneC() + ") Bs\r\n";
            deudasEnergia = deudasEnergia + "T CONSO2.CPF 0 45 925 " + StringUtils.roundTwoDigits(dataModel.getTlxDeuEneI()) + "\r\n";
        }
        String deudasAseo = "";
        if (dataModel.getTlxDeuAseC() > 0) {
            deudasAseo = "T CONSO2.CPF 0 45 945 Deuda(s) pendiente(s) de tasa de aseo (" + dataModel.getTlxDeuAseC() + ") Bs\r\n";
            deudasAseo = deudasAseo + "T CONSO2.CPF 0 45 945 " + StringUtils.roundTwoDigits(dataModel.getTlxDeuAseI()) + "\r\n";
        }

        String tipoLectura = DataModel.getTipoLectura(dataModel.getTlxTipLec());

        String cpclConfigLabel = "! 0 200 200 1570 1\r\n" +
//                "ENCODING UTF-8\r\n" +

                "RIGHT 782\r\n" +
                "T CONSO2.CPF 0 10 30 " + dataModel.getTlxFacNro() + "\r\n" +
                "T CONSO2.CPF 0 10 50 " + dataModel.getTlxNroAut() + "\r\n" +

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
                "T CONSO2.CPF 0 45 320 TIPO LECTURA:  " + tipoLectura + "\r\n" +

                "T CONSO2.CPF 0 45 340 Energía consumida en (" + calcDays(dataModel.getTlxFecAnt(), dataModel.getTlxFecLec()) + ") dias\r\n" +
                "RIGHT 782\r\n" +
                "T CONSO2.CPF 0 720 340 " + dataModel.getTlxConsumo() + " kWh\r\n" +

                "LEFT\r\n" +
                "T CONSO2.CPF 0 45 360 Total energía a facturar\r\n" +
                "RIGHT 782\r\n" +
                "T CONSO2.CPF 0 720 360 " + dataModel.getTlxConsFacturado() + " kWh\r\n" +

                "LEFT\r\n" +
                "T CONSO3.CPF 0 40 1004 Son: " + NumberToLetterConverter.convertNumberToLetter(StringUtils.roundTwoDigits(dataModel.getTlxImpTot())) + "\r\n" +


                "T CONSO3.CPF 0 40 881 Importe del mes a cancelar:Bs\r\n" +
                "T CONSO3.CPF 0 40 968 Importe total a cancelar: Bs\r\n" +
                "T CONSO3.CPF 0 40 1035 Importe base para crédito fiscal: Bs\r\n" +

                "RIGHT 782\r\n" +
                "T CONSO3.CPF 0 45 881 " + StringUtils.roundTwoDigits(importeMesCancelar) + "\r\n" +
                "T CONSO3.CPF 0 45 968 " + StringUtils.roundTwoDigits(dataModel.getTlxImpTot()) + "\r\n" +
                "T CONSO3.CPF 0 45 1035 " + StringUtils.roundTwoDigits(dataModel.getTlxImpFac()) + "\r\n" +

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
                "T CONSO4.CPF 0 0 1230 " + leyenda[0] + "\n\r\n" +
                "T CONSO4.CPF 0 0 1244 " + leyenda[1] + "\n\r\n" +
                "T CONSO4.CPF 0 0 1258 " + leyenda[2] + "\n\r\n" +

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

                createHistorico(historico) +

                "T CONSO0.CPF 0 55 1378 Fecha Vencimiento: \r\n" +
                "T CONSO1.CPF 0 210 1378 " + dataModel.getTlxFecVto() + "\r\n" +
                "T CONSO0.CPF 0 300 1378 Fecha Est.Prox.Med: \r\n" +
                "T CONSO1.CPF 0 460 1378 " + dataModel.getTlxFecproMed() + "\r\n" +
                "T CONSO0.CPF 0 560 1378 Fecha Est.Prox.Emi: \r\n" +
                "T CONSO1.CPF 0 710 1378 " + dataModel.getTlxFecproEmi() + "\r\n" +

                "T CONSO1.CPF 0 135 1430 " + formatedDateSinDia(dataModel.getTlxFecLec()) + "\r\n" +
                "T CONSO1.CPF 0 300 1430 " + dataModel.getTlxCli() + "\r\n" +
                "T CONSO1.CPF 0 510 1430 " + dataModel.getTlxFacNro() + "\r\n" +
                "T CONSO1.CPF 0 720 1430 " + StringUtils.roundTwoDigits(dataModel.getTlxImpTot()) + "\r\n";

        cpclConfigLabel += detalleFacturacion(printTitles, printValues, null, importeTotalFactura, importeMesCancelar, dataModel.getTlxImpTap(), dataModel.getTlxImpAse());
        cpclConfigLabel += "" +
                "FORM\r\n" +
                "PRINT\r\n";

        Log.e(TAG, "creator: " + cpclConfigLabel);
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

    private static String formatedDateSinDia(String fecha) {
        String[] split = fecha.split("-");
        String year = split[0];
        String month = split[1];
        return mesString(Integer.parseInt(month)).toUpperCase().substring(0, 3) + "-" + year.substring(2);
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
            res += "LEFT\r\n";
            res += "T CONSO3.CPF 0 575 " + yValue + " Bs\r\n";
            res += "T CONSO3.CPF 0 40 " + yValue + " " + titles.get(i) + "\r\n";
            res += "RIGHT 782\r\n";
            res += "T CONSO3.CPF 0 720 " + yValue + " " + StringUtils.roundTwoDigits(values.get(i)) + "\r\n";
            yValue += 20;
        }

        yValue += 20;
        res += "LEFT\r\n";
        res += "T CONSO3.CPF 0 40 " + yValue + " Tasas para el Gobierno Municipal\r\n";

        yValue += 20;
        res += "T CONSO3.CPF 0 40 " + yValue + " Por alumbrado público\r\n";
        res += "T CONSO3.CPF 0 575 " + yValue + " Bs\r\n";
        res += "RIGHT 782\r\n";
        res += "T CONSO3.CPF 0 720 " + yValue + " " + StringUtils.roundTwoDigits(tap) + "\r\n";

        yValue += 20;
        res += "LEFT\r\n";
        res += "T CONSO3.CPF 0 575 " + yValue + " Bs\r\n";
        res += "T CONSO3.CPF 0 40 " + yValue + " Por aseo urbano\r\n";
        res += "RIGHT 782\r\n";
        res += "T CONSO3.CPF 0 720 " + yValue + " " + StringUtils.roundTwoDigits(impAse) + "\r\n";

        yValue += 40;
        res += "LEFT\r\n";
        res += "T CONSO3.CPF 0 575 " + yValue + " Bs\r\n";
        res += "T CONSO3.CPF 0 40 " + yValue + " Importe total factura\r\n";
        res += "RIGHT 782\r\n";
        res += "T CONSO3.CPF 0 720 " + yValue + " " + StringUtils.roundTwoDigits(impTotFac) + "\r\n";

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
        res += "T CONSO3.CPF 0 40 " + yValue + " Son: " + NumberToLetterConverter.convertNumberToLetter(StringUtils.roundTwoDigits(importeMes)) + "\r\n";

        return res;
    }

    private static String createHistorico(Historico h) {
        String res = "" +
                "T CONSO1.CPF 0 40 1328 " + h.getConMes01() + "\r\n" +
                "T CONSO1.CPF 0 160 1328 " + h.getConKwh01() + "\r\n" +
                "T CONSO1.CPF 0 40 1343 " + h.getConMes02() + "\r\n" +
                "T CONSO1.CPF 0 160 1343 " + h.getConKwh02() + "\r\n" +
                "T CONSO1.CPF 0 40 1358 " + h.getConMes03() + "\r\n" +
                "T CONSO1.CPF 0 160 1358 " + h.getConKwh03() + "\r\n" +
                "T CONSO1.CPF 0 235 1328 " + h.getConMes04() + "\r\n" +
                "T CONSO1.CPF 0 360 1328 " + h.getConKwh04() + "\r\n" +
                "T CONSO1.CPF 0 235 1343 " + h.getConMes05() + "\r\n" +
                "T CONSO1.CPF 0 360 1343 " + h.getConKwh05() + "\r\n" +
                "T CONSO1.CPF 0 235 1358 " + h.getConMes06() + "\r\n" +
                "T CONSO1.CPF 0 360 1358 " + h.getConKwh06() + "\r\n" +
                "T CONSO1.CPF 0 435 1328 " + h.getConMes07() + "\r\n" +
                "T CONSO1.CPF 0 560 1328 " + h.getConKwh07() + "\r\n" +
                "T CONSO1.CPF 0 435 1343 " + h.getConMes08() + "\r\n" +
                "T CONSO1.CPF 0 560 1343 " + h.getConKwh08() + "\r\n" +
                "T CONSO1.CPF 0 435 1358 " + h.getConMes09() + "\r\n" +
                "T CONSO1.CPF 0 560 1358 " + h.getConKwh09() + "\r\n" +
                "T CONSO1.CPF 0 635 1328 " + h.getConMes10() + "\r\n" +
                "T CONSO1.CPF 0 755 1328 " + h.getConKwh10() + "\r\n" +
                "T CONSO1.CPF 0 635 1343 " + h.getConMes11() + "\r\n" +
                "T CONSO1.CPF 0 755 1343 " + h.getConKwh11() + "\r\n" +
                "T CONSO1.CPF 0 635 1358 " + h.getConMes12() + "\r\n" +
                "T CONSO1.CPF 0 755 1358 " + h.getConKwh12() + "\r\n";
        return res;
    }
}
