package org.jmd.utils;

import java.security.*;

/**
 * Classe comprenant des méthodes de sécurité communes aux admins.
 *
 * @author jordi charpentier - yoann vanhoeserlande
 */
public class SecurityUtils {

    /**
     * Constructeur privé de la classe.<br />
     * Empèche son instanciation.
     */
    private SecurityUtils() {

    }
    
    /**
     * Méthode permettant d'hasher une chaîne de caractères en SHA-256.
     *
     * @param passwordToHash La chaîne à hasher.
     *
     * @return La chaîne hashée en SHA-256.
     */
    public static String sha256(String passwordToHash) {
        String generatedPassword = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Le SHA-256 n'est pas supporté.");
        }

        return generatedPassword;
    }
}
