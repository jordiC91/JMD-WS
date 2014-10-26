package org.jmd.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.jmd.utils.*;
import org.jmd.metier.UE;

/**
 * Service web gérant les UE (création / suppression / recherche / ...).
 *
 * @author jordi charpentier - yoann vanhoeserlande
 */
@Path("ue")
public class UEService {
    /**
     * Constructeur par défaut de la classe.
     */
    public UEService() {
        
    }
    
    /**
     * Méthode permettant de créer une UE.
     *
     * @param nom Le nom de l'UE à créer.
     * @param yearType Type de l'année (NULL/SEMESTRE/TRIMESTRE)
     * @param idAnnee ID de l'année à laquelle l'UE appartient.
     *
     * @param pseudo Le pseudo de l'administrateur ayant fait la demande.
     * @param token Le token envoyé par l'administrateur.
     * @param timestamp Le timestamp envoyé par l'administrateur ayant fait la requête.
     * Permet d'éviter les rejeux.
     *
     * @return 2 possibilités :
     * - Un code HTTP 200 si l'utilisateur ayant fait la demande de création est
     * connecté (donc autorisé).
     * - Un code HTTP 401 si c'est un utilisateur non connecté (donc non autorisé)
     * qui a fait la demande.
     * - Un code HTTP 500 si une erreur SQL se produit.
     */
    @PUT
    public Response creer(
            @QueryParam("nom")
                    String nom,
            @QueryParam("yearType")
                    String yearType,
            @QueryParam("idAnnee")
                    int idAnnee,
            @QueryParam("pseudo")
                    String pseudo,
            @QueryParam("token")
                    String token,
            @QueryParam("timestamp")
                    long timestamp) {
        
        Connection connexion = null;
        Statement stmt = null;
        
        if (AdminUtils.checkToken(pseudo, token) && AdminUtils.checkTimestamp(pseudo, timestamp)) {
            try {
                connexion = SQLUtils.getConnexion();
                stmt = connexion.createStatement();
                stmt.execute("INSERT INTO UE (NOM, YEAR_TYPE, ID_ANNEE) VALUES ('" + nom + "','"+ yearType +"',"+idAnnee+");");
                stmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(UEService.class.getName()).log(Level.SEVERE, null, ex);
                
                if (stmt != null){
                    try {
                        stmt.close();
                    } catch (SQLException exc) {
                        Logger.getLogger(UEService.class.getName()).log(Level.SEVERE, null, exc);
                    }
                }
                
                if (connexion != null){
                    try {
                        connexion.close();
                    } catch (SQLException exc) {
                        Logger.getLogger(UEService.class.getName()).log(Level.SEVERE, null, exc);
                    }
                }
                
                return Response.status(500).build();
            }
            finally {
                if (stmt != null){
                    try {
                        stmt.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(UEService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                if (connexion != null){
                    try {
                        connexion.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(UEService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
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
     *
     * @param pseudo Le pseudo de l'administrateur ayant fait la demande.
     * @param token Le token envoyé par l'administrateur.
     * @param timestamp Le timestamp envoyé par l'administrateur ayant fait la requête.
     * Permet d'éviter les rejeux.
     *
     * @return 2 possibilités :
     * - Un code HTTP 200 si l'utilisateur ayant fait la demande de suppression est
     * connecté (donc autorisé) et si la suppression s'est bien faite.
     * - Un code HTTP 401 si c'est un utilisateur non connecté (donc non autorisé)
     * qui a fait la demande.
     */
    @DELETE
    public Response supprimer(
            @QueryParam("id")
                    String id,
            @QueryParam("pseudo")
                    String pseudo,
            @QueryParam("token")
                    String token,
            @QueryParam("timestamp")
                    long timestamp) {
        
        Connection connexion = null;
        Statement stmt = null;
        
        if (AdminUtils.checkToken(pseudo, token) && AdminUtils.checkTimestamp(pseudo, timestamp)) {
            try {
                connexion = SQLUtils.getConnexion();
                stmt = connexion.createStatement();
                stmt.executeUpdate("DELETE FROM MATIERE WHERE (ID_UE = " + id + ")");
                stmt.executeUpdate("DELETE FROM UE WHERE (ID = " + id + ")");
            } catch (SQLException ex) {
                Logger.getLogger(UEService.class.getName()).log(Level.SEVERE, null, ex);
                
                if (stmt != null){
                    try {
                        stmt.close();
                    } catch (SQLException exc) {
                        Logger.getLogger(UEService.class.getName()).log(Level.SEVERE, null, exc);
                    }
                }
                
                if (connexion != null){
                    try {
                        connexion.close();
                    } catch (SQLException exc) {
                        Logger.getLogger(UEService.class.getName()).log(Level.SEVERE, null, exc);
                    }
                }
                
                return Response.status(500).build();
            }
            finally {
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(UEService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                if (connexion != null){
                    try {
                        connexion.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(UEService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
            return Response.status(200).build();
        } else {
            return Response.status(401).build();
        }
    }
    
    /**
     * Méthode permettant de récupérer l'ensemble des UE que comprend une année.
     *
     * @param idAnnee L'identifiant de l'année.
     *
     * @return L'ensemble des UE que comprend l'année spécifiée.
     */
    @GET
    @Path("getAllUEOfAnnee")
    @Produces("application/json;charset=utf-8")
    public ArrayList<UE> getAllUEOfAnnee(
            @QueryParam("idAnnee")
                    int idAnnee) {
        
        ArrayList<UE> UEs = new ArrayList<>();
        Connection connexion = null;
        Statement stmt = null;
        ResultSet results = null;
        
        try {
            connexion = SQLUtils.getConnexion();
            stmt = connexion.createStatement();
            results = stmt.executeQuery("SELECT UE.ID, UE.NOM, UE.YEAR_TYPE " +
                    "FROM ANNEE, UE " +
                    "WHERE (ANNEE.ID = " + idAnnee + ") AND (ANNEE.ID = UE.ID_ANNEE)");
            
            UE ue = null;
            
            while (results.next()) {
                ue = new UE();
                ue.setIdUE(results.getInt("UE.ID"));
                ue.setNom(results.getString("UE.NOM"));
                ue.setYearType(results.getString("UE.YEAR_TYPE"));
                ue.setIdAnnee(results.getInt("UE.ID_ANNEE"));
                
                UEs.add(ue);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UEService.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            if (results != null ) {
                try {
                    results.close();
                } catch (SQLException ex) {
                    Logger.getLogger(UEService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (stmt != null){
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(UEService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (connexion != null){
                try {
                    connexion.close();
                } catch (SQLException ex) {
                    Logger.getLogger(UEService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return UEs;
    }
    
    /**
     * Méthode permettant de récupérer les UE que comprend une année selon le
     * découpage spécifié en paramète (UE / TRIMESTRE / NULL).
     *
     * @param idAnnee L'identifiant de l'année.
     * @param yearType Le filtre (type de l'année) spécifié.
     *
     * @return La liste des UEs que comprend l'année pendant le spécifiée.
     */
    @GET
    @Path("getAllUEOfAnneeByYearType")
    @Produces("application/json;charset=utf-8")
    public ArrayList<UE> getAllUEOfAnneeByYearType(
            @QueryParam("idAnnee")
                    int idAnnee,
            @QueryParam("yearType")
                    String yearType) {
        
        ArrayList<UE> UEs = new ArrayList<>();
        Connection connexion = null;
        Statement stmt = null;
        ResultSet results = null;
        
        try {
            connexion = SQLUtils.getConnexion();
            stmt = connexion.createStatement();

            results = stmt.executeQuery("SELECT DISTINCT UE.ID, UE.NOM, UE.YEAR_TYPE, UE.ID_ANNEE " +
                    "FROM ANNEE, UE " +
                    "WHERE (ANNEE.ID = " + idAnnee + ") AND (ANNEE.ID = UE.ID_ANNEE) AND (YEAR_TYPE ='" + yearType + "');");
            
            UE ue = null;
            
            while (results.next()) {
                ue = new UE();
                ue.setIdUE(results.getInt("UE.ID"));
                ue.setNom(results.getString("UE.NOM"));
                ue.setYearType(results.getString("UE.YEAR_TYPE"));
                ue.setIdAnnee(results.getInt("UE.ID_ANNEE"));
                
                UEs.add(ue);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UEService.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            if (results != null ) {
                try {
                    results.close();
                } catch (SQLException ex) {
                    Logger.getLogger(UEService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (stmt != null){
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(UEService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (connexion != null){
                try {
                    connexion.close();
                } catch (SQLException ex) {
                    Logger.getLogger(UEService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return UEs;
    }
}