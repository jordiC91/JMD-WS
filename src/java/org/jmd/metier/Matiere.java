package org.jmd.metier;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author yoyito
 */
@XmlRootElement
public class Matiere {
    
    /*
    ID INTEGER PRIMARY KEY AUTO_INCREMENT,
    NOM VARCHAR(100),
    COEFFICIENT INTEGER
    */
    
    private int idMatiere;
    private String nom;
    private float coefficient;
    private boolean isOption;
    
    public Matiere() {}

    /**
     * @return the idMatiere
     */
    public int getIdMatiere() {
        return idMatiere;
    }

    /**
     * @param idMatiere the idMatiere to set
     */
    public void setIdMatiere(int idMatiere) {
        this.idMatiere = idMatiere;
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
     * @return the coefficient
     */
    public float getCoefficient() {
        return coefficient;
    }

    /**
     * @param coefficient the coefficient to set
     */
    public void setCoefficient(float coefficient) {
        this.coefficient = coefficient;
    }

    /**
     * @return the isOption
     */
    public boolean isIsOption() {
        return isOption;
    }

    /**
     * @param isOption the isOption to set
     */
    public void setIsOption(boolean isOption) {
        this.isOption = isOption;
    }
    
    
    
    
}
