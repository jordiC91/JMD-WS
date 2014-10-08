package org.jmd.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.*;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.jmd.SQLUtils;
import org.jmd.metier.Matiere;

@Path("matiere")
public class MatiereService {
    
    private Connection connexion;
 
    public MatiereService() {
        
    }

    @GET
    @Path("creer")
    public Response creer(  @QueryParam("nom")
                            String nom,
                            @QueryParam("coefficient")
                            float coefficient,
                            @QueryParam("isOption")
                            boolean isOption,
                            @Context 
                            HttpServletRequest request) {
        
        if (request.getSession(false) != null) {
            if (connexion == null) {
                connexion = SQLUtils.getConnexion();
            }

            try {
                Statement stmt = connexion.createStatement();  
                stmt.execute("INSERT INTO MATIERE (nom, coefficient, isOption) VALUES ('" + nom + "', " + coefficient + ", " + isOption + ")");
                stmt.close();    
            } catch (SQLException ex) {
                Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, ex);
                
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
        
        if (request.getSession(false) != null) {
            if (connexion == null) {
                connexion = SQLUtils.getConnexion();
            }

            try {
                try (Statement stmt = connexion.createStatement()) {
                    stmt.executeUpdate("DELETE FROM MATIERE WHERE (ID = " + id + ")");
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
    @Path("getAllByUE/{idUE}")
    @Produces("application/json")
    public ArrayList<Matiere> getAllByUE(   @PathParam("idUE") 
                                            int idUE) {
        
        ArrayList<Matiere> matieres = new ArrayList<>();
                
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        try {
            Statement stmt = connexion.createStatement();
            ResultSet results = stmt.executeQuery(  "SELECT MATIERE.ID, MATIERE.NOM, MATIERE.COEFFICIENT, MATIERE.IS_OPTION " +
                                                    "FROM MATIERE, UE " +
                                                    "WHERE (UE.ID = " + idUE + ") AND (MATIERE.ID_UE = UE.ID)");
            
            Matiere m = null;
            
            while (results.next()) {
                m = new Matiere();
                m.setIdMatiere(results.getInt("ID"));
                m.setNom(results.getString("NOM"));
                m.setCoefficient(results.getFloat("COEFFICIENT"));
                m.setIsOption(results.getBoolean("IS_OPTION"));
                
                matieres.add(m);
            }

            results.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return matieres;
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