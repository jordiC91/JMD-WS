package org.jmd.metier;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Classe représentant un diplôme.
 * 
 * @author jordi charpentier - yoann vanhoeserlande
 */
@XmlRootElement
public class Diplome {
    
    /**
     * L'identifiant du diplôme.
     */
    private int idDiplome;
    
    /**
     * Le nom du diplôme.
     */
    private String nom;
    
    /**
     * Constructeur par défaut de la classe.
     */
    public Diplome() {
    
    }
    
    /* Getters. */

    /**
     * Méthode retournant l'identifiant du diplôme.
     * 
     * @return L'identifiant du diplôme.
     */
    public int getIdDiplome() {
        return idDiplome;
    }

    /**
     * Méthode retournant le nom du diplôme.
     * 
     * @return Le nom du diplôme. 
     */
    public String getNom() {
        return nom;
    }
    
    /* Setters. */

    /**
     * Méthode permettant de modifier l'identifiant du diplôme.
     * 
     * @param idDiplome Le nouvel identifiant du diplôme.
     */
    public void setIdDiplome(int idDiplome) {
        this.idDiplome = idDiplome;
    }

    /**
     * Méthode permettant de modifier le nom du diplôme.
     * 
     * @param nom Le nouveau nom du diplôme.
     */
    public void setNom(String nom) {
        this.nom = nom;
    } 
}