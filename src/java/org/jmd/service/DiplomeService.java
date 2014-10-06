package org.jmd.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.*;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.jmd.SQLUtils;
import org.jmd.metier.Diplome;
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
    
    @DELETE
    @Path("{id}")
    public Response supprimer(  @PathParam("id")
                                String id,
                                @Context 
                                HttpServletRequest request) {
        
        if (request.getSession().getAttribute("pseudo") != null) {
            if (connexion == null) {
                connexion = SQLUtils.getConnexion();
            }

            try {
                try (Statement stmt = connexion.createStatement()) {
                    stmt.executeUpdate("DELETE FROM DIPLOME WHERE (ID = "+id+")");
                }    
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
    @Produces("application/json")
    public ArrayList<Diplome> getAll() {
        
        ArrayList<Diplome> diplomes = null;
                
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        try {
            Statement stmt = connexion.createStatement();
            ResultSet results = stmt.executeQuery("SELECT * FROM diplome ORDER BY id ASC");
            diplomes = new ArrayList<>();
            Diplome d = null;
            
            while (results.next()) {
                d = new Diplome();
                d.setIdDiplome(results.getInt("ID"));
                d.setNom(results.getString("NOM"));
                diplomes.add(d);
            }

            results.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return diplomes;
        //return Response.status(200).entity(diplomes.toArray(new Diplome[diplomes.size()])).build();
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