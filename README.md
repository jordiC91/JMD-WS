# JMD-WS

Ce projet est un module contenant l'ensemble des services web de JMD.

JMD est un projet universitaire commencé en M1 MIAGE et terminé en M2 MIAGE par Jordi CHARPENTIER et Yoann VANHOESERLANDE.

Son but est de fournir aux étudiants une application permettant de calculer leur moyenne et de simuler l'obtention de leur diplôme en temps réel.

Site internet du projet : [JMD](https://www.jordi-charpentier.com/jmd).

### Contenu 

- AuthentificationService :
- DiplomeService :
- AnneeService :
- EtablissementService :
- UEService :
- MatiereService :

### Technologies utilisées 

TO-DO

### Exemple

```java
@GET
@Path("getAllByUE/{idUE}")
@Produces("application/json")
public ArrayList<Matiere> getAllByUE(@PathParam("idUE") 
                                     int idUE) {
        
	ArrayList<Matiere> matieres = new ArrayList<>();
                
    if (connexion == null) {
    	connexion = SQLUtils.getConnexion();
    }
        
    try {
        Statement stmt = connexion.createStatement();
        ResultSet results = stmt.executeQuery(  "SELECT MATIERE.ID, MATIERE.NOM, MATIERE.COEFFICIENT, MATIERE.IS_OPTION " +
                                                "FROM MATIERE, UE " +
                                                "WHERE (UE.ID = " + idUE + ") AND (MATIERE.ID_UE = UE.ID)");
            
        Matiere m = null;
            
        while (results.next()) {
            m = new Matiere();
            m.setIdMatiere(results.getInt("ID"));
            m.setNom(results.getString("NOM"));
            m.setCoefficient(results.getFloat("COEFFICIENT"));
            m.setIsOption(results.getBoolean("IS_OPTION"));
              
    		matieres.add(m);
        }

        results.close();
        stmt.close();
    } catch (SQLException ex) {
    	Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, ex);
    }

	return matieres;
}
```