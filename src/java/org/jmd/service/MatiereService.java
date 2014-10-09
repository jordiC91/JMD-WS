package org.jmd.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.*;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.jmd.SQLUtils;
import org.jmd.metier.Matiere;

/**
 * Service web gérant les matières (création / suppression / ...).
 * 
 * @author jordi charpentier - yoann vanhoeserlande
 */
@Path("matiere")
public class MatiereService {
    
    private Connection connexion;
 
    public MatiereService() {
        
    }

    /**
     * Méthode permettant de créer une matière.
     * 
     * @param nom Le nom de la matière à créer.
     * @param coefficient Le coefficient de la matière à créer.
     * @param isOption Si la matière à créer est une option ou non.
     * @param request La requête HTTP ayant appelée le service.
     * 
     * @return 2 possibilités :
     * - Un code HTTP 200 si l'utilisateur ayant fait la demande de création est
     * connecté (donc autorisé).
     * - Un code HTTP 401 si c'est un utilisateur non connecté (donc non autorisé)
     * qui a fait la demande.
     */
    @GET
    @Path("creer")
    public Response creer(@QueryParam("nom")
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
            }
            
            return Response.status(200).build();
        } else {
            return Response.status(401).build();
        }
    }
    
    /**
     * Méthode permettant de supprimer une matière.
     * 
     * @param id L'identifiant de la matière à supprimer.
     * @param request La requête HTTP ayant appelée le service.
     * 
     * @return 2 possibilités :
     * - Un code HTTP 200 si l'utilisateur ayant fait la demande de supprimé est
     * connecté (donc autorisé).
     * - Un code HTTP 401 si c'est un utilisateur non connecté (donc non autorisé)
     * qui a fait la demande.
     */
    @DELETE
    @Path("{id}")
    public Response supprimer(@QueryParam("id")
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
    @Path("getAllMatieretOfUE/{idUE}")
    @Produces("application/json")
    public ArrayList<Matiere> getAllMatieretOfUE(@PathParam("idUE") 
                                                 int idUE) {
        
        ArrayList<Matiere> matieres = new ArrayList<>();
                
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        try {
            Statement stmt = connexion.createStatement();
            ResultSet results = stmt.executeQuery("SELECT MATIERE.ID, MATIERE.NOM, MATIERE.COEFFICIENT, MATIERE.IS_OPTION " +
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
    
    @GET
    @Path("getAllMatiereOfYear/{idAnnee}")
    @Produces("application/json")
    public ArrayList<Matiere> getAllMatiereOfYear(@PathParam("idAnnee") 
                                                  int idAnnee) {
        
        ArrayList<Matiere> matieres = new ArrayList<>();
                
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        try {
            Statement stmt = connexion.createStatement();
            ResultSet results = stmt.executeQuery("SELECT * " +
                                                  "FROM MATIERE, UE_MAT, UE, ANN_UE, ANNEE " +
                                                  "WHERE (UE.ID = UE_MAT.ID_UE) AND (MATIERE.ID = UE_MAT.ID_MAT) AND (ANN_UE.ID_ANN = ANNEE.ID) AND (ANN_UE.ID_UE = UE.ID) AND (ANNEE.ID = " + idAnnee + ")");
            
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
                Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}