package org.jmd.metier;

import javax.xml.bind.annotation.XmlElement;
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
    @XmlElement(name="idMatiere")
    private int idMatiere;
    
    /**
     * Le nom de la matière.
     */
    @XmlElement(name="nom")
    private String nom;
    
    /**
     * Le coefficient de la matière.
     */
    @XmlElement(name="coefficient")
    private float coefficient;
    
    /**
     * Booléen permettant de savoir si la matière est une option ou non.
     */
    @XmlElement(name="isOption")
    private boolean isOption;
    
    @XmlElement(name="isRattrapable")
    private boolean isRattrapable;
    
    @XmlElement(name="noteMini")
    private float noteMini;
    
    /**
     * Constructeur par défaut de la classe.
     */
    public Matiere() {
        
    }
    
    /* Getters. */
    
    /**
     * Méthode retournant l'identifiant de la matière.
     *
     * @return L'identifiant de la matière.
     */
    public int getIdMatiere() {
        return this.idMatiere;
    }
    
    /**
     * Méthode retournant le nom de la matière.
     *
     * @return Le nom de la matière.
     */
    public String getNom() {
        return this.nom;
    }
    
    /**
     * Méthode retournant le coefficient de la matière.
     *
     * @return Le coefficient de la matière.
     */
    public float getCoefficient() {
        return this.coefficient;
    }
    
    /**
     * Méthode permettant de savoir si la matière est une option, ou non.
     *
     * @return <b>true</b> si la matière est une option.
     * <b>false</b> sinon.
     */
    public boolean isOption() {
        return this.isOption;
    }
    
    public boolean isRattrapable() {
        return this.isRattrapable;
    }
    
    public float getNoteMini() {
        return this.noteMini;
    }
    
    /* Setters. */
    
    /**
     * Méthode permettant de modifier l'identifiant de la matière.
     *
     * @param idMatiere Le nouvel identifiant de la matière.
     */
    public void setIdMatiere(int idMatiere) {
        this.idMatiere = idMatiere;
    }
    
    /**
     * Méthode permettant de modifier le nom de la matière.
     *
     * @param nom Le nouveau nom de la matière.
     */
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    /**
     * Méthode permettant de modifier le coefficient de la matière.
     *
     * @param coefficient Le nouveau coefficient de la matière.
     */
    public void setCoefficient(float coefficient) {
        this.coefficient = coefficient;
    }
    
    /**
     * Méthode permettant de modifier que la matière est une option, ou non.
     *
     * @param isOption Le nouveau booléen identifiant le fait que la matière soit
     * une option, ou non.
     */
    public void setIsOption(boolean isOption) {
        this.isOption = isOption;
    }
    
    public void setIsRattrapable(boolean isRattrapable) {
        this.isRattrapable = isRattrapable;
    }
    
    public void setNoteMini(float noteMini) {
        this.noteMini = noteMini;
    }
}