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
            final String nom,
            @QueryParam("coefficient")
            final float coefficient,
            @QueryParam("isOption")
            final boolean isOption,
            @QueryParam("idUE")
            final int idUE,
            @QueryParam("isRattrapable")
            final boolean isRattrapable,
            @DefaultValue("-1.0")
            @QueryParam("noteMini")
            final float noteMini,
            @QueryParam("pseudo")
            final String pseudo,
            @QueryParam("token")
            final String token,
            @QueryParam("timestamp")
            final long timestamp) {
        
        int idAnnee = 0;
        String nomUE = "";
        
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
                r = stmt.executeQuery("SELECT ANNEE.ID, UE.NOM "
                        + "FROM ANNEE, UE, MATIERE "
                        + "WHERE (ANNEE.ID = UE.ID_ANNEE) "
                        + "AND (UE.ID = MATIERE.ID_UE) "
                        + "AND (UE.ID = " + idUE + ");");
                
                r.next();
                idAnnee = r.getInt("ANNEE.ID");
                nomUE = r.getString("UE.NOM");
                
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
            final String nomUEFin = nomUE;
            
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String message = "";
                    try {
                        Connection connexion = SQLUtils.getConnexion();
                        Statement stmt = connexion.createStatement();
                        
                        ResultSet results = stmt.executeQuery("SELECT ANNEE.NOM, DIPLOME.NOM " +
                                "FROM ANNEE, DIPLOME " +
                                "WHERE ANNEE.ID = " +idAnneeFin+" "+
                                "AND ANNEE.ID_DIPLOME = DIPLOME.ID");
                        
                        results.next();
                        message = results.getString("ANNEE.NOM")+" ("+results.getString("DIPLOME.NOM")+") : la matière \""+ nom +"\" dans l'UE \""+nomUEFin+"\" a été créé par "+pseudo+".";
                        results.close();
                        stmt.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(UEService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    int exceptIdAdmin = AdminUtils.getIdAdmin(pseudo);
                    AdminUtils.notifyMail(message, idAnneeFin, exceptIdAdmin);
                    AdminUtils.notifyAndroid(message, idAnneeFin, exceptIdAdmin);
                    AdminUtils.notifyiOS(message, idAnneeFin, exceptIdAdmin);
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
        String nomUE = "";
        String nomMatiere = "";
        
        Connection connexion = null;
        Statement stmt = null;
        ResultSet r = null;
        
        if (AdminUtils.checkToken(pseudo, token) && AdminUtils.checkTimestamp(pseudo, timestamp)) {
            try {
                connexion = SQLUtils.getConnexion();
                
                
                stmt = connexion.createStatement();
                r = stmt.executeQuery("SELECT ANNEE.ID, UE.NOM, MATIERE.NOM "
                        + "FROM ANNEE, UE, MATIERE "
                        + "WHERE (ANNEE.ID = UE.ID_ANNEE) "
                        + "AND (UE.ID = MATIERE.ID_UE) "
                        + "AND (MATIERE.ID = " + id + ");");
                
                r.next();
                idAnnee = r.getInt("ANNEE.ID");
                nomUE = r.getString("UE.NOM");
                nomMatiere = r.getString("MATIERE.NOM");
                
                r.close();
                stmt.close();
                
                stmt = connexion.createStatement();
                stmt.executeUpdate("DELETE FROM MATIERE WHERE (ID = " + id + ")");
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
            final String nomUEFin = nomUE;
            final String nomMatiereFin = nomMatiere;
            
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String message = "";
                    try {
                        Connection connexion = SQLUtils.getConnexion();
                        Statement stmt = connexion.createStatement();
                        
                        ResultSet results = stmt.executeQuery("SELECT ANNEE.NOM, DIPLOME.NOM " +
                                "FROM ANNEE, DIPLOME " +
                                "WHERE ANNEE.ID = " +idAnneeFin+" "+
                                "AND ANNEE.ID_DIPLOME = DIPLOME.ID");
                        
                        results.next();
                        message = results.getString("ANNEE.NOM")+" ("+results.getString("DIPLOME.NOM")+") : la matière \""+nomMatiereFin+"\" de l'UE \""+ nomUEFin +"\" a été supprimée par "+pseudo+".";
                        results.close();
                        stmt.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(UEService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    int exceptIdAdmin = AdminUtils.getIdAdmin(pseudo);
                    AdminUtils.notifyMail(message, idAnneeFin, exceptIdAdmin);
                    AdminUtils.notifyAndroid(message, idAnneeFin, exceptIdAdmin);
                    AdminUtils.notifyiOS(message, idAnneeFin, exceptIdAdmin);
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