package com.solunes.endeapp.utils;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by jhonlimaster on 17-11-16.
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


    public static String methodEncrypt(String text) {
//        SecretKeySpec sks = null;
//        try {
//            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
//            sr.setSeed("any data used as random seed".getBytes());
//            KeyGenerator kg = KeyGenerator.getInstance("AES");
//            kg.init(128, sr);
//            sks = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
//        } catch (Exception e) {
//            Log.e(TAG, "AES secret key spec error");
//        }
//
//        // Encode the original data with AES
//        byte[] encodedBytes = null;
//        try {
//            Cipher c = Cipher.getInstance("AES");
//            c.init(Cipher.ENCRYPT_MODE, sks);
//            encodedBytes = c.doFinal(text.getBytes());
//        } catch (Exception e) {
//            Log.e(TAG, "AES encryption error");
//        }
//        return Base64.encodeToString(encodedBytes, Base64.DEFAULT);
        String sha1 = null;
        try {
            sha1 = SHA1(text);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sha1;
    }

    public static String methodDecrypt(byte[] encodedBytes) {
//        SecretKeySpec sks = null;
//        try {
//            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
//            sr.setSeed("any data used as random seed".getBytes());
//            KeyGenerator kg = KeyGenerator.getInstance("AES");
//            kg.init(128, sr);
//            sks = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
//        } catch (Exception e) {
//            Log.e(TAG, "AES secret key spec error");
//        }
//        // Decode the encoded data with AES
//        byte[] decodedBytes = null;
//        try {
//            Cipher c = Cipher.getInstance("AES");
//            c.init(Cipher.DECRYPT_MODE, sks);
//            decodedBytes = c.doFinal(encodedBytes);
//        } catch (Exception e) {
//            Log.e(TAG, "AES decryption error");
//        }
//        return new String(decodedBytes);
        return new String(encodedBytes);
    }
}
