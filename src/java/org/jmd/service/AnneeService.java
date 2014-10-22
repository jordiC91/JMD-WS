package org.jmd.service;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.*;
import javax.annotation.PreDestroy;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.jmd.utils.AdminUtils;
import org.jmd.utils.SQLUtils;
import org.jmd.metier.*;

/**
 * Service web gérant les années (création / suppression / recherche / ...).
 *
 * @author jordi charpentier - yoann vanhoeserlande
 */
@Path("annee")
public class AnneeService {
    
    /**
     * Objet représentant une connexion à la base de données de
     * l'application.
     */
    private Connection connexion;
    
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
     * @param timestamp Le timestamp envoyé par l'administrateur ayant fait la requête.
     * Permet d'éviter les rejeux.
     *
     * @return 4 possibilités :
     * - Un code HTTP 200 si l'utilisateur ayant fait la demande de création est
     * connecté (donc autorisé).
     * - Un code HTTP 401 si c'est un utilisateur non connecté (donc non autorisé)
     * qui a fait la demande.
     * - Un code HTTP 403 si l'année à créer existe déjà en base.
     * - Un code HTTP 500 si une erreur SQL se produit.
     */
    @PUT
    public Response creer(
            @QueryParam("nom")
                    String nom,
            @QueryParam("decoupage")
                    String decoupage,
            @QueryParam("isLastYear")
                    String isLastYear,
            @QueryParam("idEtablissement")
                    String idEtablissement,
            @QueryParam("idDiplome")
                    String idDiplome,
            @QueryParam("pseudo")
                    String pseudo,
            @QueryParam("token")
                    String token,
            @QueryParam("timestamp")
                    long timestamp) {
        
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        if (AdminUtils.checkToken(pseudo, token) && AdminUtils.checkTimestamp(pseudo, timestamp)) {
            try {
                Statement stmt = connexion.createStatement();
                stmt.execute("INSERT INTO ANNEE (NOM,DECOUPAGE,IS_LAST_YEAR,ID_ETABLISSEMENT,ID_DIPLOME) VALUES ('" + nom + "','" + decoupage + "','" + isLastYear + "','" + idEtablissement + "','" + idDiplome + "');");
                stmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);
                
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
    
    /**
     * Méthode permettant de supprimer une année.
     *
     * @param id L'identifiant de l'année à supprimer.
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
     * - Un code HTTP 500 si une erreur SQL se produit.
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
        
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        if (AdminUtils.checkToken(pseudo, token) && AdminUtils.checkTimestamp(pseudo, timestamp)) {
            try {
                try (Statement stmt = connexion.createStatement()) {
                    stmt.executeUpdate("DELETE FROM ANNEE WHERE (ID = "+id+");");
                    stmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(AnneeService.class.getName()).log(Level.SEVERE, null, ex);
                
                return Response.status(500).build();
            }
            return Response.status(200).build();
        } else {
            return Response.status(401).build();
        }
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
    public Annee getCompleteYear (
            @QueryParam("idAnnee")
                    String idAnnee) {
        
        Annee a = null;
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        try {
            Statement stmt1 = connexion.createStatement();
            Statement stmt2 = connexion.createStatement();
            Statement stmt3 = connexion.createStatement();
            ResultSet results1 = stmt1.executeQuery("SELECT ANNEE.ID, ANNEE.NOM, ANNEE.ID_ETABLISSEMENT, ANNEE.ID_DIPLOME, ANNEE.IS_LAST_YEAR, ETABLISSEMENT.NOM, DIPLOME.NOM FROM ANNEE, DIPLOME, ETABLISSEMENT WHERE ANNEE.ID="+idAnnee+" AND ANNEE.ID_DIPLOME=DIPLOME.ID AND ANNEE.ID_ETABLISSEMENT=ETABLISSEMENT.ID;");
            ResultSet results2;
            ResultSet results3;
            
            while (results1.next()) {
                a = new Annee();
                a.setIdAnnee(results1.getInt("ANNEE.ID"));
                a.setNom(results1.getString("ANNEE.NOM"));
                a.setIdEtablissement(results1.getInt("ANNEE.ID_ETABLISSEMENT"));
                a.setIdDiplome(results1.getInt("ANNEE.ID_DIPLOME"));
                a.setIsLastYear(results1.getBoolean("ANNEE.IS_LAST_YEAR"));
                a.setNomEtablissement(results1.getString("ETABLISSEMENT.NOM"));
                a.setNomDiplome(results1.getString("DIPLOME.NOM"));
                
                // Récupération des UEs pour une année
                stmt2 = connexion.createStatement();
                results2 = stmt2.executeQuery("SELECT ID, YEAR_TYPE, NOM FROM UE WHERE ID_ANNEE="+a.getIdAnnee()+";");
                
                while(results2.next()){
                    UE ue = new UE();
                    ue.setIdUE(results2.getInt("ID"));
                    ue.setYearType(results2.getString("YEAR_TYPE"));
                    ue.setNom(results2.getString("NOM"));
                    
                    // Récupération des matières pour une UE
                    stmt3 = connexion.createStatement();
                    results3 = stmt3.executeQuery("SELECT COEFFICIENT, ID, IS_OPTION, NOM FROM MATIERE WHERE ID_UE="+ue.getIdUE()+";");
                    
                    while(results3.next()){
                        Matiere matiere = new Matiere();
                        matiere.setCoefficient(results3.getFloat("COEFFICIENT"));
                        matiere.setIdMatiere(results3.getInt("ID"));
                        matiere.setIsOption(results3.getBoolean("IS_OPTION"));
                        matiere.setNom(results3.getString("NOM"));
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
        } catch (SQLException ex) {
            Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return a;
    }
    
    /**
     * Méthode permettant de récupérer la liste des années en fonction d'un diplôme et d'un établissement.
     *
     * @param idDiplome L'identifiant du diplôme
     * @param idEtablissement L'identifiant de l'établissement
     *
     * @return Une liste d'année de la recherche faites sur le diplôme et sur l'établissement
     * base pour l'année spécifiée.
     */
    @GET
    @Path("getAnnees")
    @Produces("application/json;charset=utf-8")
    public ArrayList<Annee> getAnnees (
            @QueryParam("idDiplome")
                    String idDiplome,
            @QueryParam("idEtablissement")
                    String idEtablissement) {
        
        ArrayList<Annee> annees = new ArrayList<>();
        
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        try {
            Statement stmt = connexion.createStatement();
            ResultSet results1 = stmt.executeQuery("SELECT ID, NOM, ID_ETABLISSEMENT, ID_DIPLOME, IS_LAST_YEAR FROM ANNEE WHERE ID_DIPLOME="+idDiplome+" AND ID_ETABLISSEMENT="+idEtablissement+";");
            Annee a = null;
            
            while (results1.next()) {
                a = new Annee();
                a.setIdAnnee(results1.getInt("ID"));
                a.setNom(results1.getString("NOM"));
                a.setIdEtablissement(results1.getInt("ID_ETABLISSEMENT"));
                a.setIdDiplome(results1.getInt("ID_DIPLOME"));
                a.setIsLastYear(results1.getBoolean("IS_LAST_YEAR"));
                annees.add(a);
            }
            
            results1.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return annees;
    }
    
    /**
     * Méthode permettant de récupérer la liste des années en fonction d'un diplôme.
     *
     * @param idDiplome L'identifiant du diplôme.
     *
     * @return Une liste d'année de la recherche faites sur le diplôme pour l'année spécifiée.
     */
    @GET
    @Path("getAnneesByDiplome")
    @Produces("application/json;charset=utf-8")
    public ArrayList<Annee> getAnneesByDiplome (
            @QueryParam("idDiplome")
                    String idDiplome) {
        
        ArrayList<Annee> annees = new ArrayList<>();
        
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        try {
            Statement stmt = connexion.createStatement();
            ResultSet results = stmt.executeQuery("SELECT * "
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
            
            results.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return annees;
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
                Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}