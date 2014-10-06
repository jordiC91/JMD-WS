/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jmd.metier;

/**
 *
 * @author yoyito
 */
public class Annee {
    private int idAnnee;
    private String nom;
    private String decoupage;
    private boolean isLastYear;
    private int idEtablissement;
    private int idDiplome;

    public Annee () {}
    
    /**
     * @return the idAnnee
     */
    public int getIdAnnee() {
        return idAnnee;
    }

    /**
     * @param idAnnee the idAnnee to set
     */
    public void setIdAnnee(int idAnnee) {
        this.idAnnee = idAnnee;
    }

    /**
     * @return the nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * @param nom the nom to set
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * @return the decoupage
     */
    public String getDecoupage() {
        return decoupage;
    }

    /**
     * @param decoupage the decoupage to set
     */
    public void setDecoupage(String decoupage) {
        this.decoupage = decoupage;
    }

    /**
     * @return the isLastYear
     */
    public boolean isIsLastYear() {
        return isLastYear;
    }

    /**
     * @param isLastYear the isLastYear to set
     */
    public void setIsLastYear(boolean isLastYear) {
        this.isLastYear = isLastYear;
    }

    /**
     * @return the idEtablissement
     */
    public int getIdEtablissement() {
        return idEtablissement;
    }

    /**
     * @param idEtablissement the idEtablissement to set
     */
    public void setIdEtablissement(int idEtablissement) {
        this.idEtablissement = idEtablissement;
    }

    /**
     * @return the idDiplome
     */
    public int getIdDiplome() {
        return idDiplome;
    }

    /**
     * @param idDiplome the idDiplome to set
     */
    public void setIdDiplome(int idDiplome) {
        this.idDiplome = idDiplome;
    }
    
    
}
