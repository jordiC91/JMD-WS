package org.jmd.metier;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
    
    public int getIdUE() {
        return idUE;
    }
    
    public String getNom() {
        return nom;
    }
    
    public String getYearType() {
        return yearType;
    }
    
    /* Setters. */

    public void setIdUE(int idUE) {
        this.idUE = idUE;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setYearType(String yearType) {
        this.yearType = yearType;
    }
}