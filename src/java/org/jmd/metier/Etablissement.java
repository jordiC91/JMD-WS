package org.jmd.metier;

import javax.xml.bind.annotation.XmlElement;
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
    @XmlElement(name="idEtablissement")
    private int idEtablissement;
    
    /**
     * Le nom de l'établissement.
     */
    @XmlElement(name="nom")
    private String nom = "";
    
    /**
     * La ville de l'établissement.
     */
    @XmlElement(name="ville")
    private String ville = "";
    
    /**
     * Constructeur par défaut de la classe.
     */
    public Etablissement() {
        
    }
    
    /* Getters. */
    
    /**
     * Méthode retournant le nom de l'établissement.
     *
     * @return Le nom de l'établissement.
     */
    public String getNom() {
        return this.nom;
    }
    
    /**
     * Méthode retournant la ville de l'établissement.
     *
     * @return La ville de l'établissement.
     */
    public String getVille() {
        return this.ville;
    }
    
    /**
     * Méthode retournant l'identifiant de l'établissement.
     *
     * @return L'identifiant de l'établissement.
     */
    public int getIdEtablissement() {
        return this.idEtablissement;
    }
    
    /* Setters. */
    
    /**
     * Méthode permettant de modifier le nom de l'établissement.
     *
     * @param nom Le nouveau nom de l'établissement.
     */
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    /**
     * Méthode permettant de modifier la ville de l'établissement.
     *
     * @param ville La nouvelle ville de l'établissement.
     */
    public void setVille(String ville) {
        this.ville = ville;
    }
    
    /**
     * Méthode permettant de modifier l'identifiant de l'établissement.
     *
     * @param idEtablissement Le nouvel identifiant de l'établissement.
     */
    public void setIdEtablissement(int idEtablissement) {
        this.idEtablissement = idEtablissement;
    }
}