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
     * L'URL d'accès aux services web sur le serveur utilisé.
     */
    public static String SERVER_URL = "http://localhost:8080/JMD/webresources";
    
    /**
     * Email de l'application utilisé lors des envois de mail.
     */
    public static String EMAIL_JMD = "jaimondiplome@gmail.com";
}
