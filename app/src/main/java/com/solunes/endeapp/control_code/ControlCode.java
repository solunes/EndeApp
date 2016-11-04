package com.solunes.endeapp.control_code;

import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by jhonlimaster on 19-09-16.
 */
public class ControlCode {
    //datos para generar codigo de control
    private String authorizationNumber; //Número de Autorización
    private String invoiceNumber; //Número de Factura
    private String NITCI;
    private String dateOfTransaction; //Fecha de la Transacción
    private String transactionAmount; //Monto de la Transacción
    private String dosageKey;//Llave de Dosificación
    //otras variables :)
    private String fiveDigitsVerhoeff;
    private String stringDKey;
    private int sumProduct;
    private String base64SIN;

    public ControlCode(){
    }

    /**
     * Genera el codigo de control
     * @param aNumber Numero de autorizacion
     * @param iNumber Numero de factura
     * @param nitci NIT o CI
     * @param dTransaction fecha de transaccion de la forma:
     *                       2007/07/02 a 20070702
     * @param tAmount Monto de la transacción
     * @param dKey Llave de dosificacion
     * @return String Codigo de control
     */
    public String generate(String aNumber, String iNumber, String nitci,
                           String dTransaction, String tAmount, String dKey){
        this.authorizationNumber = aNumber;
        this.invoiceNumber = iNumber;
        this.NITCI =nitci;
        this.dateOfTransaction = dTransaction;
        this.transactionAmount = roundUp(tAmount);
        this.dosageKey = dKey;

        /* ========== PASO 1 ============= */
        invoiceNumber = addVerhoeffDigit(invoiceNumber,2);
        NITCI = addVerhoeffDigit(NITCI,2);
        dateOfTransaction = addVerhoeffDigit(dateOfTransaction,2);
        this.transactionAmount = addVerhoeffDigit(transactionAmount,2);
        //se suman todos los valores obtenidos
        Long sumOfVariables = Long.valueOf(invoiceNumber)
                + Long.valueOf(NITCI)
                + Long.valueOf(dateOfTransaction)
                + Long.valueOf(transactionAmount);
        //A la suma total se añade 5 digitos Verhoeff
        String sumOfVariables5Verhoeff = addVerhoeffDigit(String.valueOf(sumOfVariables),5);

        /* ========== PASO 2 ============= */
        fiveDigitsVerhoeff = sumOfVariables5Verhoeff.substring(sumOfVariables5Verhoeff.length()-5);

        String[] ary = fiveDigitsVerhoeff.split("");//java 8
        int[] numbers = new int[ary.length];
        // java7 i=1
        // java8 i=0
        for(int i=1;i<ary.length;i++){
            numbers[i-1] = Integer.parseInt(ary[i]) + 1;
        }

        String string1 = dosageKey.substring(0, numbers[0] );
        String string2 = dosageKey.substring(numbers[0], numbers[0]+numbers[1] );
        String string3 = dosageKey.substring(numbers[0]+numbers[1], numbers[0]+numbers[1]+numbers[2] );
        String string4 = dosageKey.substring(numbers[0]+numbers[1]+numbers[2], numbers[0]+numbers[1]+numbers[2]+numbers[3] );
        String string5 = dosageKey.substring(numbers[0]+numbers[1]+numbers[2]+numbers[3], numbers[0]+numbers[1]+numbers[2]+numbers[3]+numbers[4] );

        String authorizationNumberDKey = authorizationNumber + string1;
        String invoiceNumberdKey = invoiceNumber + string2;
        String NITCIDKey = NITCI + string3;
        String dateOfTransactionDKey = dateOfTransaction + string4;
        String transactionAmountDKey = transactionAmount + string5;

        /* ========== PASO 3 ============= */
        //se concatena cadenas de paso 2
        stringDKey = authorizationNumberDKey + invoiceNumberdKey + NITCIDKey + dateOfTransactionDKey + transactionAmountDKey;
        //Llave para cifrado + 5 digitos Verhoeff generado en paso 2
        String keyForEncryption = dosageKey + fiveDigitsVerhoeff;
        //se aplica AllegedRC4
        String allegedRC4String = AllegedRC4.encryptMessageRC4Unscripted(stringDKey, keyForEncryption);

        /* ========== PASO 4 ============= */
        //cadena encriptada en paso 3 se convierte a un Array
        ArrayList<Character> chars = new ArrayList();
        for (char c : allegedRC4String.toCharArray()) {
            chars.add(c);
        }
        //se suman valores ascii
        int totalAmount=0;
        int sp1=0;
        int sp2=0;
        int sp3=0;
        int sp4=0;
        int sp5=0;

        int tmp=1;
        for(char c:chars){
            totalAmount += (int)c;
            switch(tmp){
                case 1: sp1 += (int)c; break;
                case 2: sp2 += (int)c; break;
                case 3: sp3 += (int)c; break;
                case 4: sp4 += (int)c; break;
                case 5: sp5 += (int)c; break;
            }
            tmp = (tmp<5)?tmp+1:1;
        }

        /* ========== PASO 5 ============= */
        //suma total * sumas parciales dividido entre resultados obtenidos
        //entre el dígito Verhoeff correspondiente más 1 (paso 2)
        int tmp1 = totalAmount*sp1/numbers[0];
        int tmp2 = totalAmount*sp2/numbers[1];
        int tmp3 = totalAmount*sp3/numbers[2];
        int tmp4 = totalAmount*sp4/numbers[3];
        int tmp5 = totalAmount*sp5/numbers[4];
        //se suman todos los resultados
        sumProduct = tmp1 + tmp2 + tmp3 + tmp4 +tmp5;
        //se obtiene base64
        base64SIN= Base64SIN.convert(sumProduct);

        /* ========== PASO 6 ============= */
        //Aplicar el AllegedRC4 a la anterior expresión obtenida
        return AllegedRC4.encryptMessageRC4(base64SIN, dosageKey+fiveDigitsVerhoeff);

    }//end:generateControlCode

    /**
     * Añade N digitos Verhoeff a una cadena de texto
     * @param value String
     * @param max numero de digitos a agregar
     * @return String cadena original + N digitos Verhoeff
     */
    private String addVerhoeffDigit(String value,int max){
        for(int i=1;i<=max;i++)
            value += Verhoeff.generateVerhoeff(value);
        return value;
    }

    /**
     * Redondea hacia arriba
     * @param value cadena con valor numerico de la forma 123 123.4 123,4
     */
    private String roundUp(String value){
        //reemplaza (,) por (.)
        value = value.replace(",", ".");
        //redondea a 0 decimales
        BigDecimal valueBD = new BigDecimal(Double.parseDouble(value));
        valueBD = valueBD.setScale(0, BigDecimal.ROUND_HALF_UP);
        return String.valueOf(valueBD);
    }

    /* metodos usados solo para realizar el testeo */
    public String getFiveDigitsVerhoeff() {
        return fiveDigitsVerhoeff;
    }

    public String getStringDKey() {
        return stringDKey;
    }

    public int getSumProduct() {
        return sumProduct;
    }

    public String getBase64SIN() {
        return base64SIN;
    }
}
