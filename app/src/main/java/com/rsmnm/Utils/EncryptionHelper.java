package com.rsmnm.Utils;

import android.util.Log;

import com.google.gson.Gson;

import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by rohail on 23-Mar-17.
 */

public class EncryptionHelper {

    private static final String HMAC_KEY = "5d3eb7520d693dae4aaf00149c4d0027";
    private static final String AES_KEY = "57238004e784498bbc2f8bf984565090";

    public static String encryptAES(String clearText) {
        byte[] encryptedText = null;
        try {
            byte[] keyData = hexStringToByteArray(AES_KEY);
            SecretKeySpec ks = new SecretKeySpec(keyData, "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, ks);
            encryptedText = c.doFinal(clearText.getBytes("UTF-8"));
            return byteArrayToHexString(encryptedText);
        } catch (Exception e) {
            return null;
        }
    }

    public static String decryptAES(String encryptedStr) {
        byte[] clearText = null;
        byte[] encryptedText = hexStringToByteArray(encryptedStr);
        try {
            byte[] keyData = hexStringToByteArray(AES_KEY);
            SecretKey ks = new SecretKeySpec(keyData, "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, ks);
            clearText = c.doFinal(encryptedText);
            return new String(clearText, "UTF-8");
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }

    public static String byteArrayToHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString();
    }

//    private static String CreateSignature(String strMethodType) {
//        String Delimiter = "\n";
//
//        String StringToSign = NSConfig.getInstance().getPublicKey() + Delimiter +
//                unixTS + Delimiter +
//                NSConfig.getInstance().getSecurityVersion() + Delimiter +
//                strMethodType + Delimiter +
//                NSConfig.getInstance().getHMACType();
//
//        return calcHMACSHA(StringToSign);
//    }

//    public static String calcHMACSHA(String Signature) {
//
//        try {
//            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
//            SecretKeySpec secret_key = new SecretKeySpec(HMAC_KEY.getBytes("UTF-8"), "HmacSHA256");
//            sha256_HMAC.init(secret_key);
//
//            String hash = Base64.encodeToString(sha256_HMAC.doFinal(Signature.getBytes("US-ASCII")), Base64.DEFAULT);
//            Log.e("Hmac", hash);
//            return hash;
//        } catch (Exception e) {
//            System.out.println("Error");
//            return e.getMessage();
//        }
//    }

    public static String calcHMACSHA(String Signature) {

        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(HMAC_KEY.getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] hash = sha256_HMAC.doFinal(Signature.getBytes("UTF-8"));
            String encoded_hash = byteArrayToHexString(hash);
            Log.v("Hmac", encoded_hash);
            return encoded_hash;
        } catch (Exception e) {
            System.out.println("Error");
            return e.getMessage();
        }
    }

    public static Map<String, Object> uppendHmac(Map<String, Object> map) {
        String json = new Gson().toJson(map);
        json.replaceAll(": ", ":");
        json.replaceAll(", ", ",");
        String hmac = calcHMACSHA(json);
        map.put("signature", hmac);
        return map;
    }

    public static String calculateHmac(Map<String, String> map) {
        String json = new Gson().toJson(map);
        json.replaceAll(": ", ":");
        json.replaceAll(", ", ",");
        String hmac = calcHMACSHA(json);
        return hmac;
    }
}
