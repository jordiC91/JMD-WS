package org.jmd;

import java.sql.*;
import java.util.logging.*;

public class SQLUtils {
 
    private SQLUtils() {
        
    }
    
    public static Connection getConnexion() {
        String url = "jdbc:mysql://localhost/jmd";
        Connection conn = null;
        
        try {
            Class.forName ("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(url, "root", "");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(SQLUtils.class.getName()).log(Level.SEVERE, null, ex);
        } 
            
        return conn;
    }
}