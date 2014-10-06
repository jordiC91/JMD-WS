package org.jmd.service;

import java.sql.*;
import java.util.logging.*;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.jmd.SQLUtils;
import org.jmd.metier.Etablissement;

@Path("diplome")
public class DiplomeService {
    
    private Connection connexion;
 
    public DiplomeService() {
        
    }

    @GET
    @Path("creer")
    public Response creer(  @QueryParam("nom")
                            String nom,
                            @Context 
                            HttpServletRequest request,
                            @Context 
                            ServletContext sContext) {
        
        if (sContext.getAttribute("pseudo") != null) {
            if (connexion == null) {
                connexion = SQLUtils.getConnexion();
            }

            try {
                Statement stmt = connexion.createStatement();  
                stmt.execute("INSERT INTO diplome (nom) VALUES ('" + nom + "')");
                stmt.close();    
            } catch (SQLException ex) {
                Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);
                
                return Response.status(500).build();
            }
            
            return Response.status(200).build();
        } else {
            return Response.status(401).build();
        }
    }
    
    @GET
    @Path("supprimer/{id}")
    public Response supprimer(  @PathParam("id")
                                String nom,
                                @Context 
                                HttpServletRequest request) {
        
        if (request.getSession().getAttribute("pseudo") != null) {
            if (connexion == null) {
                connexion = SQLUtils.getConnexion();
            }

            try {
                Statement stmt = connexion.createStatement();  
                stmt.execute("INSERT INTO diplome (nom) VALUES ('" + nom + "')");
                stmt.close();    
            } catch (SQLException ex) {
                Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);
                
                return Response.status(500).build();
            }
            
            return Response.status(200).build();
        } else {
            return Response.status(401).build();
        }
    }
    
    @GET
    @Path("getAll")
    public Response getAll() {
        
        String res = "";
        
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        try {
            Statement stmt = connexion.createStatement();
            ResultSet results = stmt.executeQuery("SELECT * FROM diplome ORDER BY id ASC");

            while (results.next()) {
                res += results.getInt(1) + " | " + results.getString(2) + "<br />";
            }

            results.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);
            
            return Response.status(500).build();
        }
        
        return Response.status(200).entity(res).build();
    }
    
    @PreDestroy
    public void onDestroy() {
        if (connexion != null) {
            try {
                connexion.close();
            } catch (SQLException ex) {
                Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @GET
    @Path("etablissement")
    @Produces("application/json")
    public Etablissement[] getEtablissement() {
        Etablissement a = new Etablissement();
        Etablissement b = new Etablissement();
        Etablissement[] c = new Etablissement[2];
        
        a.setNom("Caca");
        b.setNom("Pénis");
        
        c[0] = a;
        c[1] = b;
        
        return c;
    }
}