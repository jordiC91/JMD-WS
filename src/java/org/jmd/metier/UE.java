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
public class UE {
    private int idUE;
    private String nom;
    private String yearType;
    @XmlElement
    private ArrayList<Matiere> matieres;

    public UE () {}
    
    /**
     * @return the idUE
     */
    public int getIdUE() {
        return idUE;
    }

    /**
     * @param idUE the idUE to set
     */
    public void setIdUE(int idUE) {
        this.idUE = idUE;
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
     * @return the yearType
     */
    public String getYearType() {
        return yearType;
    }

    /**
     * @param yearType the yearType to set
     */
    public void setYearType(String yearType) {
        this.yearType = yearType;
    }
    
    /**
     * @param matiere
     * @return True si l'ajout c'est bien déroulé
     */
    public boolean addMatiere(Matiere matiere){
        if(this.matieres == null)
            this.matieres = new ArrayList<>();
        
        return this.matieres.add(matiere);
    }
    
}
