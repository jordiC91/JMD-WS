# JMD-WS

This project is a module containing all the web services of JMD.

JMD is a university project started in "M1 MIAGE" and finished in "M2 MIAGE" by Jordi CARPENTER and Yoann VANHOESERLANDE.

Its purpose is to provide to the students with application to calculate their average and simulate their graduation in real time.

Several other features are also available.
Examples:
- For students: export one year as PDF.
- For administrators: tracking changes from one year (mail for each change).

### Technologies used on the project

- Webservices : Java (+ JAX-RS library).
- iOS application : Swift
- Android application : Java
- Database : MySQL.

### Content 

List of the services of the module :
- AdminService 

For each of those services, at least 2 methods are offered ("create" and "delete").
- RegleService 
- DiplomeService
- AnneeService 
- EtablissementService 
- UEService 
- MatiereService 
 
### Example

Example of one method on "MatiereService" :

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
        ResultSet results = stmt.executeQuery( 
        	"SELECT * " +
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
