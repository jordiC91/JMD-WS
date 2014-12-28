package org.jmd.utils;

import java.sql.*;
import java.util.logging.*;

/**
 * Classe permettant de gérer les opérations SQL.
 * 
 * @author jordi charpentier - yoann vanhoeserlande
 */
public class SQLUtils {
 
    /**
     * Constructeur de la classe (privé).
     * Permet d'éviter l'instanciation de la classe (-> classe statique).
     */
    private SQLUtils() {
        
    }
    
    /**
     * Méthode permettant de récupérer une connexion à la base de données de
     * l'application.
     * 
     * @return Un objet représentant une connexion à la base de données de 
     * l'application.
     */
    public static Connection getConnexion() {
        String url = "jdbc:mysql://localhost/jmd";
        Connection conn = null;
        
        try {
            Class.forName ("com.mysql.jdbc.Driver").newInstance();
            
            // Glassfish Kimsufi
            conn = DriverManager.getConnection(url, "jordi", "Ce^7;a#F0TO@(1kO0e9m");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(SQLUtils.class.getName()).log(Level.SEVERE, null, ex);
        } 
            
        return conn;
    }
}