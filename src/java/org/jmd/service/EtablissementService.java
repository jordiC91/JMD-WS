package org.jmd.service;

import org.jmd.utils.*;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.*;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import org.jmd.metier.Etablissement;

/**
 * Service web gérant les établissements (création / suppression / recherche / ...).
 *
 * @author jordi charpentier - yoann vanhoeserlande
 */
@Path("etablissement")
public class EtablissementService {
    /**
     * Constructeur par défaut de la classe.
     */
    public EtablissementService() {
        
    }
    
    /**
     * Méthode permettant de créer un établissement.
     *
     * @param nom Le nom de l'établissement.
     * @param ville La ville de l'établissement.
     *
     * @param pseudo Le pseudo de l'administrateur ayant fait la demande.
     * @param token Le token envoyé par l'administrateur.
     * @param timestamp Le timestamp envoyé par l'administrateur ayant fait la requête.
     * Permet d'éviter les rejeux.
     *
     * @return 4 possibilités :
     * - Un code HTTP 200 si l'utilisateur ayant fait la demande de création est
     * connecté (donc autorisé).
     * - Un code HTTP 401 si c'est un utilisateur non connecté (donc non autorisé)
     * qui a fait la demande.
     * - Un code HTTP 403 si l'établissement à créer existe déjà en base.
     * - Un code HTTP 500 si une erreur SQL se produit.
     */
    @PUT
    public Response creer(
            @QueryParam("nom")
                    String nom,
            @QueryParam("ville")
                    String ville,
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
                stmt.execute("INSERT INTO ETABLISSEMENT (nom, ville) VALUES ('" + nom + "', '" + ville + "')");
                stmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(EtablissementService.class.getName()).log(Level.SEVERE, null, ex);
                if(stmt != null){
                    try {
                        stmt.close();
                    } catch (SQLException exc) {
                        Logger.getLogger(EtablissementService.class.getName()).log(Level.SEVERE, null, exc);
                    }
                }
                if (connexion != null){
                    try {
                        connexion.close();
                    } catch (SQLException exc) {
                        Logger.getLogger(EtablissementService.class.getName()).log(Level.SEVERE, null, exc);
                    }
                }
                if(ex instanceof MySQLIntegrityConstraintViolationException){
                    return Response.status(403).entity("DUPLICATE_ENTRY").build();
                }
                
                return Response.status(500).build();
            }
            finally {
                if(stmt != null){
                    try {
                        stmt.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(EtablissementService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (connexion != null){
                    try {
                        connexion.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(EtablissementService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
            return Response.status(200).build();
        } else {
            return Response.status(401).build();
        }
    }
    
    /**
     * Méthode permettant de supprimer un établissement.
     *
     * @param id L'identifiant de l'établissement à supprimer.
     *
     * @param pseudo Le pseudo de l'administrateur ayant fait la demande.
     * @param token Le token envoyé par l'administrateur.
     * @param timestamp Le timestamp envoyé par l'administrateur ayant fait la requête.
     * Permet d'éviter les rejeux.
     *
     * @return 3 possibilités :
     * - Un code HTTP 200 si l'utilisateur ayant fait la demande de suppression est
     * connecté (donc autorisé) et si la suppression s'est bien faite.
     * - Un code HTTP 401 si c'est un utilisateur non connecté (donc non autorisé)
     * qui a fait la demande.
     * - Un code HTTP 500 si une erreur SQL se produit
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
                stmt.executeUpdate("DELETE FROM ETABLISSEMENT WHERE (ID = " + id + ")");
                stmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(EtablissementService.class.getName()).log(Level.SEVERE, null, ex);
                
                if (stmt != null){
                    try {
                        stmt.close();
                    } catch (SQLException exc) {
                        Logger.getLogger(EtablissementService.class.getName()).log(Level.SEVERE, null, exc);
                    }
                }
                
                if (connexion != null) {
                    try {
                        connexion.close();
                    } catch (SQLException exc) {
                        Logger.getLogger(EtablissementService.class.getName()).log(Level.SEVERE, null, exc);
                    }
                }     
                
                return Response.status(500).build();
            }
            finally {
                if (stmt != null){
                    try {
                        stmt.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(EtablissementService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                if (connexion != null) {
                    try {
                        connexion.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(EtablissementService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
            return Response.status(200).build();
        } else {
            return Response.status(401).build();
        }
    }
    
    /**
     * Méthode permettant de récupérer l'ensemble des établissements présents
     * en base.
     *
     * @return Une liste (<i>ArrayList</i> correspondant à l'ensemble des
     * établissements présents en base.
     */
    @GET
    @Path("getAll")
    @Produces("application/json;charset=utf-8")
    public ArrayList<Etablissement> getAll() {
        ArrayList<Etablissement> etablissements = new ArrayList<>();
        Connection connexion = null;
        Statement stmt = null;
        ResultSet results = null;
        
        try {
            connexion = SQLUtils.getConnexion();
            stmt = connexion.createStatement();
            results = stmt.executeQuery("SELECT * " +
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
        } catch (SQLException ex) {
            Logger.getLogger(EtablissementService.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            if (results != null ) {
                try {
                    results.close();
                } catch (SQLException ex) {
                    Logger.getLogger(EtablissementService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(EtablissementService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (connexion != null) {
                try {
                    connexion.close();
                } catch (SQLException ex) {
                    Logger.getLogger(EtablissementService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return etablissements;
    }
}