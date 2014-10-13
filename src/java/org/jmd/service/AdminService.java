package org.jmd.service;

import java.sql.*;
import java.util.*;
import java.util.logging.*;
import javax.annotation.PreDestroy;
import javax.mail.*;
import javax.mail.internet.*;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.jmd.*;

/**
 * Service web gérant l'authentification et la d�connexion d'un utilisateur.
 * 
 * @author jordi charpentier - yoann vanhoeserlande
 */
@Path("admin")
public class AdminService {
    
    private Connection connexion;
 
    public AdminService() {
        
    }

    /**
     * Méthode permettant de connecter un utilisateur (si les identifiants sont
     * bons). 
     * Si les identifiants sont bons, une session est créée qui aura comme durée
     * de vie celle définie dans le web.xml du projet.
     * 
     * @param pseudo Le pseudo de l'utilisateur.
     * @param password Le mot de passe de l'utilisateur.
     * @param request La requête HTTP ayant appelée le service.
     * 
     * @return 2 possibilités :
     * - Un code HTTP 200 si les identifiants sont bons.
     * - Un code HTTP 401 si les identifiants n'étaient pas bons.
     */
    @POST
    @Path("login")
    public Response login(@QueryParam("username")
                          String pseudo,
                          @QueryParam("password")
                          String password,
                          @Context 
                          HttpServletRequest request) {
        
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        try {
            Statement stmt = connexion.createStatement();  
            ResultSet results = stmt.executeQuery("SELECT * FROM administrateur WHERE (pseudo ='" + pseudo + "') AND (password ='" + password + "')");
            
            while (results.next()) {
                if (results.getString("PSEUDO") != null) {                    
                    request.getSession(true);
                    
                    return Response.status(200).build();
                }
            }
            
            results.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(401).build();
    }
    
    /**
     * Méthode permettant de déconnecter un utilisateur.
     * La session actuelle de l'utilisateur est invalidée (= terminée).
     * 
     * @param request La requête HTTP ayant appelée le service.
     * 
     * @return Un code HTTP 200 si la session a bien été terminée.
     */
    @Path("logout")
    public Response logout(@Context 
                           HttpServletRequest request) {
        
        request.getSession().invalidate();
        
        return Response.status(200).build();
    }
    
    /**
     * Méthode permettant de générer un code aléatoire (alphabet + 0123456789)
     * de 20 caractères.
     * 
     * @return Le code aléatoire généré.
     */
    private String generateRandomCode() {
        char[] chars = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        
        return sb.toString();
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
     * - Un code HTTP 503 si une erreur de type SQL ou pendant l'envoi du mail arrive.
     * - Un code HTTP 403 si l'utilisateur spécifié n'existe pas.
     */
    @Path("passwordOublie")
    @GET
    public Response passwordOublie(@QueryParam("pseudo")
                                   String pseudo) {
        
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }

        String randomString = generateRandomCode();
        
        try {
            Statement stmt = connexion.createStatement();
            
            ResultSet results = stmt.executeQuery("SELECT * FROM administrateur WHERE (pseudo ='" + pseudo + "')");
            
            String emailAdmin = "";
            
            while (results.next()) {
                emailAdmin = results.getString("EMAIL");
            }
            
            if (emailAdmin.length() == 0) {
                return Response.status(404).entity("ADMIN_NOT_FOUND").build();
            }
            
            stmt.execute("INSERT INTO CODE_REINIT_MDP (PSEUDO, CODE) VALUES ('" + pseudo + "','" + randomString + "');");
            stmt.close();

            Properties properties = System.getProperties();
            properties.setProperty("mail.smtp.auth", "true");
            properties.setProperty("mail.smtp.starttls.enable", "true");
            properties.setProperty("mail.smtp.host", "smtp.gmail.com");
            properties.setProperty("mail.smtp.port", "25");

            Session session = Session.getInstance(properties, 
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Constantes.EMAIL_JMD, "gkc19iregpt3qir");
                    }  
                }
            );

            try {
                MimeMessage message = new MimeMessage(session);

                message.setFrom(new InternetAddress(Constantes.EMAIL_JMD));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailAdmin));
                message.setSubject("JMD - Mot de passe oublié");

                String text = "Bonjour " + pseudo + ",<br />"
                        + "Merci de cliquer <a href=\"" + Constantes.SERVER_URL + "/admin/resetPassword?pseudo="+ pseudo+ "&code=" + randomString + "\">ici</a> pour réinitialiser votre mot de passe."
                        + "<br /><br />"
                        + "Cordialement,<br />L'équipe de JMD<br /><br />"
                        + "PS : Si vous n'êtes pas à l'origine de cette demande, merci de cliquer <a href=\""+ Constantes.SERVER_URL + "/admin/cancelResetRequest?pseudo="+ pseudo + "\">ici</a>.";

                message.setContent(text, "text/html; charset=utf-8");
                
                Transport.send(message);
                
                return Response.status(200).build();
            } catch (MessagingException e) {
                Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, e);

                return Response.status(503).build();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);
            
            return Response.status(503).build();
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
     * - Un code HTTP 403 si aucune demande n'existe pour l'utilisateur.
     */
    @Path("cancelResetRequest")
    @GET
    public Response cancelResetRequest(@QueryParam("pseudo")
                                       String pseudo) {
        
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        try {
            Statement stmt = connexion.createStatement();
            int rows = stmt.executeUpdate("DELETE FROM CODE_REINIT_MDP WHERE (PSEUDO = '" + pseudo + "')");
        
            if (rows == 0) {
                return Response.status(404).entity("Aucune demande.").build();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MatiereService.class.getName()).log(Level.SEVERE, null, ex);
                
            return Response.status(503).build();
        }
            
        return Response.status(200).entity("Demande supprimée.").build();
    }
    
    /**
     * 
     * 
     * @param pseudo
     * @param code
     * 
     * @return 
     */
    @Path("resetPassword")
    public Response resetPassword(@QueryParam("pseudo")
                                  String pseudo,
                                  @QueryParam("code")
                                  String code) {
        
        return Response.status(200).build();
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