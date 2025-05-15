package com.reelthoughts.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class PasswordUtil {
    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int SALT_LENGTH_BYTE = 16;
    private static final Charset UTF_8 = StandardCharsets.UTF_8;
    private static final Logger logger = Logger.getLogger(PasswordUtil.class.getName());
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;

    private static byte[] getRandomNonce(int numBytes) {
        byte[] nonce = new byte[numBytes];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    private static SecretKey getAESKeyFromPassword(char[] password, byte[] salt) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
            return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            logger.log(Level.SEVERE, "Key derivation failed", ex);
            return null;
        }
    }

    public static String hashPassword(String userIdentifier, String password) {
        try {
            byte[] iv = getRandomNonce(IV_LENGTH_BYTE);
            byte[] salt = getRandomNonce(SALT_LENGTH_BYTE);

            SecretKey aesKey = getAESKeyFromPassword(userIdentifier.toCharArray(), salt);
            if (aesKey == null) return null;

            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
            byte[] cipherText = cipher.doFinal(password.getBytes(UTF_8));

            return "v2$" + 
                   Base64.getEncoder().encodeToString(iv) + "$" +
                   Base64.getEncoder().encodeToString(salt) + "$" +
                   Base64.getEncoder().encodeToString(cipherText);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Password hashing failed", ex);
            return null;
        }
    }

    public static boolean checkPassword(String userIdentifier, String inputPassword, String storedPassword) {
        if (userIdentifier == null || inputPassword == null || storedPassword == null) {
            return false;
        }

        try {
            String[] parts = storedPassword.split("\\$");
            if (parts.length != 4 || !parts[0].equals("v2")) {
                return false;
            }

            byte[] iv = Base64.getDecoder().decode(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] cipherText = Base64.getDecoder().decode(parts[3]);

            SecretKey aesKey = getAESKeyFromPassword(userIdentifier.toCharArray(), salt);
            if (aesKey == null) return false;

            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
            byte[] decryptedPassword = cipher.doFinal(cipherText);

            return constantTimeEquals(inputPassword.getBytes(UTF_8), decryptedPassword);
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Password verification failed", ex);
            return false;
        }
    }

    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }
}