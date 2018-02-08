package com.nayrisian.dev.messageproject.utility;

import android.content.Context;
import android.widget.Toast;

import com.nayrisian.dev.messageproject.Setting;
import com.nayrisian.dev.messageproject.encryption.Encrypt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

/**
 * Utility class with static methods to perform various tasks.
 * Created by Nayrisian on 17/11/2016.
 */
public class Util {
    public static <T> String toString(T[] params) {
        String output = "";
        for (T param : params)
            output += param.toString();
        return output;
    }

    public static byte[] serialise(Object object, Context context) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            return bos.toByteArray();
        } catch (IOException ex) {
            Error.log(ex, context);
            return null;
        }
    }

    public static Object deserialise(byte[] bytes, Context context) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Error.log(ex, context);
            return null;
        }
    }

    public static void encryption(Context context) {
        try {
            // Generate salt by password.
            String salt = Encrypt.saltString(Encrypt.generateSalt());
            Toast.makeText(context, salt, Toast.LENGTH_LONG).show();
            // Generate key using the password and salt.
            Encrypt.SecretKeys secretKeys = Encrypt.generateKeyFromPassword(
                    Setting.getAccount().getPassword(), salt);
            Toast.makeText(context, secretKeys.toString(), Toast.LENGTH_LONG).show();
            // Encrypt plain text.
            Encrypt.CipherTextIvMac cipherTextIvMac = Encrypt.encrypt("Nayrisian", secretKeys);
            Toast.makeText(context, cipherTextIvMac.toString(), Toast.LENGTH_LONG).show();
            // Decrypt cipher text.
            String plainText = Encrypt.decryptString(cipherTextIvMac, secretKeys);
            Toast.makeText(context, plainText, Toast.LENGTH_LONG).show();
        } catch (UnsupportedEncodingException | GeneralSecurityException ex) {
            Error.log(ex, context);
        }
    }
}