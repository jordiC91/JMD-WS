# JMD-WS

This Github project is a module containing all the web services of JMD. These services are used by our two applications : Android and iOS. 

### Presentation of JMD

JMD ('J'ai Mon Diplôme' in French, 'I Have My Diploma' in English) is a university project started in "M1 MIAGE" and finished in "M2 MIAGE" by Jordi CHARPENTIER and Yoann VANHOESERLANDE. 

Its purpose is to provide to all students an application to calculate their average marks and simulate their graduation in real time.

Several other features are also available.
Examples:
- For students: export one year as PDF. 
You can see an example here : http://www.jordi-charpentier.com/jmd/Example_Mail_Modif.png
- For administrators: tracking changes from one year, with a mail for each change.
You can see an example here : http://www.jordi-charpentier.com/jmd/Example_PDF.pdf

### Technologies used on JMD

- Webservices : Java (+ JAX-RS library).
- Database : MySQL.
- iOS application : Swift
- Android application : Java

### Content of 'JMD-WS'

List of the services in the module :
- AdminService.

For each of those services, at least 2 methods are exposed ("create" and "delete") :
- DiplomeService
- AnneeService 
- EtablissementService 
- UEService 
- MatiereService 
 
### Login process 

We store in our database the password hashed (SHA256) and salted and the used salt (one different salt for each user).
When a user wants to log, the application sends his hashed password. 
The login method will do CALCULATED_PASSWORSD = SHA256(PASSWORD_RECEIVED + SALT_IN_DATABASE). If the calculated password is the same as the stored password, this is OK. Otherwise, a 401 error is returned. 

### Security in our services

When a service require an admin right, we "secured it" with our means. With each request, it have to receive the nickname of the admin calling the service, the current timestamp (to avoid replay attack) and the token of the admin. The token is a random string generated when a user is logging into JMD and stored for each of them. A token expire after 15 minutes of inactivity.
If the token, the timestamp or the nickname is incorrect, a 401 error is returned.

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
