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
    @XmlElement(name="idAnnee")
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
    @XmlElement(name="isLastYear")
    private boolean isLastYear;
    
    /**
     * L'identifiant de l'établissement de l'année.
     */
    @XmlElement(name="idEtablissement")
    private int idEtablissement;
    
    /**
     * L'identifiant du diplôme dont fait partie l'année.
     */
    @XmlElement(name="idDiplome")
    private int idDiplome;
    
    /**
     * Booléen permettant de savoir si l'année est suivie, ou non.
     */
    @XmlElement(name="isFollowed")
    private boolean isFollowed;
    
    /**
     * La liste des UE de l'année.
     */
    @XmlElement(name="ues")
    private ArrayList<UE> ues;
    
    // Non en base.
    
    /**
     * Le nom du diplôme lié où l'année est rattachée.
     */
    @XmlElement(name="nomDiplome")
    private String nomDiplome;
    
    /**
     * L'établissement où l'année est rattachée.
     */
    @XmlElement(name="eta")
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
    
    /**
     * Méthode retournant l'identifiant de l'année.
     *
     * @return L'identifiant de l'année.
     */
    public int getIdAnnee() {
        return this.idAnnee;
    }
    
    /**
     * Méthode retournant le nom de l'année.
     *
     * @return Le nom de l'année.
     */
    public String getNom() {
        return this.nom;
    }
    
    /**
     * Méthode retournant le découpage (SEMESTRE / NULL / TRIMESTRE) de l'année.
     *
     * @return Le découpage de l'année.
     */
    @XmlElement(name="decoupage")
    public String getDecoupage() {
        return this.decoupage;
    }
    
    /**
     * Méthode retournant le booléen permettant de savoir si l'année est la dernière du diplôme.
     *
     * @return <b>true</b> si l'année est suivie.
     * <b>false</b> sinon.
     */
    public boolean isIsLastYear() {
        return this.isLastYear;
    }
    
    /**
     * Méthode retournant l'identifiant de l'établissement où l'année est rattachée.
     *
     * @return L'identifiant de l'établissement où l'année est rattachée.
     */
    public int getIdEtablissement() {
        return this.idEtablissement;
    }
    
    /**
     * Méthode retournant l'identifiant du diplôme où l'année est rattachée.
     *
     * @return L'identifiant du diplôme où l'année est rattachée.
     */
    public int getIdDiplome() {
        return this.idDiplome;
    }
    
    /**
     * Méthode retournant le booléen permettant de savoir si l'année est suivie.
     *
     * @return <b>true</b> si l'année est suivie.
     * <b>false</b> sinon.
     */
    public boolean isFollowed() {
        return this.isFollowed;
    }
    
    /**
     * Méthode retournant la liste des UE de l'année.
     *
     * @return La liste des UE de l'année.
     */
    @XmlElement(name="ues")
    public ArrayList<UE> getListeUE() {
        return this.ues;
    }
    
    // Non en base.
    
    /**
     * Méthode retournant le nom du diplôme où est rattachée l'année.
     *
     * @return Le nom du diplôme où est rattachée l'année.
     */
    public String getNomDiplome() {
        return this.nomDiplome;
    }
    
    /**
     * Méthode retournant l'établissement où est rattachée l'année.
     *
     * @return L'établissement où est rattachée l'année.
     */
    public Etablissement getEtablissement() {
        return this.eta;
    }
    
    /* Setters. */
    
    // En base.
    
    /**
     * Méthode permettant de modifier l'identifiant de l'année.
     *
     * @param idAnnee Le nouvel identifiant de l'année.
     */
    public void setIdAnnee(int idAnnee) {
        this.idAnnee = idAnnee;
    }
    
    /**
     * Méthode permettant de modifier le nom de l'année.
     *
     * @param nom Le nouveau nom de l'année.
     */
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    /**
     * Méthode permettant de modifier le découpage de l'année.
     *
     * @param decoupage Le nouveau découpage de l'année.
     */
    public void setDecoupage(String decoupage) {
        this.decoupage = decoupage;
    }
    
    /**
     * Méthode permettant de modifier le booléen permettant de savoir si l'année
     * est la dernière du diplôme, ou non.
     *
     * @param isLastYear Le nouveau booléen permettant de savoir si l'année est la
     * dernière du diplôme.
     */
    public void setIsLastYear(boolean isLastYear) {
        this.isLastYear = isLastYear;
    }
    
    /**
     * Méthode permettant de modifier l'identifiant de l'établissement où est
     * rattachée l'année.
     *
     * @param idEtablissement Le nouvel identifiant de l'établissement où est rattachée
     * l'année.
     */
    public void setIdEtablissement(int idEtablissement) {
        this.idEtablissement = idEtablissement;
    }
    
    /**
     * Méthode permettant de modifier l'identifiant du diplôme où est rattachée l'année.
     *
     * @param idDiplome Le nouvel identifiant du diplôme.
     */
    public void setIdDiplome(int idDiplome) {
        this.idDiplome = idDiplome;
    }
    
    /**
     * Méthode permettant de modifier le booléen permettant de savoir si l'année est
     * suivie.
     *
     * @param isFollowed Le nouveau booléen permettant de savoir si l'année est
     * suivie.
     */
    public void setIsFollowed(boolean isFollowed) {
        this.isFollowed = isFollowed;
    }
    
    /**
     * Méthode permettant de modifier la liste des UE de l'année.
     *
     * @param listeUE La nouvelle liste des UE de l'année.
     */
    public void setListeUE(ArrayList<UE> listeUE) {
        this.ues = listeUE;
    }
    
    // Non en base.
    
    /**
     * Méthode permettant de modifier le nom du diplôme où est rattachée l'année.
     *
     * @param nomDiplome Le nouveau nom du diplôme où est rattachée l'année.
     */
    public void setNomDiplome(String nomDiplome) {
        this.nomDiplome = nomDiplome;
    }
    
    /**
     * Méthode permettant de modifier l'établissement où est rattachée l'année.
     *
     * @param eta Le nouvel établissement où sera rattachée l'année.
     */
    public void setEtablissement(Etablissement eta) {
        this.eta = eta;
    }
}