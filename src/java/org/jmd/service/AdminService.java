package org.jmd.service;

import org.jmd.utils.AdminUtils;
import org.jmd.utils.SQLUtils;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import java.security.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;
import javax.annotation.PreDestroy;
import javax.mail.*;
import javax.mail.internet.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.jmd.*;
import org.jmd.metier.Administrateur;

/**
 * Service web gérant l'authentification et la d�connexion d'un utilisateur.
 * 
 * @author jordi charpentier - yoann vanhoeserlande
 */
@Path("admin")
public class AdminService {
    
    /**
     * Objet représentant une connexion à la base de données de 
     * l'application.
     */
    private Connection connexion;
 
    /**
     * Constructeur par défaut de la classe.
     */
    public AdminService() {
        
    }

    /**
     * Méthode permettant de connecter un utilisateur.
     * Si les logins envoyés sont bons, le serveur génère un token (20 caractères)
     * et l'envoie à l'utilisateur.
     * 
     * @param pseudo Le pseudo de l'utilisateur.
     * @param password Le mot de passe de l'utilisateur.
     * 
     * @return 4 possibilités :
     * - Un code HTTP 200 si les identifiants sont bons.
     * - Un code HTTP 401 si les identifiants n'étaient pas bons.
     * - Un code HTTP 403 si le compte n'est pas activé ou plus actif.
     * - Un code HTTP 500 si une erreur SQL se produit.
     */
    @POST
    @Path("login")
    public Response login(
            @FormParam("username")
                    String pseudo,
            @FormParam("password")
                    String password) {
        
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        try {
            Statement stmt = connexion.createStatement();  
            ResultSet results = stmt.executeQuery("SELECT * FROM ADMINISTRATEUR WHERE (PSEUDO ='" + pseudo + "')");
            
            while (results.next()) {
                String sel = results.getString("SEL");
                String passwordSalted = sha256(password + sel);
                
                if (!passwordSalted.equals(results.getString("PASSWORD"))) {
                    return Response.status(401).build();
                }
                
                if (!results.getBoolean("EST_ACTIF")) {              
                    results.close();
                    stmt.close();
            
                    return Response.status(403).build();
                } 
                
                if ((results.getDate("DATE_FIN_VALIDITE") != null) && 
                        (results.getDate("DATE_FIN_VALIDITE").after(new java.sql.Date(new java.util.Date().getTime())))) {    
                    
                    results.close();
                    stmt.close();
            
                    return Response.status(403).build();
                }
                
                results.close();
                
                String token = generateRandomCode();
                stmt.executeUpdate("UPDATE ADMINISTRATEUR SET TOKEN = '" + token + "' WHERE PSEUDO = '" + pseudo + "';");
                stmt.executeUpdate("UPDATE ADMINISTRATEUR SET TIMESTAMP_USER = " + new java.util.Date().getTime() + " WHERE PSEUDO = '" + pseudo + "';");
                
                stmt.close();
                
                return Response.status(200).entity(token).build();
            }
  
            results.close();
            stmt.close();
            
            return Response.status(401).build();
        } catch (SQLException ex) {
            Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, ex);
            
            return Response.status(500).build();
        }
    }
    
    /**
     * Méthode permettant de déconnecter un utilisateur.
     * Elle va supprimer le token et le timestamp de l'utilisateur spécifié.
     * 
     * @param pseudo Le pseudo de l'utilisateur.
     * @param token Le token de l'utilisateur.
     * @param timestamp Le timestamp de l'utilisateur.
     * 
     * @return 2 possibilités :
     * - Un code HTTP 200 si l'utilisateur a bien été déconnecté.
     * - Un code HTTP 401 si les infos (token ou timestamp) n'étaient pas bonnes.
     * - Un code HTTP 500 si une erreur SQL se produit.
     */
    @Path("logout")
    @GET
    public Response logout(
            @QueryParam("pseudo")
                    String pseudo,
            @QueryParam("token")
                    String token,
            @QueryParam("timestamp")
                    long timestamp) {
        
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        if (AdminUtils.checkToken(pseudo, token) && AdminUtils.checkTimestamp(pseudo, timestamp)) {
            try {
                Statement stmt = connexion.createStatement();  
                stmt.executeUpdate("UPDATE ADMINISTRATEUR SET TOKEN = 'NULL' WHERE PSEUDO = '" + pseudo + "';");
                stmt.executeUpdate("UPDATE ADMINISTRATEUR SET TIMESTAMP_USER = '0' WHERE PSEUDO = '" + pseudo + "';");

                stmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, ex);

                return Response.status(500).build();
            }
        } else {
            return Response.status(401).build();
        }
        
        return Response.status(200).build();
    }
    
    /**
     * Méthode permettant de gérer la première étape de la demande de réinitialisation
     * de mot de passe d'un utilisateur (envoi d'un mail de confirmation).
     * 
     * @param pseudo Le pseudo de la personne qui demande de réinitialiser son
     * mot de passe.
     * 
     * @return 3 possibilités :
     * - Un code HTTP 200 si l'utilisateur ayant fait la demande "existe".
     * - Un code HTTP 500 si une erreur de type SQL ou pendant l'envoi du mail arrive.
     * - Un code HTTP 404 si l'utilisateur spécifié n'existe pas.
     */
    @Path("passwordOublie")
    @GET
    public Response passwordOublie(
            @QueryParam("pseudo")
                    String pseudo) {
        
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }

        String randomString = generateRandomCode();
        
        try {
            Statement stmt = connexion.createStatement();
            ResultSet results = stmt.executeQuery("SELECT * FROM ADMINISTRATEUR WHERE (PSEUDO ='" + pseudo + "')");
            
            String emailAdmin = "";
            
            while (results.next()) {
                emailAdmin = results.getString("EMAIL");
            }
            
            if (emailAdmin.length() == 0) {
                return Response.status(404).entity("ADMIN_NOT_FOUND").build();
            }
            
            stmt.execute("INSERT INTO CODE_REINIT_MDP (PSEUDO, CODE) VALUES ('" + pseudo + "','" + randomString + "');");
            stmt.close();

            String text = "Bonjour " + pseudo + ",<br />"
                        + "Merci de cliquer <a href=\"" + Constantes.SERVER_URL + "/admin/resetPassword?pseudo="+ pseudo+ "&code=" + randomString + "\">ici</a> pour réinitialiser votre mot de passe."
                        + "<br /><br />"
                        + "Cordialement,<br />L'équipe de JMD<br /><br />"
                        + "PS : Si vous n'êtes pas à l'origine de cette demande, merci de cliquer <a href=\""+ Constantes.SERVER_URL + "/admin/cancelResetRequest?pseudo="+ pseudo + "\">ici</a>.";
            
            String subject = "JMD - Mot de passe oublié";
            
            sendMail(subject, text, emailAdmin);
            
            return Response.status(200).build();
        } catch (SQLException ex) {
            Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);
            
            return Response.status(500).build();
        }
    }
    
    /**
     * Méthode permettant d'annuler une demande de réinitialisation de mot de passe.
     * 
     * @param pseudo Le pseudo de la personne qui annule sa demande.
     * 
     * @return 3 possibilités :
     * - Un code HTTP 200 si l'utilisateur ayant fait la demande "existe" et que
     * l'annulation a bien été faite.
     * - Un code HTTP 503 si une erreur de type SQL ou pendant l'envoi du mail arrive.
     * - Un code HTTP 404 si aucune demande n'existe pour l'utilisateur.
     */
    @Path("cancelResetRequest")
    @GET
    public Response cancelResetRequest(
            @QueryParam("pseudo")
                    String pseudo) {
        
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        try {
            Statement stmt = connexion.createStatement();
            int rows = stmt.executeUpdate("DELETE FROM CODE_REINIT_MDP WHERE (PSEUDO = '" + pseudo + "')");
        
            if (rows == 0) {
                return Response.status(404).entity("NO_REQUEST_FOUND").build();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, ex);
                
            return Response.status(503).build();
        }
            
        return Response.status(200).entity("La demande de reset de mot de passe a bien été supprimée.").build();
    }
 
    /**
     * Méthode permettant de faire la réinitialisation du mot de passe.
     * 
     * @param pseudo Le pseudo de la personne ayant fait la demande.
     * @param code Le code généré lors de la première étape.
     * 
     * @return 3 possibilités :
     * - Un code HTTP 200 si l'utilisateur ayant fait la demande "existe" et que
     * la réinitialisation a bien été faite.
     * - Un code HTTP 500 si une erreur de type SQL arrive.
     * - Un code HTTP 404 si aucune demande n'existe pour l'utilisateur.
     */
    @Path("resetPassword")
    @GET
    public Response resetPassword(
            @QueryParam("pseudo")
                    String pseudo,
            @QueryParam("code")
                    String code) {
        
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        try {
            Statement stmt = connexion.createStatement();
            ResultSet results = stmt.executeQuery("SELECT * " +
                                                  "FROM ADMINISTRATEUR, CODE_REINIT_MDP " +
                                                  "WHERE (CODE_REINIT_MDP.PSEUDO = ADMINISTRATEUR.PSEUDO) AND (CODE_REINIT_MDP.PSEUDO = '" + pseudo + "') AND (CODE_REINIT_MDP.CODE = '" + code + "')");
            
            boolean wasFound = false;
            String emailAdmin = "";
            
            while (results.next()) {    
                emailAdmin = results.getString("EMAIL");
                
                if ( (results.getString("CODE").equals(code)) &&
                     (results.getString("PSEUDO").equals(pseudo))) {
                    
                    wasFound = true;
                    break;
                }
            }
            
            if (emailAdmin.length() == 0) {
                return Response.status(404).entity("ADMIN_NOT_FOUND").build();
            }
            
            if (!wasFound) {
                return Response.status(404).entity("NO_REQU").build();
            } else {
                String newMdp = generateRandomCode();
                
                String text = "Bonjour " + pseudo + ",<br />"
                        + "Voici votre nouveau mot de passe : " + newMdp + "."
                        + "<br /><br />"
                        + "Cordialement,<br />L'équipe de JMD";
            
                String subject = "JMD - Nouveau mot de passe";
            
                sendMail(subject, text, emailAdmin);
                
                stmt.executeUpdate("UPDATE ADMINISTRATEUR SET PASSWORD = '" + sha256(newMdp) + "' WHERE (PSEUDO = '" + pseudo + "')");
            }
            
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);
            
            return Response.status(500).build();
        }
        
        return Response.status(200).build();
    }
    
    /**
     * Méthode permettant de clôturer un compte admin.
     * 
     * @param closedAdminNickname Le pseudo du compte de l'administrateur à clôturer.
     * 
     * @param pseudo Le pseudo de l'administrateur ayant fait la demande.
     * @param token Le token envoyé par l'administrateur.
     * @param timestamp Le timestamp envoyé par l'administrateur ayant fait la requête.
     * Permet d'éviter les rejeux.
     * 
     * @return 3 possibilités :
     * - Un code HTTP 200 si l'utilisateur ayant fait la demande est connecté 
     * (donc autorisé).
     * - Un code HTTP 401 si c'est un utilisateur non connecté (donc non autorisé)
     * qui a fait la clôture.
     * - Un code HTTP 500 si une erreur SQL se produit.
     */
    @Path("closeAdminAccount")
    @GET
    public Response closeAdminAccount(
        @QueryParam("closedAdminNickname")
                String closedAdminNickname,
        @QueryParam("pseudo")
                String pseudo,
        @QueryParam("token")
                String token,
        @QueryParam("timestamp")
                long timestamp) {
        
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
           
        if (AdminUtils.checkToken(pseudo, token) && AdminUtils.checkTimestamp(pseudo, timestamp)) {
            try {
                Statement stmt = connexion.createStatement();
                stmt.executeUpdate("UPDATE ADMINISTRATEUR SET EST_ACTIF = 0 WHERE PSEUDO = '" + pseudo + "';");
                stmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);

                return Response.status(500).build();
            }
        } else {
            return Response.status(401).build();
        }
            
        return Response.status(200).build();
    }
    
    /**
     * Méthode permettant de nommer un administrateur.
     * 
     * @param pseudoToNominate Le pseudo de l'utilisateur à nommer administrateur.
     * 
     * @param pseudo Le pseudo de l'administrateur ayant fait la demande.
     * @param token Le token envoyé par l'administrateur.
     * @param timestamp Le timestamp envoyé par l'administrateur ayant fait la requête.
     * Permet d'éviter les rejeux.
     * 
     * @return 3 possibilités :
     * - Un code HTTP 200 si l'utilisateur ayant fait la demande de création est
     * connecté (donc autorisé).
     * - Un code HTTP 401 si c'est un utilisateur non connecté (donc non autorisé)
     * qui a fait la nomination.
     * - Un code HTTP 500 si une erreur SQL se produit.
     */
    @Path("nominateAdmin")
    @GET
    public Response nominateAdmin(
            @QueryParam("pseudoToNominate")
                    String pseudoToNominate,
            @QueryParam("pseudo")
                String pseudo,
            @QueryParam("token")
                String token,
            @QueryParam("timestamp")
                long timestamp) {
        
            if (connexion == null) {
                connexion = SQLUtils.getConnexion();
            }
            
            if (AdminUtils.checkToken(pseudo, token) && AdminUtils.checkTimestamp(pseudo, timestamp)) {
                try {
                    Statement stmt = connexion.createStatement();
                    stmt.executeUpdate("UPDATE ADMINISTRATEUR SET EST_ACTIF = 1 WHERE PSEUDO = '" + pseudoToNominate + "';");

                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);

                    return Response.status(500).build();
                }
            } else {
                return Response.status(401).build();
            }
            
            return Response.status(200).build();
    }

    /**
     * Méthode permettant de récupérer tous les administrateurs ayant un compte
     * non activé.
     * 
     * @return Une liste contenant l'ensemble des administrateurs ayant un compte
     * non activé.
     */
    @GET
    @Path("getAllAdminInactive")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Administrateur> getAllAdminInactive() {
        ArrayList<Administrateur> listesAdmin = new ArrayList<>();
        Administrateur a = null;
        
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        try {
            Statement stmt = connexion.createStatement();
            ResultSet results = stmt.executeQuery("SELECT * FROM ADMINISTRATEUR WHERE EST_ACTIF = 0;");
            
            while (results.next()) {
                a = new Administrateur();
                a.setId(results.getInt("ID"));
                a.setEmail(results.getString("EMAIL"));
                a.setEstActive(results.getBoolean("EST_ACTIF"));
                a.setNom(results.getString("NOM"));
                a.setPrenom(results.getString("PRENOM"));
                a.setPseudo(results.getString("PSEUDO"));
                
                listesAdmin.add(a);
            }
            
            results.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return listesAdmin;
    }
    
    /**
     * Méthode permettant à un utilisateur de s'inscrire pour devenir administrateur.
     * 
     * @param nom Le nom de l'administrateur.
     * @param prenom Le prénom de l'administrateur.
     * @param email L'email de l'administrateur.
     * @param pseudo Le pseudo de l'administrateur.
     * @param password Le mot de passe de l'administrateur. 
     * Il est envoyé par l'utilisateur s'inscrivant en SHA-256.
     * 
     * @return 3 possibilités :
     * - Un code HTTP 200 si l'inscription est OK.
     * - Un code HTTP 403 si les informations entrées (pseudo, entre autre)
     * existent déjà en base.
     * - Un code HTTP 500 si une erreur SQL se produit.
     */
    @Path("subscription")
    @GET
    public Response subscription(
            @QueryParam("nom")
                    String nom,
            @QueryParam("prenom")
                    String prenom,
            @QueryParam("email")
                    String email,
            @QueryParam("pseudo")
                    String pseudo,
            @QueryParam("password")
                    String password) {
        
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        try {
            Statement stmt = connexion.createStatement();
            
            String sel = generateRandomCode();
            String passwordSalted = sha256(password + sel);
            
            stmt.execute("INSERT INTO ADMINISTRATEUR (PSEUDO, NOM, PRENOM, PASSWORD, EMAIL, EST_ACTIF, SEL, TOKEN, TIMESTAMP_USER) VALUES ('" + pseudo + "', '" + nom + "', '" + prenom + "', '" + passwordSalted + "', '" + email + "', 0, '" + sel + "', '', 0);");
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);
                
            if(ex instanceof MySQLIntegrityConstraintViolationException){
                return Response.status(403).entity("DUPLICATE_ENTRY").build();
            }
                
            return Response.status(500).build();
        }
        
        return Response.status(200).build();
    }
    
    /**
     * Méthode permettant d'hasher une chaîne de caractères en SHA-256.
     * 
     * @param passwordToHash La chaîne à hasher.
     * 
     * @return La chaîne hashée en SHA-256.
     */
    private String sha256(String passwordToHash) {
        String generatedPassword = null;
		
	try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
			
            for(int i=0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
			
            generatedPassword = sb.toString();
	} catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Le SHA-256 n'est pas supporté.");
	}
		
	return generatedPassword;
    }
    
    /**
     * Méthode permettant de générer un code aléatoire (alphabet + 0123456789)
     * de 20 caractères.
     * 
     * @return Le code aléatoire généré.
     */
    private String generateRandomCode() {
        char[] chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        
        return sb.toString();
    }
    
    /**
     * Méthode permettant d'envoyer un mail.
     * 
     * @param subject Le sujet du mai.
     * @param text Le contenu du mail.
     * @param to Le destinataire du mail.
     */
    private void sendMail(String subject, String text, String to) {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.user", "jaimondiplome@gmail.com");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", 465);
        properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.smtp.debug", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.port", 465);
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.fallback", "false");

        Session session = Session.getInstance(properties, 
            new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(Constantes.EMAIL_JMD, Constantes.PASSWORD_JMD);
                }  
            }
        );

        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(Constantes.EMAIL_JMD));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setContent(text, "text/html; charset=utf-8");
  
            Transport.send(message);
        } catch (MessagingException e) {
            Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, e);
        }
    } 
    
    /**
     * Méthode exécutée avant la fin de vie du service.
     * La connexion à la base est fermée.
     */
    @PreDestroy
    public void onDestroy() {
        if (connexion != null) {
            try {
                connexion.close();
            } catch (SQLException ex) {
                Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}