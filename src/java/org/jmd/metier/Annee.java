/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jmd.metier;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author yoyito
 */
@XmlRootElement
public class Annee {
    private int idAnnee;
    @XmlElement(name="nom")
    private String nom;
    @XmlElement(name="decoupage")
    private String decoupage;
    private boolean isLastYear;
    private int idEtablissement;
    private int idDiplome;
    private String nomEtablissement;
    private String nomDiplome;
    @XmlElement(name="ues")
    private ArrayList<UE> ues;

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
    
    
    
    /**
     * @param ue 
     * @return True si l'UE a bien été ajouté
     */
    public boolean addUE(UE ue) {
        if(this.ues == null)
            this.ues = new ArrayList<>();
        
        return this.ues.add(ue);
    }    

    /**
     * @return the nomEtablissement
     */
    public String getNomEtablissement() {
        return nomEtablissement;
    }

    /**
     * @return the nomDiplome
     */
    public String getNomDiplome() {
        return nomDiplome;
    }

    /**
     * @param nomEtablissement the nomEtablissement to set
     */
    public void setNomEtablissement(String nomEtablissement) {
        this.nomEtablissement = nomEtablissement;
    }

    /**
     * @param nomDiplome the nomDiplome to set
     */
    public void setNomDiplome(String nomDiplome) {
        this.nomDiplome = nomDiplome;
    }
    
}
