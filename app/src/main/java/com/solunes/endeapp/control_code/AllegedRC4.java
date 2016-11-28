package com.solunes.endeapp.control_code;

/**
 * Un algoritmo de criptografía simétrica, basado en cifrado de flujo (stream cipher), muy utilizado
 * por su rendimiento y simplicidad. Para efectos de la implementación del Código de Control.
 */
public class AllegedRC4 {
    /**
     * Retorna mensaje encriptado
     *
     * @param message mensaje a encriptar
     * @param key     llave
     * @return String mensaje encriptado
     */
    public static String encryptMessageRC4(String message, String key) {
        int state[] = new int[256];
        int x = 0;
        int y = 0;
        int index1 = 0;
        int index2 = 0;
        int nmen;
        String messageEncryption = "";

        for (int i = 0; i <= 255; i++) {
            state[i] = i;
        }

        for (int i = 0; i <= 255; i++) {
            //Index2 = ( ObtieneASCII(key[Index1]) + State[I] + Index2 ) MODULO 256
            index2 = (((int) key.toCharArray()[index1]) + state[i] + index2) % 256;
            //IntercambiaValor( State[I], State[Index2] )
            int aux = state[i];
            state[i] = state[index2];
            state[index2] = aux;
            //Index1 = (Index1 + 1) MODULO LargoCadena(Key)
            index1 = (index1 + 1) % key.length();
        }

        //PARA I = 0 HASTA LargoCadena(Mensaje)-1 HACER
        for (int i = 0; i < message.length(); i++) {
            //X = (X + 1) MODULO 256
            x = (x + 1) % 256;
            //Y = (State[X] + Y) MODULO 256
            y = (state[x] + y) % 256;
            //IntercambiaValor( State[X] , State[Y] )
            int aux = state[x];
            state[x] = state[y];
            state[y] = aux;
            //NMen = ObtieneASCII(Mensaje[I]) XOR State[(State[X] + State[Y]) MODULO 256]
            nmen = ((int) message.toCharArray()[i]) ^ state[(state[x] + state[y]) % 256];
            //MensajeCifrado = MensajeCifrado + "-" + RellenaCero(ConvierteAHexadecimal(NMen))
            String nmenHex = Integer.toHexString(nmen).toUpperCase();
            messageEncryption = messageEncryption + "-" + ((nmenHex.length() == 1) ? ("0" + nmenHex) : nmenHex);
        }
        //RETORNAR ObtieneSubCadena(MensajeCifrado, 1, LargoCadena(MensajeCifrado) - 1);
        return messageEncryption.substring(1, messageEncryption.length());
    }

    /**
     * Retorna mensaje encriptado sin guion (-)
     *
     * @param message mensaje a encriptar
     * @param key     llave
     * @return String mensaje encriptado
     */
    public static String encryptMessageRC4Unscripted(String message, String key) {
        String resul = encryptMessageRC4(message, key);
        resul = resul.replace("-", "");
        return resul;
    }
}
