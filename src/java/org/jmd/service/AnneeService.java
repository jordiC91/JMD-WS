/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package org.jmd.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.jmd.SQLUtils;
import org.jmd.metier.Annee;
import org.jmd.metier.Matiere;
import org.jmd.metier.UE;

/**
 *
 * @author yoyito
 */
@Path("annee")
public class AnneeService {
    private Connection connexion;
    
    public AnneeService() {}
    
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
            @Context
                    HttpServletRequest request,
            @Context
                    ServletContext sContext) {
        
        if (sContext.getAttribute("pseudo") != null) {
            if (connexion == null) {
                connexion = SQLUtils.getConnexion();
            }
            
            try {
                Statement stmt = connexion.createStatement();
                stmt.execute("INSERT INTO ANNEE (NOM,DECOUPAGE,IS_LAST_YEAR,ID_ETABLISSEMENT,ID_DIPLOME) VALUES ('" + nom + "','" + decoupage + "','" + isLastYear + "','" + idEtablissement + "','" + idDiplome + "')");
                stmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);
                
                return Response.status(500).build();
            }
            
            return Response.status(200).build();
        } else {
            return Response.status(401).build();
        }
    }
    
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
                    stmt.executeUpdate("DELETE FROM DIPLOME WHERE (ID = "+id+")");
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
    
    @GET
    @Path("getAll")
    @Produces("application/json")
    public ArrayList<Annee> getAll() {
        
        ArrayList<Annee> annees = null;
        
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        try {
            Statement stmt = connexion.createStatement();
            ResultSet results = stmt.executeQuery("SELECT * FROM diplome ORDER BY id ASC");
            annees = new ArrayList<>();
            Annee a = null;
            
            while (results.next()) {
                a = new Annee();
                a.setIdDiplome(results.getInt("ID"));
                a.setNom(results.getString("NOM"));
                annees.add(a);
            }
            
            results.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return annees;
        //return Response.status(200).entity(diplomes.toArray(new Diplome[diplomes.size()])).build();
    }
    
    @GET
    @Path("getCompleteYear")
    @Produces("application/json")
    public Annee getCompleteYear (
            @QueryParam("idAnnee")
                    String idAnnee) {
        Annee a = null;
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        try {
            Statement stmt = connexion.createStatement();
            ResultSet results1 = stmt.executeQuery("SELECT * FROM ANNEE, DIPLOME, ETABLISSEMENT WHERE ANNEE.ID="+idAnnee+" AND ANNEE.ID_DIPLOME=DIPLOME.ID AND ANNEE.ID_ETABLISSEMENT=ETABLISSEMENT.ID;");
            ResultSet results2;
            ResultSet results3;
            UE ue = null;
            Matiere matiere = null;
            
            while (results1.next()) {
                a = new Annee();
                a.setIdAnnee(results1.getInt("ANNEE.ID"));
                a.setNom(results1.getString("ANNEE.NOM"));
                a.setIdEtablissement(results1.getInt("ANNEE.ID_ETABLISSEMENT"));
                a.setIdDiplome(results1.getInt("ANNEE.ID_DIPLOME"));
                a.setIsLastYear(true);
                a.setNomEtablissement(results1.getString("ETABLISSEMENT.NOM"));
                a.setNomDiplome(results1.getString("DIPLOME.NOM"));
                
                 
               // Récupération des UEs pour une année
                results2 = connexion.createStatement().executeQuery("SELECT * FROM UE WHERE ID_ANN="+a.getIdAnnee()+";");
                System.out.print("Query2 : SELECT * FROM UE WHERE ID_ANN="+a.getIdAnnee()+";");
                while(results2.next()){
                    ue = new UE();
                    ue.setIdUE(results2.getInt("ID"));
                    ue.setYearType(results2.getString("YEAR_TYPE"));
                    ue.setNom(results2.getString("NOM"));
                    
                    // Récupération des matières pour une UE
                    results3 = connexion.createStatement().executeQuery("SELECT * FROM MATIERE WHERE ID_UE="+ue.getIdUE()+";");
                    System.out.print("SELECT * FROM MATIERE WHERE ID_UE="+ue.getIdUE()+";");
                    while(results3.next()){
                        matiere = new Matiere();
                        matiere.setCoefficient(results3.getFloat("COEFFICIENT"));
                        matiere.setIdMatiere(results3.getInt("ID"));
                        matiere.setIsOption(results3.getBoolean("IS_OPTION"));
                        matiere.setNom(results3.getString("NOM"));
                        ue.addMatiere(matiere);
                    }
                    results3.close();
                    a.addUE(ue);
                            
                }
                results2.close();
                
            }
            
            results1.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return a;
        //return Response.status(200).entity(diplomes.toArray(new Diplome[diplomes.size()])).build();
        
    }
    
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