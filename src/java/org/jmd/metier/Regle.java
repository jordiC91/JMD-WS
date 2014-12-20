package org.jmd.metier;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Classe représentant une règle (pour les UE/matière).
 *
 * @author jordi charpentier - yoann vanhoeserlande
 */
@XmlRootElement
public class Regle {

    /**
     * L'identifiant de la règle.
     */
    private int id;

    /**
     * La règle (0 ou 1, soit NB_OPT_MINI ou NOTE_MINIMALE).
     */
    private int regle;

    /**
     * L'opérateur de la règle.
     */
    private int operateur;

    /**
     * La valeur de la règle.
     */
    private int valeur;

    /**
     * L'identifiant de l'UE rattachée à la règle.
     */
    private int idUE;
    
    /**
     * Le nom de l'UE associée.
     */
    private String nomUE;

    /**
     * L'identifiant de l'année rattachée à la règle.
     */
    private int idAnnee;
    
    /**
     * L'identifiant de la matière rattachée à la règle.
     */
    private int idMatiere;

    /**
     * Constructeur par défaut de la classe.
     */
    public Regle() {

    }

    /* Getters. */
    
    public int getId() {
        return this.id;
    }

    public int getRegle() {
        return this.regle;
    }

    public int getOperateur() {
        return this.operateur;
    }

    public int getValeur() {
        return this.valeur;
    }

    public int getIdUE() {
        return this.idUE;
    }
    
    public String getNomUE() {
        return this.nomUE;
    }

    public int getIdAnnee() {
        return this.idAnnee;
    }
    
    public int getIdMatiere() {
        return this.idMatiere;
    }

    /* Setters. */
    
    public void setId(int id) {
        this.id = id;
    }

    public void setRegle(int regle) {
        this.regle = regle;
    }

    public void setOperateur(int operateur) {
        this.operateur = operateur;
    }

    public void setValeur(int valeur) {
        this.valeur = valeur;
    }

    public void setIdUE(int idUE) {
        this.idUE = idUE;
    }
    
    public void setNomUE(String nomUE) {
        this.nomUE = nomUE;
    }

    public void setIdAnnee(int idAnnee) {
        this.idAnnee = idAnnee;
    }
    
    public void setIdMatiere(int idMatiere) {
        this.idMatiere = idMatiere;
    }
}