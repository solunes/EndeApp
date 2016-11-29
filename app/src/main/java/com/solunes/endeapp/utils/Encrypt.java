package com.solunes.endeapp.utils;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Esta clase tiene los métodos para encriptar y desencriptar
 * Tiene dos métodos, Un algoritmo de Hash SHA-1 y otro de encriptacion AES-256-CBC
 */

public class Encrypt {

    private static final String TAG = "Encrypt";

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] textBytes = text.getBytes("iso-8859-1");
        md.update(textBytes, 0, textBytes.length);
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    public static String encrypt(String privateKey, String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        byte[] dataBytes = data.getBytes("UTF-8");
        SecretKeySpec keyspec = getKeySpec(privateKey);
        IvParameterSpec ivspec = getIvSpec();

        cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
        byte[] encrypted = cipher.doFinal(dataBytes);
        String finalEncrypted = toHex(encrypted);
        return finalEncrypted;
    }

    public static String decrypt(String privateKey, String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        byte[] dataBytes = toByte(data);
        SecretKeySpec keyspec = getKeySpec(privateKey);
        IvParameterSpec ivspec = getIvSpec();

        cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
        byte[] decrypted = cipher.doFinal(dataBytes);
        String finalDecrypted = new String(decrypted, "UTF-8");
        return finalDecrypted;
    }

    private static SecretKeySpec getKeySpec(String privateKey) throws Exception {
        SecretKeySpec keyspec = new SecretKeySpec(privateKey.getBytes(), "AES");
        return keyspec;
    }

    private static IvParameterSpec getIvSpec() throws Exception {
        String iv = "0123456789ABCDEF";
        IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
        return ivspec;
    }

    public static byte[] toByte(String str) {
        if (str==null) {
            return null;
        }
        else if (str.length() < 2) {
            return null;
        }
        else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i=0; i<len; i++) {
                buffer[i] = (byte) Integer.parseInt(str.substring(i*2,i*2+2),16);
            }
            return buffer;
        }
    }

    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2*buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }
    private final static String HEX = "0123456789ABCDEF";
    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
    }

}
