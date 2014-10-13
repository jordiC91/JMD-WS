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

    public int getIdDiplome() {
        return idDiplome;
    }

    public String getNom() {
        return nom;
    }
    
    /* Setters. */

    public void setIdDiplome(int idDiplome) {
        this.idDiplome = idDiplome;
    }

    public void setNom(String nom) {
        this.nom = nom;
    } 
}