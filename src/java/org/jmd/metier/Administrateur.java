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

    /**
     * Méthode retournant l'identifiant de l'administrateur.
     * 
     * @return L'identifiant de l'administrateur.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Méthode retournant le nom de l'administrateur.
     * 
     * @return Le nom de l'administrateur.
     */
    public String getNom() {
        return this.nom;
    }

    /**
     * Méthode retournant le prénom de l'administrateur.
     * 
     * @return Le prénom de l'administrateur. 
     */
    public String getPrenom() {
        return this.prenom;
    }

    /**
     * Méthode retournant l'email de l'administrateur.
     * 
     * @return L'email de l'administrateur. 
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Méthode retournant le pseudo de l'administrateur.
     * 
     * @return Le pseudo de l'administrateur. 
     */
    public String getPseudo() {
        return this.pseudo;
    }
    
    /**
     * Méthode retournant le mot de passe de l'administrateur.
     * A noter que le mot de passe est hashé en SHA-256 et salé.
     * 
     * @return Le mot de passe de l'administrateur.
     */
    public String getPassword() {
        return this.password;
    }
    
    /**
     * Méthode permettant de savoir si le compte admin est activé.
     * 
     * @return Un booléen valant <b>true</b> si le compte est actif.
     * <b>false</b> sinon.
     */
    public boolean isEstActive() {
        return this.estActive;
    }

    /**
     * Méthode retournant la date de fin de validité de l'administrateur.
     * 
     * @return La date de fin de validité de l'administrateur.
     */
    public Date getDateFinValidite() {
        return this.dateFinValidite;
    }
    
    /* Setters. */

    /**
     * Méthode permettant de modifier l'identifiant de l'administrateur.
     * 
     * @param id Le nouvel identifiant de l'administrateur.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Méthode permettant de modifier le nom de l'administrateur.
     * 
     * @param nom Le nouveau nom de l'administrateur.
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Méthode permettant de modifier le prénom de l'administrateur.
     * 
     * @param prenom Le nouveau prénom de l'administrateur.
     */
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    /**
     * Méthode permettant de modifier l'email de l'administrateur.
     * 
     * @param email Le nouvel email de l'administrateur.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Méthode permettant de modifier le pseudo de l'administrateur.
     * 
     * @param pseudo Le nouveau pseudo de l'administrateur.
     */
    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    /**
     * Méthode permettant de modifier le mot de passe du compte.
     * <b>Attention</b> : le nouveau mot de passe doit être hashé en SHA-256
     * et salé.
     * 
     * @param password Le nouveau mot de passe du compte.
     */
    public void setPassword(String password) {
        this.password = password;
    }    

    /**
     * Méthode permettant de modifier le fait que le compte soit activé, ou non.
     * 
     * @param estActive Le nouveau booléen identifiant l'activation du compte, ou non.
     */
    public void setEstActive(boolean estActive) {
        this.estActive = estActive;
    }

    /**
     * Méthode permettant de modifier la date de fin de validité du compte.
     * 
     * @param dateFinValidite La nouvel date de fin de validité du compte.
     */
    public void setDateFinValidite(Date dateFinValidite) {
        this.dateFinValidite = dateFinValidite;
    }
}