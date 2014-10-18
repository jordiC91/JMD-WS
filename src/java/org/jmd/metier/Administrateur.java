package org.jmd.metier;

import java.sql.Date;
import javax.xml.bind.annotation.*;

/**
 * Classe représentant un administrateur.
 * 
 * @author jordi charpentier - yoann vanhoeserlande
 */
@XmlRootElement
public class Administrateur {
    
    /**
     * L'identifiant de l'administrateur.
     */
    private int id;
    
    /**
     * Le nom de l'administrateur.
     */
    private String nom;
    
    /**
     * Le prénom de l'administrateur.
     */
    private String prenom;
    
    /**
     * L'email de l'administrateur.
     */
    private String email;
    
    /**
     * Le pseudo de l'administrateur.
     */
    private String pseudo;
    
    /**
     * Le mot de passe de l'administrateur.
     */
    private String password;
    
    /**
     * Booléen permettant de savoir si le compte est activé ou non.
     */
    private boolean estActive;
    
    /**
     * La date de fin de validité du compte de l'administrateur.
     */
    private Date dateFinValidite;

    /**
     * Constructeur par défaut de la classe.
     */
    public Administrateur () {
    
    }
    
    /* Getters. */

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getEmail() {
        return email;
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getPassword() {
        return password;
    }
    
    public boolean isEstActive() {
        return estActive;
    }

    public Date getDateFinValidite() {
        return dateFinValidite;
    }
    
    /* Setters. */

    public void setId(int id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public void setPassword(String password) {
        this.password = password;
    }    

    public void setEstActive(boolean estActive) {
        this.estActive = estActive;
    }

    public void setDateFinValidite(Date dateFinValidite) {
        this.dateFinValidite = dateFinValidite;
    }
}