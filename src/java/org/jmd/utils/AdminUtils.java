package org.jmd.utils;

import java.sql.*;
import java.util.logging.*;
import org.jmd.Constantes;
import org.jmd.service.AdminService;
import org.jmd.service.DiplomeService;

/**
 * Classe comprenant des méthodes communes aux admins.
 * Exemple : vérification de token et de timestamp.
 * 
 * @author jordi charpentier - yoann vanhoeserlande
 */
public class AdminUtils {
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
        Connection connexion = null;
        Statement stmt = null;
        ResultSet results = null;
        
        try {
            connexion = SQLUtils.getConnexion();
            stmt = connexion.createStatement();
            results = stmt.executeQuery("SELECT TOKEN " +
                                                  "FROM ADMINISTRATEUR " +
                                                  "WHERE (PSEUDO = '" + pseudo + "');");
                       
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
        }        finally {
            if( results != null ) {
                try {
                    results.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if(stmt != null){
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (connexion != null){
                try {
                    connexion.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
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
        Connection connexion = null;
        Statement stmt = null;
        ResultSet results = null;
        
        try {
            connexion = SQLUtils.getConnexion();
             stmt = connexion.createStatement();
             results = stmt.executeQuery("SELECT TIMESTAMP_USER " +
                                                  "FROM ADMINISTRATEUR " +
                                                  "WHERE (PSEUDO = '" + pseudo + "');");
                       
            while (results.next()) {    
                long timestamp = results.getLong("TIMESTAMP_USER");
                
                if ((timestampACheck - timestamp) < Constantes.TIMESTAMP_LIMIT) {
                    // Mise à jour du timestamp.
                    stmt.executeUpdate("UPDATE ADMINISTRATEUR SET TIMESTAMP_USER = "+ timestampACheck +" WHERE PSEUDO = '" + pseudo + "';");
                    
                    isOK = true;
                    break;
                } else {
                    logout(pseudo);
                }
            }
            results.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);
        }        finally {
            if( results != null ) {
                try {
                    results.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if(stmt != null){
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (connexion != null){
                try {
                    connexion.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return isOK;
    }
    
    private static void logout(String pseudo) {
        Connection connexion = null;
        Statement stmt = null;
        
        try {
            connexion = SQLUtils.getConnexion();
            stmt = connexion.createStatement();  
            stmt.executeUpdate("UPDATE ADMINISTRATEUR SET TOKEN = 'NULL' WHERE PSEUDO = '" + pseudo + "';");
            stmt.executeUpdate("UPDATE ADMINISTRATEUR SET TIMESTAMP_USER = '0' WHERE PSEUDO = '" + pseudo + "';");

            stmt.close();
        } catch (SQLException ex) {
                Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, null, ex);
        }         
        finally {
            if(stmt != null){
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (connexion != null){
                try {
                    connexion.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}