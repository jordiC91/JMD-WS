/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jmd.metier;

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
    
    
}
