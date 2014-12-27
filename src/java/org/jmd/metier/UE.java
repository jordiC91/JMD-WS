package org.jmd.metier;

import java.util.ArrayList;
import javax.xml.bind.annotation.*;

/**
 * Classe représentant une UE.
 * 
 * @author jordi charpentier - yoann vanhoeserlande
 */
@XmlRootElement
public class UE {
    
    /**
     * L'identifiant de l'UE.
     */
    private int idUE;
    
    /**
     * Le nom de l'UE.
     */
    private String nom;
    
    /**
     * Le type de l'UE (SEMESTRE 1/2, TRIMESTRE 1/2, ...).
     */
    private String yearType;
    
    /**
     * L'identifiant de l'année à laquelle est rattachée l'UE.
     */
    private int idAnnee;
    
    /**
     * La liste des matières de l'UE.
     */
    @XmlElement
    private ArrayList<Matiere> matieres;

    /**
     * Constructeur par défaut de la classe.
     */
    public UE () {
    
    }
    
    /**
     * Méthode permettant d'ajouter une matière à l'UE.
     * 
     * @param matiere La matière à ajouter.
     * 
     * @return <b>true</b> si l'ajout s'est bien déroulé.
     * <b>false</b> sinon.
     */
    public boolean addMatiere(Matiere matiere){
        if(this.matieres == null) {
            this.matieres = new ArrayList<>();
        }
        
        return this.matieres.add(matiere);
    }
    
    /* Getters. */
    
    /**
     * Méthode retournant l'identifiant de l'UE.
     * 
     * @return L'identifiant de l'UE.
     */
    public int getIdUE() {
        return this.idUE;
    }
    
    /**
     * Méthode retournant le nom de l'UE.
     * 
     * @return Le nom de l'UE. 
     */
    public String getNom() {
        return this.nom;
    }
    
    /**
     * Méthode retournant le type de l'UE.
     * 
     * @return Le type de l'UE (SEMESTRE 1/2, TRIMESTRE 1/2, ...).
     */
    public String getYearType() {
        return this.yearType;
    }
    
    /**
     * Méthode retournant l'identifiant de l'année à laquelle l'UE est rattachée.
     * 
     * @return L'identifiant de l'année à laquelle est rattachée l'UE.
     */
    public int getIdAnnee() {
        return this.idAnnee;
    }
    
    /* Setters. */

    /**
     * Méthode permettant de modifier l'identifiant de l'UE.
     * 
     * @param idUE Le nouvel identifiant de l'UE.
     */
    public void setIdUE(int idUE) {
        this.idUE = idUE;
    }
    
    /**
     * Méthode permettant de modifier le nom de l'UE.
     * 
     * @param nom Le nouveau nom de l'UE.
     */
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    /**
     * Méthode permettant de modifier le type de l'UE.
     * 
     * @param yearType Le nouveau type de l'UE.
     */
    public void setYearType(String yearType) {
        this.yearType = yearType;
    }
    
    /**
     * Méthode permettant de modifier l'identifiant de l'année.
     * 
     * @param idAnnee Le nouvel identifiant de l'année.
     */
    public void setIdAnnee(int idAnnee) {
        this.idAnnee = idAnnee;
    }
}