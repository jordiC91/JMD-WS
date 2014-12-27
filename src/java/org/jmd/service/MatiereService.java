package org.jmd.service;

import org.jmd.utils.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.jmd.metier.Matiere;

/**
 * Service web gérant les matières (création / suppression / recherche / ...).
 *
 * @author jordi charpentier - yoann vanhoeserlande
 */
@Path("matiere")
public class MatiereService {
    
    /**
     * Constructeur par défaut de la classe.
     */
    public MatiereService() {
        
    }
    
    /**
     * Méthode permettant de créer une matière.
     *
     * @param nom Le nom de la matière à créer.
     * @param coefficient Le coefficient de la matière à créer.
     * @param isOption Si la matière à créer est une option ou non.
     * @param idUE ID de l'UE auquel appartient la matière
     * @param isRattrapable Si la matière est rattrapable ou non.
     * @param noteMini La note minimale de la matière.
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
            @QueryParam("nom")
                    String nom,
            @QueryParam("coefficient")
                    float coefficient,
            @QueryParam("isOption")
                    boolean isOption,
            @QueryParam("idUE")
                    final int idUE,
            @QueryParam("isRattrapable")
                    boolean isRattrapable,
            @DefaultValue("-1.0")
            @QueryParam("noteMini")
                    float noteMini,
            @QueryParam("pseudo")
                    final String pseudo,
            @QueryParam("token")
                    String token,
            @QueryParam("timestamp")
                    long timestamp) {
        
        int idAnnee = 0;
        
        Connection connexion = null;
        Statement stmt = null;
        ResultSet r = null;
        
        if (AdminUtils.checkToken(pseudo, token) && AdminUtils.checkTimestamp(pseudo, timestamp)) {
            try {
                connexion = SQLUtils.getConnexion();
                stmt = connexion.createStatement();
                stmt.execute("INSERT INTO MATIERE (NOM, COEFFICIENT, IS_OPTION, ID_UE, IS_RATTRAPABLE, NOTE_MINI) VALUES ('" + nom + "', " + coefficient + ", " + isOption + ","+idUE+ ", " + isRattrapable + ", " + noteMini + ");");
                stmt.close();
                
                stmt = connexion.createStatement();
                r = stmt.executeQuery("SELECT ANNEE.ID "
                                    + "FROM ANNEE, UE, MATIERE "
                                    + "WHERE (ANNEE.ID = UE.ID_ANNEE) "
                                        + "AND (UE.ID = MATIERE.ID_UE) "
                                        + "AND (UE.ID = " + idUE + ");");
                
                while (r.next()) {
                    idAnnee = r.getInt("ANNEE.ID");
                }
                
                r.close();
                stmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, ex);
                
                if (r != null){
                    try {
                        r.close();
                    } catch (SQLException exc) {
                        Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, exc);
                    }
                }
                
                if (stmt != null){
                    try {
                        stmt.close();
                    } catch (SQLException exc) {
                        Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, exc);
                    }
                }
                
                if (connexion != null){
                    try {
                        connexion.close();
                    } catch (SQLException exc) {
                        Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, exc);
                    }
                }
                
                return Response.status(500).build();
            } 
            finally {
                if (r != null){
                    try {
                        r.close();
                    } catch (SQLException exc) {
                        Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, exc);
                    }
                }
                
                if (stmt != null){
                    try {
                        stmt.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (connexion != null){
                    try {
                        connexion.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
            final int idAnneeFin = idAnnee;
            
            new Thread(new Runnable() {
                @Override
                public void run() {
                   AdminUtils.notify(pseudo, idAnneeFin);
                }
            }).start();
            
            return Response.status(200).build();
        } else {
            return Response.status(401).build();
        }
    }
    
    /**
     * Méthode permettant de supprimer une matière.
     *
     * @param id L'identifiant de la matière à supprimer.
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
                    final String pseudo,
            @QueryParam("token")
                    String token,
            @QueryParam("timestamp")
                    long timestamp) {
        
        int idAnnee = 0;
        
        Connection connexion = null;
        Statement stmt = null;
        ResultSet r = null;
        
        if (AdminUtils.checkToken(pseudo, token) && AdminUtils.checkTimestamp(pseudo, timestamp)) {
            try {
                connexion = SQLUtils.getConnexion();
                stmt = connexion.createStatement();
                stmt.executeUpdate("DELETE FROM MATIERE WHERE (ID = " + id + ")");
                stmt.close();
                
                stmt = connexion.createStatement();
                r = stmt.executeQuery("SELECT ANNEE.ID "
                                    + "FROM ANNEE, UE, MATIERE "
                                    + "WHERE (ANNEE.ID = UE.ID_ANNEE) "
                                        + "AND (UE.ID = MATIERE.ID_UE) "
                                        + "AND (MATIERE.ID = " + id + ");");
                
                while (r.next()) {
                    idAnnee = r.getInt("ANNEE.ID");
                }
                
                r.close();
                stmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, ex);
                
                if (r != null){
                    try {
                        r.close();
                    } catch (SQLException exc) {
                        Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, exc);
                    }
                }
                
                if (stmt != null){
                    try {
                        stmt.close();
                    } catch (SQLException exc) {
                        Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, exc);
                    }
                }
                
                if (connexion != null){
                    try {
                        connexion.close();
                    } catch (SQLException exc) {
                        Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, exc);
                    }
                }
                
                return Response.status(500).build();
            }
            finally {
                if (r != null){
                    try {
                        r.close();
                    } catch (SQLException exc) {
                        Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, exc);
                    }
                }
                
                if (stmt != null){
                    try {
                        stmt.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                if (connexion != null){
                    try {
                        connexion.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
            final int idAnneeFin = idAnnee;
            
            new Thread(new Runnable() {
                @Override
                public void run() {
                   AdminUtils.notify(pseudo, idAnneeFin);
                }
            }).start();
            
            return Response.status(200).build();
        } else {
            return Response.status(401).build();
        }
    }
    
    /**
     * Méthode permettant de récupérer l'ensemble des matières d'une UE.
     * 
     * @param idUE L'identifiant de l'UE recherchée.
     * 
     * @return La liste de l'ensemble des matières de l'UE spécifiée.
     */
    @GET
    @Path("getAllMatieretOfUE")
    @Produces("application/json;charset=utf-8")
    public ArrayList<Matiere> getAllMatieretOfUE(
            @QueryParam("idUE")
                    int idUE) {
        
        ArrayList<Matiere> matieres = new ArrayList<>();
        Connection connexion = null;
        Statement stmt = null;
        ResultSet results = null;
        
        try {
            connexion = SQLUtils.getConnexion();
            stmt = connexion.createStatement();
            results = stmt.executeQuery("SELECT MATIERE.ID, MATIERE.NOM, MATIERE.COEFFICIENT, MATIERE.IS_OPTION, MATIERE.IS_RATTRAPABLE, MATIERE.NOTE_MINI " +
                    "FROM MATIERE, UE " +
                    "WHERE (UE.ID = " + idUE + ") AND (MATIERE.ID_UE = UE.ID);");
            
            Matiere m = null;
            
            while (results.next()) {
                m = new Matiere();
                m.setIdMatiere(results.getInt("MATIERE.ID"));
                m.setNom(results.getString("MATIERE.NOM"));
                m.setCoefficient(results.getFloat("MATIERE.COEFFICIENT"));
                m.setIsOption(results.getBoolean("MATIERE.IS_OPTION"));
                m.setIsRattrapable(results.getBoolean("MATIERE.IS_RATTRAPABLE"));
                m.setNoteMini(results.getFloat("MATIERE.NOTE_MINI"));
                
                matieres.add(m);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            if (results != null ) {
                try {
                    results.close();
                } catch (SQLException ex) {
                    Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (stmt != null){
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
        
        return matieres;
    }
    
    /**
     * Méthode permettant de récupérer l'ensemble des matières d'une année.
     * 
     * @param idAnnee L'identifiant de l'année recherchée.
     * 
     * @return La liste de l'ensemble des matières de l'année spécifiée.
     */
    @GET
    @Path("getAllMatiereOfYear")
    @Produces("application/json;charset=utf-8")
    public ArrayList<Matiere> getAllMatiereOfYear(
            @QueryParam("idAnnee")
                    int idAnnee) {
        
        ArrayList<Matiere> matieres = new ArrayList<>();
        Connection connexion = null;
        Statement stmt = null;
        ResultSet results = null;
        
        try {
            connexion = SQLUtils.getConnexion();
            stmt = connexion.createStatement();
            results = stmt.executeQuery("SELECT MATIERE.ID, MATIERE.NOM, MATIERE.COEFFICIENT, MATIERE.IS_OPTION, MATIERE.IS_RATTRAPABLE, MATIERE.NOTE_MINI " +
                    "FROM MATIERE, UE, ANNEE " +
                    "WHERE (UE.ID = MATIERE.ID_UE) AND (UE.ID_ANNEE = ANNEE.ID)  AND (ANNEE.ID = " + idAnnee + ");");
            
            Matiere m = null;
            
            while (results.next()) {
                m = new Matiere();
                m.setIdMatiere(results.getInt("MATIERE.ID"));
                m.setNom(results.getString("MATIERE.NOM"));
                m.setCoefficient(results.getFloat("MATIERE.COEFFICIENT"));
                m.setIsOption(results.getBoolean("MATIERE.IS_OPTION"));
                m.setIsRattrapable(results.getBoolean("MATIERE.IS_RATTRAPABLE"));
                m.setNoteMini(results.getFloat("MATIERE.NOTE_MINI"));

                matieres.add(m);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            if (results != null ) {
                try {
                    results.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (stmt != null){
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
        
        return matieres;
    }
}