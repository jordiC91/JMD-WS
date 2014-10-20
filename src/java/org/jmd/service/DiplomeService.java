package org.jmd.service;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.*;
import javax.annotation.PreDestroy;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.jmd.AdminUtils;
import org.jmd.SQLUtils;
import org.jmd.metier.Diplome;

/**
 * Service web gérant les diplômes (création / suppression / recherche / ...).
 *
 * @author jordi charpentier - yoann vanhoeserlande
 */
@Path("diplome")
public class DiplomeService {
    
    /**
     * Objet représentant une connexion à la base de données de 
     * l'application.
     */
    private Connection connexion;
    
    /**
     * Constructeur par défaut de la classe.
     */
    public DiplomeService() {
        
    }
    
    /**
     * Méthode permettant de créer un diplôme.
     * 
     * @param nom Le nom du diplôme à créer.
     * 
     * @param pseudo Le pseudo de l'utilisateur.
     * @param token Le token de l'utilisateur.
     * @param timestamp Le timestamp de l'utilisateur.
     * 
     * @return 4 possibilités :
     * - Un code HTTP 200 si l'utilisateur ayant fait la demande de création est
     * connecté (donc autorisé).
     * - Un code HTTP 401 si c'est un utilisateur non connecté (donc non autorisé)
     * qui a fait la demande.
     * - Un code HTTP 403 si le diplôme à créer existe déjà en base.
     * - Un code HTTP 500 si une erreur SQL se produit.
     */
    @PUT
    public Response insertDiplome(
            @QueryParam("nom")
                    String nom,
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
                stmt.execute("INSERT INTO DIPLOME (NOM) VALUES ('" + nom + "')");
                stmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);
                
                if (ex instanceof MySQLIntegrityConstraintViolationException){
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
     * Méthode permettant de supprimer un diplôme.
     * 
     * @param id L'identifiant du diplôme à supprimer.
     * 
     * @param pseudo Le pseudo de l'utilisateur.
     * @param token Le token de l'utilisateur.
     * @param timestamp Le timestamp de l'utilisateur.
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
        
        if (AdminUtils.checkToken(pseudo, token) && AdminUtils.checkTimestamp(pseudo, timestamp)) {
            if (connexion == null) {
                connexion = SQLUtils.getConnexion();
            }
            
            try {
                try (Statement stmt = connexion.createStatement()) {
                    ResultSet results = stmt.executeQuery("SELECT * FROM ANNEE WHERE (ID_DIPLOME = " + id + ")");
                    ResultSet results2 = null;
                    ResultSet results3 = null;
                    
                    ArrayList<Integer> idAnneeList = new ArrayList<>();
                    ArrayList<Integer> idUEList = new ArrayList<>();
                    ArrayList<Integer> idMatiereList = new ArrayList<>();
                    
                    boolean hasResults = false;
                    
                    while (results.next()) {
                        hasResults = true;
                        idAnneeList.add(results.getInt("ID"));
                        
                        results2 = stmt.executeQuery("SELECT * FROM UE WHERE (ID_ANNEE = " + results.getInt("ID") + ")");
                        
                        while (results2.next()) {
                            idUEList.add(results.getInt("ID"));
                        
                            results3 = stmt.executeQuery("SELECT * FROM MATIERE WHERE (ID_UE = " + results.getInt("ID") + ")");
                            
                            while (results3.next()) {
                                idMatiereList.add(results.getInt("ID"));
                            }
                        }
                    }
                    
                    if (results3 != null) {
                        results3.close();
                    }
                    
                    if (results2 != null) {
                        results2.close();
                    }
                    
                    results.close();
                    
                    // S'il n'y a pas de résultats pour l'id diplôme spécifié.
                    if (!hasResults) {
                        return Response.status(404).entity("ID_NOT_FOUND").build();
                    }
                    
                    // Suppression des matières du diplôme.
                    for (Integer idMatiereList1 : idMatiereList) {
                        stmt.executeUpdate("DELETE FROM MATIERE WHERE (ID = " + idMatiereList1 + ")");
                    }
                    
                    // Suppression des UE du diplôme.
                    for (Integer idUEList1 : idUEList) {
                        stmt.executeUpdate("DELETE FROM UE WHERE (ID = " + idUEList1 + ")");
                    }
                    
                    // Suppression des années du diplôme.
                    for (Integer idAnneeList1 : idAnneeList) {
                        stmt.executeUpdate("DELETE FROM ANNEE WHERE (ID = " + idAnneeList1 + ")");
                    }
                    
                    // Suppression du diplôme.
                    stmt.executeUpdate("DELETE FROM DIPLOME WHERE (ID = "+id+")");
                    
                    stmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);
                
                return Response.status(500).build();
            }
            
            return Response.status(200).build();
        } else {
            return Response.status(401).build();
        }
    }
    
    /**
     * Méthode permettant de récupérer tout les diplômes présents en base.
     * 
     * @return Une liste (<i>ArrayList</i>) comprenant l'ensemble des diplômes
     * présents en base.
     */
    @GET
    @Path("getAll")
    @Produces("application/json")
    public ArrayList<Diplome> getAll() {
        
        ArrayList<Diplome> diplomes = null;
        
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        try {
            Statement stmt = connexion.createStatement();
            ResultSet results = stmt.executeQuery("SELECT * FROM diplome ORDER BY id ASC");
            diplomes = new ArrayList<>();
            Diplome d = null;
            
            while (results.next()) {
                d = new Diplome();
                d.setIdDiplome(results.getInt("ID"));
                d.setNom(results.getString("NOM"));
                diplomes.add(d);
            }
            
            results.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return diplomes;
    }
    
    /**
     * Méthode permettant de chercher un diplôme à partir de son nom.
     * 
     * @param nom Le nom du diplôme à chercher.
     * 
     * @return Une liste (<i>ArrayList</i>) de diplômes dont le nom "est comme"
     * celui recherché.
     * 
     * @see Opérateur LIKE en SQL.
     */
    @GET
    @Path("search")
    public ArrayList<Diplome> search(
            @QueryParam ("nom") 
                    String nom){
        
        ArrayList<Diplome> diplomes = null;
        
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        try {
            Statement stmt = connexion.createStatement();
            ResultSet results = stmt.executeQuery("SELECT * FROM diplome WHERE NOM LIKE '%"+nom+"%' ORDER BY id ASC");
            diplomes = new ArrayList<>();
            Diplome d = null;
            
            while (results.next()) {
                d = new Diplome();
                d.setIdDiplome(results.getInt("ID"));
                d.setNom(results.getString("NOM"));
                diplomes.add(d);
            }
            
            results.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return diplomes;
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