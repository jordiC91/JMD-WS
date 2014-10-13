package org.jmd.metier;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Classe représentant un établissement.
 * 
 * @author jordi charpentier - yoann vanhoeserlande
 */
@XmlRootElement
public class Etablissement {
    
    /**
     * L'identifiant de l'établissement.
     */
    private int idEtablissement;
    
    /**
     * Le nom de l'établissement.
     */
    private String nom = "";
    
    /**
     * La ville de l'établissement.
     */
    private String ville = "";
    
    /**
     * Constructeur par défaut de la classe.
     */
    public Etablissement() {
    
    }
    
    /* Getters. */

    public String getNom() {
        return nom;
    }
    
    public String getVille() {
        return ville;
    }
    
    public int getIdEtablissement() {
        return idEtablissement;
    }
    
    /* Setters. */

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public void setIdEtablissement(int idEtablissement) {
        this.idEtablissement = idEtablissement;
    }
}