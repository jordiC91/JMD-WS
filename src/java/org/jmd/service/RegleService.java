package org.jmd.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.jmd.metier.Regle;
import org.jmd.utils.*;

/**
 * Service web gérant les règles (création / suppression / recherche / ...).
 *
 * @author jordi charpentier - yoann vanhoeserlande
 */
@Path("regle")
public class RegleService {
    /**
     * Constructeur par défaut de la classe.
     */
    public RegleService() {
        
    }
    
    /**
     * Méthode permettant de créer une règle.
     *
     * @param regle La règle (0 ou 1, NB_OPT_MINI ou NOTE_MINIMALE).
     * @param operateur L'opérateur de la règle : >, <, <=, ...
     * @param valeur La valeur de la règle.
     * @param idAnnee L'identifiant de l'année rattachée à la règle.
     * @param idUE L'identifiant de l'UE rattachée à la règle.
     * @param idMatiere L'identifiant de la matière rattachée à la règle.
     *
     * @param pseudo Le pseudo de l'administrateur ayant fait la demande.
     * @param token Le token envoyé par l'administrateur.
     * @param timestamp Le timestamp envoyé par l'administrateur ayant fait la requête.
     * Permet d'éviter les rejeux.
     *
     * @return 3 possibilités :
     * - Un code HTTP 200 si l'utilisateur ayant fait la demande de création est
     * connecté (donc autorisé).
     * - Un code HTTP 401 si c'est un utilisateur non connecté (donc non autorisé)
     * qui a fait la demande.
     * - Un code HTTP 500 si une erreur SQL se produit.
     */
    @PUT
    public Response creer(
            @QueryParam("regle")
                    int regle,
            @QueryParam("operateur")
                    int operateur,
            @QueryParam("valeur")
                    int valeur,
            @QueryParam("idAnnee")
                    int idAnnee,
            @QueryParam("idUE")
                    int idUE,
            @QueryParam("idMatiere")
                    int idMatiere,
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
                stmt.execute("INSERT INTO REGLE (REGLE, ID_ANNEE, ID_UE, ID_MATIERE, OPERATEUR, VALEUR) VALUES (" + regle + "," + idAnnee + "," + idUE + "," + idMatiere + ", " + operateur + ", " + valeur + ");");
                stmt.close();
            }
            catch (SQLException ex) {
                Logger.getLogger(RegleService.class.getName()).log(Level.SEVERE, null, ex);
                if(stmt != null){
                    try {
                        stmt.close();
                    } catch (SQLException exc) {
                        Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, exc);
                    }
                }
                if (connexion != null){
                    try {
                        connexion.close();
                    } catch (SQLException exc) {
                        Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, exc);
                    }
                }
                return Response.status(500).build();
            }
            finally {
                if(stmt != null){
                    try {
                        stmt.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (connexion != null){
                    try {
                        connexion.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
            return Response.status(200).build();
        }
        else {
            return Response.status(401).build();
        }
    }
    
    /**
     * Méthode permettant de supprimer une règle.
     *
     * @param id L'identifiant de la règle à supprimer.
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
                stmt.executeUpdate("DELETE FROM REGLE WHERE (ID = " + id + ")");
                stmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(RegleService.class.getName()).log(Level.SEVERE, null, ex);
                if(stmt != null){
                    try {
                        stmt.close();
                    } catch (SQLException exc) {
                        Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, exc);
                    }
                }
                if (connexion != null){
                    try {
                        connexion.close();
                    } catch (SQLException exc) {
                        Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, exc);
                    }
                }
                return Response.status(500).build();
            }
            finally {
                if(stmt != null){
                    try {
                        stmt.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (connexion != null){
                    try {
                        connexion.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
            return Response.status(200).build();
        }
        else {
            return Response.status(401).build();
        }
    }
    
    /**
     * Méthode permettant de récupérer l'ensemble des règles d'une année.
     *
     * @param idAnnee L'identifiant de l'année.
     *
     * @return La liste des règles de l'année spécifiée.
     */
    @GET
    @Path("getAllByAnnee")
    @Produces("application/json")
    public ArrayList<Regle> getAllByAnnee(
            @QueryParam("idAnnee")
                    int idAnnee) {
        
        ArrayList<Regle> regles = new ArrayList<>();
        Connection connexion = null;
        Statement stmt = null;
        ResultSet results = null;
        
        try {
            connexion = SQLUtils.getConnexion();
            stmt = connexion.createStatement();
            results = stmt.executeQuery("SELECT * "
                    + "FROM ANNEE, REGLE "
                    + "WHERE (ID_ANNEE=" + idAnnee + ") AND (ANNEE.ID = REGLE.ID_ANNEE);");
            
            Regle r = null;
            
            while (results.next()) {
                r = new Regle();
                r.setId(results.getInt("REGLE.ID"));
                r.setIdAnnee(results.getInt("REGLE.ID_ANNEE"));
                r.setIdMatiere(results.getInt("REGLE.ID_MATIERE"));
                r.setIdUE(results.getInt("REGLE.ID_UE"));
                r.setOperateur(results.getInt("REGLE.OPERATEUR"));
                r.setRegle(results.getInt("REGLE.REGLE"));
                r.setValeur(results.getInt("REGLE.VALEUR"));
                
                regles.add(r);
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            if( results != null ) {
                try {
                    results.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if(stmt != null){
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (connexion != null){
                try {
                    connexion.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return regles;
    }
    
    /**
     * Méthode exécutée avant la fin de vie du service.
     * La connexion à la base est fermée.
     */
    /*
    @PreDestroy
    public void onDestroy() {
    if (connexion != null) {
    try {
    connexion.close();
    } catch (SQLException ex) {
    Logger.getLogger(RegleService.class.getName()).log(Level.SEVERE, null, ex);
    }
    }
    }*/
}