package org.jmd.service;

import java.sql.*;
import java.util.Properties;
import java.util.Random;
import java.util.logging.*;
import javax.annotation.PreDestroy;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.jmd.SQLUtils;

/**
 * Service web g�rant l'authentification et la d�connexion d'un utilisateur.
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
    public Response login(  @QueryParam("username")
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
    
    @Path("passwordOublie")
    @GET
    public Response passwordOublie(@QueryParam("pseudo")
                                   String pseudo) {
        
        if (connexion == null) {
            connexion = SQLUtils.getConnexion();
        }
        
        char[] chars = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }

        String randomString = sb.toString();
        
        try {
            Statement stmt = connexion.createStatement();
            stmt.execute("INSERT INTO CODE_REINIT_MDP (PSEUDO, CODE) VALUES ('" + pseudo + "','" + randomString + "');");
            stmt.close();

            String to = "jordi.charpentier@gmail.com";
            String from = "jaimondiplome@gmail.com";

            Properties properties = System.getProperties();
            properties.setProperty("mail.smtp.auth", "true");
            properties.setProperty("mail.smtp.starttls.enable", "true");
            properties.setProperty("mail.smtp.host", "smtp.gmail.com");
            properties.setProperty("mail.smtp.port", "25");

            Session session = Session.getInstance(properties, 
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("jaimondiplome@gmail.com", "gkc19iregpt3qir");
                    }  
                }
            );

            try {
                MimeMessage message = new MimeMessage(session);

                message.setFrom(new InternetAddress(from));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                message.setSubject("JMD - Mot de passe oublié");

                String text = "Bonjour " + pseudo + ",<br />"
                        + "Merci de cliquer sur le lien suivant pour réinitialiser votre mot de passe :<br />"
                        + "<a href=\"http://localhost:8080/JMD/webresources/admin/resetPassword?pseudo="+ pseudo+ "&code=" + randomString + "\">http://localhost:8080/JMD/webresources/admin/resetPassword?pseudo="+ pseudo+ "&code=" + randomString + "</a><br /><br />"
                        + "Cordialement,<br />L'équipe de JMD<br /><br />"
                        + "PS : Si vous n'êtes pas à l'origine de cette demande, merci de cliquer sur le lien suivant : <a href=\"http://localhost:8080/JMD/webresources/admin/cancelResetRequest?pseudo="+ pseudo + "\">http://localhost:8080/JMD/webresources/admin/cancelResetRequest?pseudo="+ pseudo+ "</a>";

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
    
    @Path("cancelResetRequest")
    public Response cancelResetRequest() {
        return Response.status(200).build();
    }
    
    @Path("resetPassword")
    public Response resetPassword() {
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