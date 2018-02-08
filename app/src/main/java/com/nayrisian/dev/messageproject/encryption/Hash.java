package com.nayrisian.dev.messageproject.encryption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * Created by Nayrisian on 15/10/2016.
 */
public class Hash {
    public static String hash(String message, Hashtype hashtype) {
        final String type;
        switch (hashtype) {
            case MD5:
                type = "MD5";
                break;
            case SHA1:
                type = "SHA-1";
                break;
            default:
                type = "SHA-1";
        }
        try {
            // Create Hash Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(type);
            digest.update(message.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}