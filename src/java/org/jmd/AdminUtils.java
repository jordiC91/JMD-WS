package org.jmd;

import java.sql.*;
import java.util.logging.*;
import org.jmd.service.DiplomeService;

/**
 * Classe comprenant des méthodes communes aux admins.
 * Exemple : vérification de token et de timestamp.
 * 
 * @author jordi charpentier - yoann vanhoeserlande
 */
public class AdminUtils {
    
    /**
     * Objet représentant une connexion à la base de données de  l'application.
     */
    private static Connection connexion;
    
    /**
     * Constructeur privé de la classe.<br />
     * Empèche son instanciation.
     */
    private AdminUtils() {
        
    }
    
    /**
     * Méthode permettant de vérifier le token envoyé pour un utilisateur afin
     * de voir s'il est connecté ou non.
     * 
     * @param pseudo Le pseudo donné lors de la requête.
     * @param tokenACheck Le token à verifier.
     * 
     * @return <b>true</b> si le token de l'utilisateur est valide.
     * <b>false</b> sinon.
     */
    public static boolean checkToken(String pseudo, String tokenACheck) {
        boolean isOK = false;
        
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        try {
            Statement stmt = connexion.createStatement();
            ResultSet results = stmt.executeQuery("SELECT * " +
                                                  "FROM ADMINISTRATEUR " +
                                                  "WHERE (PSEUDO = '" + pseudo + "')");
                       
            while (results.next()) {    
                String token = results.getString("TOKEN");
                
                if (token.equals(tokenACheck)) {
                    isOK = true;
                    break;
                }
            }
            
            results.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return isOK;
    }
    
    /**
     * Méthode permettant de vérifier le timestamp d'un utilisateur afin d'éviter
     * les attaques par rejeu.
     * 
     * @param pseudo Le pseudo donné lors de la requête.
     * @param timestampACheck Le timestamp à verifier.
     * 
     * @return <b>true</b> si le timestamp de l'utilisateur est valide.
     * <b>false</b> sinon.
     */
    public static boolean checkTimestamp(String pseudo, long timestampACheck) {
        boolean isOK = false;
        
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        try {
            Statement stmt = connexion.createStatement();
            ResultSet results = stmt.executeQuery("SELECT * " +
                                                  "FROM ADMINISTRATEUR " +
                                                  "WHERE (PSEUDO = '" + pseudo + "')");
                       
            while (results.next()) {    
                long timestamp = results.getLong("TOKEN");
                
                if (timestampACheck > timestamp) {
                    isOK = true;
                    break;
                }
            }
            
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return isOK;
    }
}