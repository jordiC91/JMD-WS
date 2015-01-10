package org.jmd.service;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.jmd.utils.*;
import org.jmd.metier.*;

/**
 * Service web gérant les années (création / suppression / recherche / ...).
 *
 * @author jordi charpentier - yoann vanhoeserlande
 */
@Path("annee")
public class AnneeService {
    
    /**
     * Constructeur par défaut de la classe.
     */
    public AnneeService() {
        
    }
    
    /**
     * Méthode permettant de créer une année.
     *
     * @param nom Le nom de l'année.
     * @param decoupage Le découpage de l'année (UE / TRIMESTRE / NULL).
     * @param isLastYear Booléen permettant de savoir si l'année est la dernière
     * du diplôme.
     * @param idEtablissement L'identifiant de l'établissement de l'année.
     * @param idDiplome L'identifiant du diplôme dont fait partie l'année.
     *
     * @param pseudo Le pseudo de l'administrateur ayant fait la demande.
     * @param token Le token envoyé par l'administrateur.
     * @param timestamp Le timestamp envoyé par l'administrateur ayant fait la
     * requête. Permet d'éviter les rejeux.
     *
     * @return 4 possibilités :
     * - Un code HTTP 200 si l'utilisateur ayant fait la demande de création est connecté (donc autorisé).
     * - Un code HTTP 401 si c'est un utilisateur non connecté (donc non autorisé)
     * qui a fait la demande.
     * - Un code HTTP 403 si l'année à créer existe déjà en base.
     * - Un code HTTP 500 si une erreur SQL se produit.
     */
    @PUT
    public Response creer(
            @QueryParam("nom") String nom,
            @QueryParam("decoupage") String decoupage,
            @QueryParam("isLastYear") boolean isLastYear,
            @QueryParam("idEtablissement") String idEtablissement,
            @QueryParam("idDiplome") String idDiplome,
            @QueryParam("pseudo") String pseudo,
            @QueryParam("token") String token,
            @QueryParam("timestamp") long timestamp) {
        
        Connection connexion = null;
        Statement stmt = null;
        
        try {
            if (AdminUtils.checkToken(pseudo, token) && AdminUtils.checkTimestamp(pseudo, timestamp)) {
                connexion = SQLUtils.getConnexion();
                stmt = connexion.createStatement();
                stmt.execute("INSERT INTO ANNEE (NOM,DECOUPAGE,IS_LAST_YEAR,ID_ETABLISSEMENT,ID_DIPLOME) VALUES ('" + nom + "','" + decoupage + "'," + isLastYear + ",'" + idEtablissement + "','" + idDiplome + "');");
                stmt.close();
                connexion.close();
                return Response.status(200).build();
            } else {
                return Response.status(401).build();
            }
        } catch (SQLException ex) {
            Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
            
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException exc) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, exc);
                }
            }
            
            if (connexion != null) {
                try {
                    connexion.close();
                } catch (SQLException exc) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, exc);
                }
            }
            
            if (ex instanceof MySQLIntegrityConstraintViolationException) {
                return Response.status(403).entity("DUPLICATE_ENTRY").build();
            }
            
            return Response.status(500).build();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (connexion != null) {
                try {
                    connexion.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    /**
     * Méthode permettant de supprimer une année.
     *
     * @param id L'identifiant de l'année à supprimer.
     *
     * @param pseudo Le pseudo de l'administrateur ayant fait la demande.
     * @param token Le token envoyé par l'administrateur.
     * @param timestamp Le timestamp envoyé par l'administrateur ayant fait la
     * requête. Permet d'éviter les rejeux.
     *
     * @return 3 possibilités :
     * - Un code HTTP 200 si l'utilisateur ayant fait la demande de suppression
     * est connecté (donc autorisé) et si la suppression s'est bien faite.
     * - Un code HTTP 401 si c'est un utilisateur non connecté (donc non autorisé) qui a fait la demande.
     * - Un code HTTP 500 si une erreur SQL se produit.
     */
    @DELETE
    public Response supprimer(
            @QueryParam("id")
            final int id,
            @QueryParam("pseudo")
            final String pseudo,
            @QueryParam("token")
                    String token,
            @QueryParam("timestamp")
                    long timestamp) {
        
        Connection connexion = null;
        Statement stmt1 = null;
        Statement stmt2 = null;
        ResultSet results1 = null;
        ResultSet results2 = null;
        String annee = "";
        String diplome = "";
        
        if (AdminUtils.checkToken(pseudo, token) && AdminUtils.checkTimestamp(pseudo, timestamp)) {
            try {
                connexion = SQLUtils.getConnexion();
                
                Statement stmt = connexion.createStatement();
                
                ResultSet results = stmt.executeQuery("SELECT ANNEE.NOM, DIPLOME.NOM " +
                        "FROM ANNEE, DIPLOME " +
                        "WHERE ANNEE.ID = " +id+" "+
                        "AND ANNEE.ID_DIPLOME = DIPLOME.ID");
                
                results.next();
                diplome = results.getString("DIPLOME.NOM");
                annee = results.getString("ANNEE.NOM");
                results.close();
                stmt.close();
                
                stmt1 = connexion.createStatement();
                stmt2 = connexion.createStatement();
                
                results1 = stmt1.executeQuery("SELECT * FROM UE WHERE (ID_ANNEE = " + id + ")");
                
                ArrayList<Integer> idUEList = new ArrayList<>();
                ArrayList<Integer> idMatiereList = new ArrayList<>();
                
                while (results1.next()) {
                    idUEList.add(results1.getInt("ID"));
                    
                    stmt2 = connexion.createStatement();
                    results2 = stmt2.executeQuery("SELECT * FROM MATIERE WHERE (ID_UE = " + results1.getInt("ID") + ")");
                    
                    while (results2.next()) {
                        idMatiereList.add(results2.getInt("ID"));
                    }
                    
                    results2.close();
                    stmt2.close();
                }
                
                results1.close();
                stmt1.close();
                
                stmt2 = connexion.createStatement();
                
                /*
                // Suppression des matières de l'année.
                for (Integer idMatiereListe : idMatiereList) {
                    stmt2.executeUpdate("DELETE FROM MATIERE WHERE (ID = " + idMatiereListe + ")");
                }
                
                // Suppression des UE de l'année.
                for (Integer idUEListe : idUEList) {
                    stmt2.executeUpdate("DELETE FROM UE WHERE (ID = " + idUEListe + ")");
                }
                */
                
                stmt2.executeUpdate("DELETE FROM ANNEE WHERE (ID = " + id + ");");
                stmt2.close();                
                connexion.close();
            } catch (SQLException ex) {
                Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                
                if (results1 != null) {
                    try {
                        results1.close();
                    } catch (SQLException exc) {
                        Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, exc);
                    }
                }
                
                if (results2 != null) {
                    try {
                        results2.close();
                    } catch (SQLException exc) {
                        Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, exc);
                    }
                }
                
                if (stmt1 != null) {
                    try {
                        stmt1.close();
                    } catch (SQLException exc) {
                        Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, exc);
                    }
                }
                
                if (stmt2 != null) {
                    try {
                        stmt2.close();
                    } catch (SQLException exc) {
                        Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, exc);
                    }
                }
                
                if (connexion != null) {
                    try {
                        connexion.close();
                    } catch (SQLException exc) {
                        Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, exc);
                    }
                }
                
                return Response.status(500).build();
            }
        } else {
            return Response.status(401).build();
        }
        
        final String anneeF = annee;
        final String diplomeF = diplome;
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String message = "";
                    message = anneeF+" ("+diplomeF+") : Cette année a été supprimée par "+pseudo+".";
                    
                    int exceptIdAdmin = AdminUtils.getIdAdmin(pseudo);
                    AdminUtils.notifyMail(message, id, exceptIdAdmin);
                    AdminUtils.notifyAndroid(message, id, exceptIdAdmin);
                    AdminUtils.notifyiOS(message, id, exceptIdAdmin);
                    
                    Connection connexion = SQLUtils.getConnexion();
                    Statement stmt = connexion.createStatement();
                    stmt.executeUpdate("DELETE FROM ADMIN_FOLLOWER WHERE (ID_ANNEE = " + id + ");");
                    stmt.close();
                    connexion.close();
                    
                } catch (SQLException ex) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        }).start();
        
        return Response.status(200).build();
    }
    
    /**
     * Méthode permettant de récupérer une année complète (UE / MATIERE).
     *
     * @param idAnnee L'identifiant de l'année souhaitée.
     *
     * @return Un objet "Année" contenant l'ensemble des données présentes en
     * base pour l'année spécifiée.
     */
    @GET
    @Path("getCompleteYear")
    @Produces("application/json;charset=utf-8")
    public Annee getCompleteYear(
            @QueryParam("idAnnee")
                    String idAnnee) {
        
        Annee a = null;
        Connection connexion = null;
        
        Statement stmt1 = null;
        Statement stmt2 = null;
        Statement stmt3 = null;
        
        ResultSet results1 = null;
        ResultSet results2 = null;
        ResultSet results3 = null;
        
        try {
            connexion = SQLUtils.getConnexion();
            
            stmt1 = connexion.createStatement();
            stmt2 = connexion.createStatement();
            stmt3 = connexion.createStatement();
            
            results1 = stmt1.executeQuery("SELECT ANNEE.DECOUPAGE, ANNEE.ID, ANNEE.NOM, ANNEE.ID_ETABLISSEMENT, ANNEE.ID_DIPLOME, ANNEE.IS_LAST_YEAR, "
                    + "ETABLISSEMENT.NOM, ETABLISSEMENT.VILLE, ETABLISSEMENT.ID, "
                    + "DIPLOME.NOM "
                    + "FROM ANNEE, DIPLOME, ETABLISSEMENT WHERE ANNEE.ID=" + idAnnee + " AND ANNEE.ID_DIPLOME=DIPLOME.ID AND ANNEE.ID_ETABLISSEMENT=ETABLISSEMENT.ID;");
            
            while (results1.next()) {
                a = new Annee();
                a.setIdAnnee(results1.getInt("ANNEE.ID"));
                a.setNom(results1.getString("ANNEE.NOM"));
                a.setIdEtablissement(results1.getInt("ANNEE.ID_ETABLISSEMENT"));
                a.setIdDiplome(results1.getInt("ANNEE.ID_DIPLOME"));
                a.setIsLastYear(results1.getBoolean("ANNEE.IS_LAST_YEAR"));
                a.setDecoupage(results1.getString("ANNEE.DECOUPAGE"));
                a.setNomDiplome(results1.getString("DIPLOME.NOM"));
                
                Etablissement e = new Etablissement();
                e.setIdEtablissement(results1.getInt("ETABLISSEMENT.ID"));
                e.setVille(results1.getString("ETABLISSEMENT.VILLE"));
                e.setNom(results1.getString("ETABLISSEMENT.NOM"));
                
                a.setEtablissement(e);
                
                // Récupération des UEs pour une année
                stmt2 = connexion.createStatement();
                results2 = stmt2.executeQuery("SELECT ID, YEAR_TYPE, NOM, NOTE_MINI, NB_OPT_MINI FROM UE WHERE ID_ANNEE=" + a.getIdAnnee() + ";");
                
                while (results2.next()) {
                    UE ue = new UE();
                    ue.setIdUE(results2.getInt("ID"));
                    ue.setYearType(results2.getString("YEAR_TYPE"));
                    ue.setNom(results2.getString("NOM"));
                    ue.setNoteMini(results2.getFloat("UE.NOTE_MINI"));
                    ue.setNbOptionMini(results2.getInt("UE.NB_OPT_MINI"));
                    
                    // Récupération des matières pour une UE
                    stmt3 = connexion.createStatement();
                    results3 = stmt3.executeQuery("SELECT COEFFICIENT, ID, IS_OPTION, IS_RATTRAPABLE, NOTE_MINI, NOM FROM MATIERE WHERE ID_UE=" + ue.getIdUE() + ";");
                    
                    while (results3.next()) {
                        Matiere matiere = new Matiere();
                        matiere.setCoefficient(results3.getFloat("COEFFICIENT"));
                        matiere.setIdMatiere(results3.getInt("ID"));
                        matiere.setIsOption(results3.getBoolean("IS_OPTION"));
                        matiere.setNom(results3.getString("NOM"));
                        matiere.setIsRattrapable(results3.getBoolean("MATIERE.IS_RATTRAPABLE"));
                        matiere.setNoteMini(results3.getFloat("MATIERE.NOTE_MINI"));
                        
                        ue.addMatiere(matiere);
                    }
                    
                    results3.close();
                    stmt3.close();
                    
                    a.addUE(ue);
                }
                
                results2.close();
                stmt2.close();
            }
            
            results1.close();
            stmt1.close();
            connexion.close();
        } catch (SQLException ex) {
            Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (results1 != null) {
                try {
                    results1.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (results2 != null) {
                try {
                    results2.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (results3 != null) {
                try {
                    results3.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (stmt1 != null) {
                try {
                    stmt1.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (stmt2 != null) {
                try {
                    stmt2.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (stmt3 != null) {
                try {
                    stmt3.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (connexion != null) {
                try {
                    connexion.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return a;
    }
    
    /**
     * Méthode permettant de récupérer la liste des années en fonction d'un
     * diplôme et d'un établissement.
     *
     * @param idDiplome L'identifiant du diplôme
     * @param idEtablissement L'identifiant de l'établissement
     *
     * @return Une liste d'année de la recherche faites sur le diplôme et sur
     * l'établissement base pour l'année spécifiée.
     */
    @GET
    @Path("getAnnees")
    @Produces("application/json;charset=utf-8")
    public ArrayList<Annee> getAnnees(
            @QueryParam("idDiplome")
                    String idDiplome,
            @QueryParam("idEtablissement")
                    String idEtablissement) {
        
        ArrayList<Annee> annees = new ArrayList<>();
        Connection connexion = null;
        Statement stmt = null;
        ResultSet results = null;
        
        try {
            connexion = SQLUtils.getConnexion();
            stmt = connexion.createStatement();
            results = stmt.executeQuery("SELECT ID, NOM, ID_ETABLISSEMENT, ID_DIPLOME, IS_LAST_YEAR, DECOUPAGE FROM ANNEE WHERE ID_DIPLOME=" + idDiplome + " AND ID_ETABLISSEMENT=" + idEtablissement + ";");
            Annee a = null;
            
            while (results.next()) {
                a = new Annee();
                a.setIdAnnee(results.getInt("ID"));
                a.setNom(results.getString("NOM"));
                a.setIdEtablissement(results.getInt("ID_ETABLISSEMENT"));
                a.setIdDiplome(results.getInt("ID_DIPLOME"));
                a.setIsLastYear(results.getBoolean("IS_LAST_YEAR"));
                a.setDecoupage(results.getString("DECOUPAGE"));
                
                annees.add(a);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (connexion != null) {
                try {
                    connexion.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return annees;
    }
    
    /**
     * Méthode permettant de savoir si une année est suivie ou non par l'admin
     * spécifiée.
     *
     * @param idAnnee L'identifiant de l'année à chercher.
     * @param idAdmin L'identifiant de l'admin à chercher.
     *
     * @return <b>true</b> si l'année est suivie par l'admin spécifié.
     * <b>false</b> sinon.
     */
    private boolean isFollowed (int idAnnee, int idAdmin) {
        boolean isFollowed = false;
        
        Connection connexion = null;
        Statement stmt = null;
        ResultSet results = null;
        
        try {
            connexion = SQLUtils.getConnexion();
            stmt = connexion.createStatement();
            results = stmt.executeQuery("SELECT * "
                    + "FROM ANNEE, ADMINISTRATEUR, ADMIN_FOLLOWER "
                    + "WHERE (ANNEE.ID = ADMIN_FOLLOWER.ID_ANNEE) "
                    + "AND (ADMINISTRATEUR.ID = ADMIN_FOLLOWER.ID_ADMIN) "
                    + "AND (ADMINISTRATEUR.ID = " + idAdmin +") "
                    + "AND (ANNEE.ID = " + idAnnee + ");");
            
            while (results.next()) {
                if (results.getInt("ANNEE.ID") == idAnnee) {
                    isFollowed = true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (connexion != null) {
                try {
                    connexion.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return isFollowed;
    }
    
    /**
     * Méthode permettant de récupérer les années suivies par un admin donné.
     *
     * @param pseudo Le pseudo de l'administrateur ayant fait la demande.
     * @param token Le token envoyé par l'administrateur.
     * @param timestamp Le timestamp envoyé par l'administrateur ayant fait la
     * requête. Permet d'éviter les rejeux.
     *
     * @return 3 possibilités :
     * - Un code HTTP 200 avec les années suivies, si l'utilisateur ayant fait
     * la demande est connecté (donc autorisé).
     * - Un code HTTP 401 si c'est un utilisateur non connecté (donc non autorisé) qui a fait la demande.
     * - Un code HTTP 500 si une erreur SQL se produit.
     */
    @GET
    @Path("getFavorites")
    @Produces("application/json;charset=utf-8")
    public Response getFavorites(
            @QueryParam("pseudo")
                    String pseudo,
            @QueryParam("token")
                    String token,
            @QueryParam("timestamp")
                    long timestamp) {
        
        ArrayList<Annee> annees = new ArrayList<>();
        Connection connexion = null;
        Statement stmt = null;
        ResultSet results = null;
        
        try {
            if (AdminUtils.checkToken(pseudo, token) && AdminUtils.checkTimestamp(pseudo, timestamp)) {
                connexion = SQLUtils.getConnexion();
                stmt = connexion.createStatement();
                
                results = stmt.executeQuery("SELECT ID " +
                        "FROM ADMINISTRATEUR " +
                        "WHERE (PSEUDO = '" + pseudo + "');");
                
                int idAdmin = 0;
                
                while (results.next()) {
                    idAdmin = results.getInt("ID");
                }
                
                results.close();
                stmt.close();
                
                stmt = connexion.createStatement();
                results = stmt.executeQuery("SELECT * " +
                        "FROM DIPLOME, ANNEE, ADMIN_FOLLOWER, ADMINISTRATEUR, ETABLISSEMENT " +
                        "WHERE (DIPLOME.ID = ANNEE.ID_DIPLOME) " +
                        "AND (ADMIN_FOLLOWER.ID_ADMIN = ADMINISTRATEUR.ID) " +
                        "AND (ETABLISSEMENT.ID = ANNEE.ID_ETABLISSEMENT) " +
                        "AND (ANNEE.ID = ADMIN_FOLLOWER.ID_ANNEE) " +
                        "AND (ADMINISTRATEUR.ID = " + idAdmin + ");");
                
                Annee a = null;
                
                while (results.next()) {
                    a = new Annee();
                    a.setIdAnnee(results.getInt("ANNEE.ID"));
                    a.setNom(results.getString("ANNEE.NOM"));
                    a.setIdEtablissement(results.getInt("ID_ETABLISSEMENT"));
                    a.setIdDiplome(results.getInt("ID_DIPLOME"));
                    a.setIsLastYear(results.getBoolean("IS_LAST_YEAR"));
                    a.setDecoupage(results.getString("DECOUPAGE"));
                    a.setIsFollowed(isFollowed(a.getIdAnnee(), idAdmin));
                    
                    Etablissement e = new Etablissement();
                    e.setIdEtablissement(results.getInt("ETABLISSEMENT.ID"));
                    e.setNom(results.getString("ETABLISSEMENT.NOM"));
                    e.setVille(results.getString("VILLE"));
                    a.setEtablissement(e);
                    
                    annees.add(a);
                }
            } else {
                if (results != null) {
                    try {
                        results.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                if (connexion != null) {
                    try {
                        connexion.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                return Response.status(401).build();
            }
        } catch (SQLException ex) {
            Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (connexion != null) {
                try {
                    connexion.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return Response.status(200).entity(annees.toArray(new Annee[annees.size()])).build();
    }
    
    /**
     * Méthode retournant les années suivies d'un diplôme par un administrateur.
     *
     * @param idDiplome L'identifiant du diplôme.
     *
     * @param pseudo Le pseudo de l'administrateur ayant fait la demande.
     * @param token Le token envoyé par l'administrateur.
     * @param timestamp Le timestamp envoyé par l'administrateur ayant fait la
     * requête. Permet d'éviter les rejeux.
     *
     * @return 3 possibilités :
     * - La liste des années suivies d'un diplôme par l'admin spécifié si les
     * paramètres sont corrects.
     * - Un code HTTP 401 si c'est un utilisateur non connecté (donc non autorisé) qui a fait la demande.
     */
    @GET
    @Path("getAnneesFollowedByDiplome")
    @Produces("application/json;charset=utf-8")
    public Response getAnneesFollowedByDiplome(
            @QueryParam("idDiplome")
                    String idDiplome,
            @QueryParam("pseudo")
                    String pseudo,
            @QueryParam("token")
                    String token,
            @QueryParam("timestamp")
                    long timestamp) {
        
        ArrayList<Annee> annees = new ArrayList<>();
        Connection connexion = null;
        Statement stmt = null;
        ResultSet results = null;
        
        try {
            if (AdminUtils.checkToken(pseudo, token) && AdminUtils.checkTimestamp(pseudo, timestamp)) {
                connexion = SQLUtils.getConnexion();
                stmt = connexion.createStatement();
                
                results = stmt.executeQuery("SELECT ID " +
                        "FROM ADMINISTRATEUR " +
                        "WHERE (PSEUDO = '" + pseudo + "');");
                
                int idAdmin = 0;
                
                while (results.next()) {
                    idAdmin = results.getInt("ID");
                }
                
                results.close();
                stmt.close();
                
                stmt = connexion.createStatement();
                results = stmt.executeQuery("SELECT * "
                        + "FROM ANNEE, ETABLISSEMENT "
                        + "WHERE (ID_DIPLOME=" + idDiplome + ") "
                        + "AND (ANNEE.ID_ETABLISSEMENT = ETABLISSEMENT.ID);");
                
                Annee a = null;
                
                while (results.next()) {
                    a = new Annee();
                    a.setIdAnnee(results.getInt("ANNEE.ID"));
                    a.setNom(results.getString("ANNEE.NOM"));
                    a.setIdEtablissement(results.getInt("ID_ETABLISSEMENT"));
                    a.setIdDiplome(results.getInt("ID_DIPLOME"));
                    a.setIsLastYear(results.getBoolean("IS_LAST_YEAR"));
                    a.setDecoupage(results.getString("DECOUPAGE"));
                    a.setIsFollowed(isFollowed(a.getIdAnnee(), idAdmin));
                    
                    Etablissement e = new Etablissement();
                    e.setIdEtablissement(results.getInt("ETABLISSEMENT.ID"));
                    e.setNom(results.getString("ETABLISSEMENT.NOM"));
                    e.setVille(results.getString("VILLE"));
                    a.setEtablissement(e);
                    
                    annees.add(a);
                }
            } else {
                if (results != null) {
                    try {
                        results.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                if (connexion != null) {
                    try {
                        connexion.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                return Response.status(401).build();
            }
        } catch (SQLException ex) {
            Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (connexion != null) {
                try {
                    connexion.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return Response.status(200).entity(annees.toArray(new Annee[annees.size()])).build();
    }
    
    /**
     * Méthode retournant les années suivies d'un diplôme par un administrateur.
     *
     * @param idDiplome L'identifiant du diplôme.
     * @param idEtablissement L'identifiant de l'établissement
     * @param pseudo Le pseudo de l'administrateur ayant fait la demande.
     * @param token Le token envoyé par l'administrateur.
     * @param timestamp Le timestamp envoyé par l'administrateur ayant fait la
     * requête. Permet d'éviter les rejeux.
     *
     * @return 3 possibilités :
     * - La liste des années suivies d'un diplôme par l'admin spécifié si les
     * paramètres sont corrects.
     * - Un code HTTP 401 si c'est un utilisateur non connecté (donc non autorisé) qui a fait la demande.
     */
    @GET
    @Path("getAnneesFollowedByDE")
    @Produces("application/json;charset=utf-8")
    public Response getAnneesFollowedByDiplomeAndEtablissement(
            @QueryParam("idDiplome")
                    String idDiplome,
            @QueryParam("idEtablissement")
                    String idEtablissement,
            @QueryParam("pseudo")
                    String pseudo,
            @QueryParam("token")
                    String token,
            @QueryParam("timestamp")
                    long timestamp) {
        
        ArrayList<Annee> annees = new ArrayList<>();
        Connection connexion = null;
        Statement stmt = null;
        ResultSet results = null;
        
        try {
            if (AdminUtils.checkToken(pseudo, token) && AdminUtils.checkTimestamp(pseudo, timestamp)) {
                connexion = SQLUtils.getConnexion();
                stmt = connexion.createStatement();
                
                results = stmt.executeQuery("SELECT ID " +
                        "FROM ADMINISTRATEUR " +
                        "WHERE (PSEUDO = '" + pseudo + "');");
                
                int idAdmin = 0;
                
                while (results.next()) {
                    idAdmin = results.getInt("ID");
                }
                
                results.close();
                stmt.close();
                
                stmt = connexion.createStatement();
                results = stmt.executeQuery("SELECT * "
                        + "FROM ANNEE "
                        + "WHERE (ANNEE.ID_DIPLOME=" + idDiplome + ") "
                        + "AND (ANNEE.ID_ETABLISSEMENT = " + idEtablissement + ");");
                
                Annee a = null;
                
                while (results.next()) {
                    a = new Annee();
                    a.setIdAnnee(results.getInt("ANNEE.ID"));
                    a.setNom(results.getString("ANNEE.NOM"));
                    a.setIdEtablissement(results.getInt("ID_ETABLISSEMENT"));
                    a.setIdDiplome(results.getInt("ID_DIPLOME"));
                    a.setIsLastYear(results.getBoolean("IS_LAST_YEAR"));
                    a.setDecoupage(results.getString("DECOUPAGE"));
                    a.setIsFollowed(isFollowed(a.getIdAnnee(), idAdmin));
                    
                    annees.add(a);
                }
            } else {
                if (results != null) {
                    try {
                        results.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                if (connexion != null) {
                    try {
                        connexion.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                return Response.status(401).build();
            }
        } catch (SQLException ex) {
            Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (connexion != null) {
                try {
                    connexion.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return Response.status(200).entity(annees.toArray(new Annee[annees.size()])).build();
    }
    
    
    /**
     * Méthode permettant de récupérer la liste des années en fonction d'un
     * diplôme.
     *
     * @param idDiplome L'identifiant du diplôme.
     *
     * @return Une liste d'année de la recherche faites sur le diplôme pour
     * l'année spécifiée.
     */
    @GET
    @Path("getAnneesByDiplome")
    @Produces("application/json;charset=utf-8")
    public ArrayList<Annee> getAnneesByDiplome(
            @QueryParam("idDiplome") String idDiplome) {
        
        ArrayList<Annee> annees = new ArrayList<>();
        Connection connexion = null;
        Statement stmt = null;
        ResultSet results = null;
        
        try {
            connexion = SQLUtils.getConnexion();
            stmt = connexion.createStatement();
            results = stmt.executeQuery("SELECT * "
                    + "FROM ANNEE, ETABLISSEMENT "
                    + "WHERE (ID_DIPLOME=" + idDiplome + ") AND (ANNEE.ID_ETABLISSEMENT = ETABLISSEMENT.ID);");
            
            Annee a = null;
            
            while (results.next()) {
                a = new Annee();
                a.setIdAnnee(results.getInt("ANNEE.ID"));
                a.setNom(results.getString("ANNEE.NOM"));
                a.setIdEtablissement(results.getInt("ID_ETABLISSEMENT"));
                a.setIdDiplome(results.getInt("ID_DIPLOME"));
                a.setIsLastYear(results.getBoolean("IS_LAST_YEAR"));
                a.setDecoupage(results.getString("DECOUPAGE"));
                
                Etablissement e = new Etablissement();
                e.setIdEtablissement(results.getInt("ETABLISSEMENT.ID"));
                e.setNom(results.getString("ETABLISSEMENT.NOM"));
                e.setVille(results.getString("VILLE"));
                a.setEtablissement(e);
                
                annees.add(a);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (connexion != null) {
                try {
                    connexion.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return annees;
    }
}
