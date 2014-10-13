package org.jmd.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.*;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.jmd.SQLUtils;
import org.jmd.metier.UE;

/**
 * Service web gérant les UE (création / suppression / ...).
 *
 * @author jordi charpentier - yoann vanhoeserlande
 */
@Path("ue")
public class UEService {
    
    private Connection connexion;
    
    public UEService() {
        
    }
    
    /**
     * Méthode permettant de créer une UE.
     *
     * @param nom Le nom de l'UE à créer.
     * @param yearType Type de l'année (NULL/SEMESTRE/TRIMESTRE)
     * @param request La requête HTTP ayant appelée le service.
     * @param idAnnee ID de l'année à laquelle l'UE appartient
     *
     * @return 2 possibilités :
     * - Un code HTTP 200 si l'utilisateur ayant fait la demande de création est
     * connecté (donc autorisé).
     * - Un code HTTP 401 si c'est un utilisateur non connecté (donc non autorisé)
     * qui a fait la demande.
     */
    @PUT
    public Response creer(
            @QueryParam("nom")
                    String nom,
            @QueryParam("yearType")
                    String yearType,
            @QueryParam("idAnnee")
                    String idAnnee,
            @Context
                    HttpServletRequest request) {
        
        if (request.getSession(false) != null) {
            if (connexion == null) {
                connexion = SQLUtils.getConnexion();
            }
            
            try {
                Statement stmt = connexion.createStatement();
                stmt.execute("INSERT INTO UE (NOM, YEAR_TYPE, ID_ANNEE) VALUES ('" + nom + ",'"+ yearType +"',"+idAnnee+");");
                stmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(UEService.class.getName()).log(Level.SEVERE, null, ex);
                
                return Response.status(500).build();
            }
            
            return Response.status(200).build();
        } else {
            return Response.status(401).build();
        }
    }
    
    /**
     * Méthode permettant de supprimer une UE.
     *
     * @param id L'identifiant de l'UE à supprimer.
     * @param request La requête HTTP ayant appelée le service.
     *
     * @return 2 possibilités :
     * - Un code HTTP 200 si l'utilisateur ayant fait la demande de suppression est
     * connecté (donc autorisé).
     * - Un code HTTP 401 si c'est un utilisateur non connecté (donc non autorisé)
     * qui a fait la demande.
     */
    @DELETE
    public Response supprimer(
            @QueryParam("id")
                    String id,
            @Context
                    HttpServletRequest request) {
        
        if (request.getSession().getAttribute("pseudo") != null) {
            if (connexion == null) {
                connexion = SQLUtils.getConnexion();
            }
            
            try {
                try (Statement stmt = connexion.createStatement()) {
                    stmt.executeUpdate("DELETE FROM UE WHERE (ID = " + id + ")");
                    
                    // A FAIRE SUPPRESSION EN CASCADE
                    
                }
            } catch (SQLException ex) {
                Logger.getLogger(UEService.class.getName()).log(Level.SEVERE, null, ex);
                
                return Response.status(500).build();
            }
            
            return Response.status(200).build();
        } else {
            return Response.status(401).build();
        }
    }
    
    @GET
    @Path("getAllUEOfAnnee")
    @Produces("application/json")
    public ArrayList<UE> getAllUEOfAnnee(
            @QueryParam("idAnnee")
            int idAnnee) {
        
        ArrayList<UE> UEs = new ArrayList<>();
        
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        try {
            Statement stmt = connexion.createStatement();
            ResultSet results = stmt.executeQuery("SELECT UE.ID, UE.NOM, UE.YEAR_TYPE " +
                    "FROM ANNEE, UE " +
                    "WHERE (ANNEE.ID = " + idAnnee + ") AND (ANNEE.ID = UE.ID_ANNEE)");
            
            UE ue = null;
            
            while (results.next()) {
                ue = new UE();
                ue.setIdUE(results.getInt("UE.ID"));
                ue.setNom(results.getString("UE.NOM"));
                ue.setYearType(results.getString("UE.YEAR_TYPE"));
                
                UEs.add(ue);
            }
            
            results.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return UEs;
    }
    
    @GET
    @Path("getAllUEOfAnneeByYearType")
    @Produces("application/json")
    public ArrayList<UE> getAllUEOfAnneeByYearType(
            @QueryParam("idAnnee")
                    int idAnnee,
            @QueryParam("yearType")
                    String yearType) {
        
        ArrayList<UE> UEs = new ArrayList<>();
        
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        try {
            Statement stmt = connexion.createStatement();
            ResultSet results = stmt.executeQuery("SELECT * " +
                    "FROM ANNEE, UE " +
                    "WHERE (ANNEE.ID = " + idAnnee + ") AND (ANNEE.ID = UE.ID_ANNEE) AND (UE.ID = MATIERE.ID_UE) AND (YEAR_TYPE ='" + yearType + "');");
            
            UE ue = null;
            
            while (results.next()) {
                ue = new UE();
                ue.setIdUE(results.getInt("UE.ID"));
                ue.setNom(results.getString("UE.NOM"));
                ue.setYearType(results.getString("UE.YEAR_TYPE"));
                
                UEs.add(ue);
            }
            
            results.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return UEs;
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
                Logger.getLogger(UEService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}