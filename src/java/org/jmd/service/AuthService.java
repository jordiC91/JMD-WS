package org.jmd.service;

import java.sql.*;
import java.util.logging.*;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.jmd.SQLUtils;

/**
 * Service web gérant l'authentification et la déconnexion d'un utilisateur.
 * 
 * @author jordi charpentier - yoann vanhoeserlande
 */
@Path("authentification")
public class AuthService {
    
    private Connection connexion;
 
    public AuthService() {
        
    }

    /**
     * Méthode permettant de connecter un utilisateur (si les identifiants sont
     * bons). 
     * Si les identifiants sont bons, une session est créée qui aura comme durée
     * de vie celle définie dans le web.xml du projet.
     * 
     * @param pseudo Le pseudo de l'utilisateur.
     * @param password Le mot de passe de l'utilisateur.
     * @param request La requête HTTP ayant appelée le service.
     * 
     * @return 2 possibilités :
     * - Un code HTTP 200 si les identifiants sont bons.
     * - Un code HTTP 401 si les identifiants n'étaient pas bons.
     */
    @POST
    @Path("login")
    public Response login(  @QueryParam("username")
                            String pseudo,
                            @QueryParam("password")
                            String password,
                            @Context 
                            HttpServletRequest request) {
        
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        try {
            Statement stmt = connexion.createStatement();  
            ResultSet results = stmt.executeQuery("SELECT * FROM administrateur WHERE (pseudo ='" + pseudo + "') AND (password ='" + password + "')");
            
            while (results.next()) {
                if (results.getString("PSEUDO") != null) {                    
                    request.getSession(true);
                    
                    return Response.status(200).build();
                }
            }
            
            results.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(AuthService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(401).build();
    }
    
    /**
     * Méthode permettant de déconnecter un utilisateur.
     * La session actuelle de l'utilisateur est invalidée (= terminée).
     * 
     * @param request La requête HTTP ayant appelée le service.
     * 
     * @return Un code HTTP 200 si la session a bien été terminée.
     */
    @Path("logout")
    public Response logout(@Context 
                           HttpServletRequest request) {
        
        request.getSession().invalidate();
        
        return Response.status(200).build();
    }
    
    /**
     * Méthode exécutée avant la fin de vie du service.
     * La connexion à la base est fermée.
     */
    @PreDestroy
    public void onDestroy() {
        if (connexion != null) {
            try {
                connexion.close();
            } catch (SQLException ex) {
                Logger.getLogger(AuthService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}