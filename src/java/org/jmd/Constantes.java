package org.jmd;

/**
 * Classe contenant des constantes pour l'application.
 * Déclarées ici car susceptibles de changer lors du déploiement sur un nouvel
 * environnement (exemple : dév -> prod).
 * 
 * @author jordi charpentier - yoann vanhoeserlande
 */
public class Constantes {
    
    /**
     * L'URL d'accès aux services web sur le serveur de prod.
     */
    public static String SERVER_URL = "http://ns3281017.ip-5-39-94.eu:8080/JMD-Webservice/webresources/";
    
    /**
     * L'URL d'accès aux services web sur le serveur de dév.
     */
    public static String SERVER_DEV_URL = "http://localhost:8080/JMD/webresources";
    
    /**
     * Email de l'application utilisé lors des envois de mail.
     */
    public static String EMAIL_JMD = "jaimondiplome@gmail.com";
    
    /**
     * Mot de passe du compte mail pour les envois.
     * <b>Attention</b> : stocké en clair.
     */
    public static String PASSWORD_JMD = "gkc19iregpt3qir";
    
    /**
     * Limite donnée pour qu'un timestamp soit valide.
     * Fixée à 15 minutes.
     */
    public static long TIMESTAMP_LIMIT = 15 * 60 * 1000;
}
