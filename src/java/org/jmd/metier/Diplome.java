/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jmd.metier;

/**
 *
 * @author yoyito
 */
public class Diplome {
    private int idDiplome;
    private String nom;
    
    public Diplome(){}

    /**
     * @return the idDiplome
     */
    public int getIdDiplome() {
        return idDiplome;
    }

    /**
     * @param idDiplome the idDiplome to set
     */
    public void setIdDiplome(int idDiplome) {
        this.idDiplome = idDiplome;
    }

    /**
     * @return the nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * @param nom the nom to set
     */
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    
}
