/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package org.jmd.metier;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author yoyito
 */
@XmlRootElement
public class Etablissement {
    private int idEtablissement;
    private String nom = "";
    private String ville = "";
    
    public Etablissement() {}

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

    /**
     * @return the ville
     */
    public String getVille() {
        return ville;
    }

    /**
     * @param ville the ville to set
     */
    public void setVille(String ville) {
        this.ville = ville;
    }

    /**
     * @return the idEtablissement
     */
    public int getIdEtablissement() {
        return idEtablissement;
    }

    /**
     * @param idEtablissement the idEtablissement to set
     */
    public void setIdEtablissement(int idEtablissement) {
        this.idEtablissement = idEtablissement;
    }

    
}
