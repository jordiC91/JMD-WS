package org.jmd.metier;

import java.util.ArrayList;
import javax.xml.bind.annotation.*;

/**
 * Classe représentant une année.
 * 
 * @author jordi charpentier - yoann vanhoeserlande
 */
@XmlRootElement
public class Annee {
    
    // En base. 
    
    /**
     * L'identifiant de l'année.
     */
    private int idAnnee;
    
    /**
     * Le nom de l'année.
     */
    @XmlElement(name="nom")
    private String nom;
    
    /**
     * Le découpage de l'année (UE/TRIMESTRE/NULL).
     */
    @XmlElement(name="decoupage")
    private String decoupage;
    
    /**
     * Booléen permettant de savoir si l'année est la dernière du diplôme.
     */
    private boolean isLastYear;
    
    /**
     * L'identifiant de l'établissement de l'année.
     */
    private int idEtablissement;
    
    /**
     * L'identifiant du diplôme dont fait partie l'année.
     */
    private int idDiplome;
    
    /**
     * La liste des UE de l'année.
     */
    @XmlElement(name="ues")
    private ArrayList<UE> ues;
    
    @XmlElement(name="regles")
    private ArrayList<Regle> listeRegles = new ArrayList();
    
    // Non en base.
    
    @XmlElement(name="isFollowed")
    private boolean isFollowed;
    
    private String nomEtablissement;
    
    private String nomDiplome;

    private Etablissement eta;
    
    /**
     * Constructeur par défaut de la classe.
     */
    public Annee () {
    
    }
    
    /**
     * Méthode permettant d'ajouter une UE à l'année.
     * 
     * @param ue L'UE à ajouter à l'année.
     * 
     * @return <b>true</b> si l'UE a bien été ajoutée.
     * <b>false</b> sinon.
     */
    public boolean addUE(UE ue) {
        if (this.ues == null) {
            this.ues = new ArrayList<>();
        }
        
        return this.ues.add(ue);
    } 
    
    /* Getters. */
    
    // En base.
    
    public int getIdAnnee() {
        return idAnnee;
    }
    
    public String getNom() {
        return nom;
    }
    
    public String getDecoupage() {
        return decoupage;
    }
    
    public boolean isIsLastYear() {
        return isLastYear;
    }
    
    public int getIdEtablissement() {
        return idEtablissement;
    }
    
    public int getIdDiplome() {
        return idDiplome;
    }
    
    public ArrayList<Regle> getListeRegles() {
        return this.listeRegles;
    }
    
    // Non en base.
    
    public boolean isFollowed() {
        return isFollowed;
    }
    
    public String getNomEtablissement() {
        return nomEtablissement;
    }

    public String getNomDiplome() {
        return nomDiplome;
    }
    
    public Etablissement getEtablissement() {
        return eta;
    }
    
    /* Setters. */

    // En base.
    
    public void setIdAnnee(int idAnnee) {
        this.idAnnee = idAnnee;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }    

    public void setDecoupage(String decoupage) {
        this.decoupage = decoupage;
    }

    public void setIsLastYear(boolean isLastYear) {
        this.isLastYear = isLastYear;
    }

    public void setIdEtablissement(int idEtablissement) {
        this.idEtablissement = idEtablissement;
    }

    public void setIdDiplome(int idDiplome) {
        this.idDiplome = idDiplome;
    }
    
    public void setListeRegles(ArrayList<Regle> listeRegles) {
        this.listeRegles = listeRegles;
    }

    // Non en base.
    
    public void setIsFollowed(boolean isFollowed) {
        this.isFollowed = isFollowed;
    }
    
    public void setNomEtablissement(String nomEtablissement) {
        this.nomEtablissement = nomEtablissement;
    }

    public void setNomDiplome(String nomDiplome) {
        this.nomDiplome = nomDiplome;
    }    
    
    public void setEtablissement(Etablissement eta) {
        this.eta = eta;
    }
}