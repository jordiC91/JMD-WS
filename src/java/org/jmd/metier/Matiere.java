package org.jmd.metier;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Classe représentant une matière.
 * 
 * @author jordi charpentier - yoann vanhoeserlande
 */
@XmlRootElement
public class Matiere {
    
    /**
     * Identifiant de la matière.
     */
    private int idMatiere;
    
    /**
     * Le nom de la matière.
     */
    private String nom;
    
    /**
     * Le coefficient de la matière.
     */
    private float coefficient;
    
    /**
     * Booléen permettant de savoir si la matière est une option ou non.
     */
    private boolean isOption;
    
    /**
     * Constructeur par défaut de la classe.
     */
    public Matiere() {
    
    }
    
    /* Getters. */

    public int getIdMatiere() {
        return idMatiere;
    }
    
    public String getNom() {
        return nom;
    }

    public float getCoefficient() {
        return coefficient;
    }
    
    public boolean isIsOption() {
        return isOption;
    }
    
    /* Setters. */

    public void setIdMatiere(int idMatiere) {
        this.idMatiere = idMatiere;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setCoefficient(float coefficient) {
        this.coefficient = coefficient;
    }

    public void setIsOption(boolean isOption) {
        this.isOption = isOption;
    }   
}