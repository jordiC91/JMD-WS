package org.jmd.service;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.*;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.jmd.SQLUtils;
import org.jmd.metier.Etablissement;

@Path("etablissement")
public class EtablissementService {
    
    private Connection connexion;
    
    public EtablissementService() {
        
    }
    
    @GET
    public Response creer(  
            @QueryParam("nom")
            String nom,
            @QueryParam("ville")
                    String ville,
            @Context
                    HttpServletRequest request) {
        
        if (request.getSession(false) != null) {
            if (connexion == null) {
                connexion = SQLUtils.getConnexion();
            }
            
            try {
                Statement stmt = connexion.createStatement();
                stmt.execute("INSERT INTO ETABLISSEMENT (nom, ville) VALUES ('" + nom + "', '" + ville + "')");
                stmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, ex);
                
                if(ex instanceof MySQLIntegrityConstraintViolationException){
                    return Response.status(403).entity("DUPLICATE_ENTRY").build();
                }
                
                return Response.status(500).build();
            }
            
            return Response.status(200).build();
        } else {
            return Response.status(401).build();
        }
    }
    
    @DELETE
    public Response supprimer(  @QueryParam("id")
            String id,
            @Context
                    HttpServletRequest request) {
        
        if (request.getSession(false) != null) {
            if (connexion == null) {
                connexion = SQLUtils.getConnexion();
            }
            
            try {
                try (Statement stmt = connexion.createStatement()) {
                    stmt.executeUpdate("DELETE FROM ETABLISSEMENT WHERE (ID = " + id + ")");
                }
            } catch (SQLException ex) {
                Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, ex);
                
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
    public ArrayList<Etablissement> getAll() {
        ArrayList<Etablissement> etablissements = new ArrayList<>();
        
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        try {
            Statement stmt = connexion.createStatement();
            ResultSet results = stmt.executeQuery(  "SELECT * " +
                    "FROM ETABLISSEMENT " +
                    "ORDER BY ID ASC");
            
            Etablissement etablissement = null;
            
            while (results.next()) {
                etablissement = new Etablissement();
                etablissement.setIdEtablissement(results.getInt("ID"));
                etablissement.setNom(results.getString("NOM"));
                etablissement.setVille(results.getString("VILLE"));
                
                etablissements.add(etablissement);
            }
            
            results.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return etablissements;
    }
    
    @PreDestroy
    public void onDestroy() {
        if (connexion != null) {
            try {
                connexion.close();
            } catch (SQLException ex) {
                Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}