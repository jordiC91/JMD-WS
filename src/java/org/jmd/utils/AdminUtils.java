package org.jmd.utils;

import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.logging.*;
import javapns.Push;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.notification.PushedNotification;
import javax.mail.*;
import javax.mail.Transport;
import javax.mail.internet.*;
import org.jmd.Constantes;
import org.jmd.service.*;

/**
 * Classe comprenant des méthodes communes aux admins. Exemple : vérification de
 * token et de timestamp.
 *
 * @author jordi charpentier - yoann vanhoeserlande
 */
public class AdminUtils {
    
    /**
     * Constructeur privé de la classe.<br />
     * Empèche son instanciation.
     */
    private AdminUtils() {
        
    }
    
    /**
     * Méthode permettant d'envoyer un mail.
     *
     * @param subject Le sujet du mai.
     * @param text Le contenu du mail.
     * @param to Le destinataire du mail.
     */
    public static void sendMail(String subject, String text, String to) {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.user", Constantes.EMAIL_JMD);
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
            Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public static void notifyAllDevices(String message, String pseudo, int idAnnee){
        
    }
    
    /**
     * Méthode permettant de notifier par mail les administrateurs suivant l'année
     * spécifiée en argument lors d'une modification de celle-ci.
     *
     * @param message Le message à envoyer.
     * @param idAnnee L'identificant de l'année modifiée.
     * @param exceptIdAdmin L'id de l'administrateur expéditeur (donc non destinataire).
     */
    public static void notifyMail(String message, int idAnnee, int exceptIdAdmin) {
        Connection connexion = null;
        Statement stmt = null;
        ResultSet results = null;
        
        try {
            connexion = SQLUtils.getConnexion();
            stmt = connexion.createStatement();
            
            results = stmt.executeQuery("SELECT * "
                    + "FROM ADMINISTRATEUR, ADMIN_FOLLOWER "
                    + "WHERE (ADMINISTRATEUR.ID = ADMIN_FOLLOWER.ID_ADMIN) "
                    + "AND (ADMINISTRATEUR.ACCEPT_MAIL = 1)"
                    + "AND (ADMIN_FOLLOWER.ID_ANNEE = " + idAnnee + ") "
                    + "AND (ADMIN_FOLLOWER.ID_ADMIN <> "+ exceptIdAdmin +");");
            
            while (results.next()) {
                sendMail("JMD - Modification d'une année suivie",
                        "Bonjour,<br /><br />"
                                + message + "<br />"
                                + "<br /><br />"
                                + "Cordialement,<br />"
                                + "L'équipe de JMD,",
                        results.getString("ADMINISTRATEUR.EMAIL"));
            }
            
            results.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        finally {
            if (results != null) {
                try {
                    results.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if (connexion != null) {
                try {
                    connexion.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    /**
     * Méthode permettant de notifier par une notification iOS les administrateurs
     * suivant l'année spécifiée en argument lors d'une modification de celle-ci.
     *
     * @param message Le message à envoyer.
     * @param idAnnee L'identificant de l'année modifiée.
     * @param exceptIdAdmin L'id de l'administrateur expéditeur (donc non destinataire).
     */
    public static void notifyiOS(String message, int idAnnee, int exceptIdAdmin){
        Connection connexion = null;
        Statement stmt = null;
        ResultSet results = null;
        
        try {
            connexion = SQLUtils.getConnexion();
            stmt = connexion.createStatement();
            ArrayList<String> tokenList = new ArrayList<>();
            
            results = stmt.executeQuery("SELECT ADMIN_IOS.TOKEN "
                    + "FROM ADMIN_FOLLOWER, ADMIN_IOS "
                    + "WHERE (ADMIN_FOLLOWER.ID_ADMIN = ADMIN_IOS.ID_ADMIN) "
                    + "AND (ADMIN_FOLLOWER.ID_ANNEE = "+idAnnee+") "
                    + "AND (ADMIN_FOLLOWER.ID_ADMIN <> "+exceptIdAdmin+");");
            
            while (results.next()) {
                tokenList.add(results.getString("ADMIN_IOS.TOKEN"));
            }
            results.close();
            stmt.close();
            
            String [] devices = tokenList.toArray(new String[tokenList.size()]);
            if (tokenList.size() > 0){
                List<PushedNotification> notifications = Push.alert(message, IOSUtils.pathToCert, IOSUtils.pwdCert, IOSUtils.isProduction, devices);
                
                for (PushedNotification notification : notifications) {
                    if (!notification.isSuccessful()) {
                        String invalidToken = notification.getDevice().getToken();
                        stmt = connexion.createStatement();
                        stmt.executeUpdate("DELETE FROM ADMIN_IOS WHERE (TOKEN  = '" + invalidToken + "');");
                        stmt.close();
                    }
                }
            }
            
            
        } catch (CommunicationException | KeystoreException | SQLException ex) {
            Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            if (results != null) {
                try {
                    results.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (connexion != null) {
                try {
                    connexion.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    /**
     * Méthode permettant de notifier par une notification Android les administrateurs
     * suivant l'année spécifiée en argument lors d'une modification de celle-ci.
     *
     * @param message Le message à envoyer.
     * @param idAnnee L'identificant de l'année modifiée.
     * @param exceptIdAdmin L'id de l'administrateur expéditeur (donc non destinataire).
     */
    public static void notifyAndroid(String message, int idAnnee, int exceptIdAdmin) {
        Connection connexion = null;
        Statement stmt = null;
        ResultSet results = null;
        
        try {
            Sender sender = new Sender("AIzaSyCpawXxdzAN8rlfReinli1SZQSd-Hu70P4");
            connexion = SQLUtils.getConnexion();
            stmt = connexion.createStatement();
            ArrayList<String> devicesList = new ArrayList<>();
            
            // Création du message à envoyer.
            results = stmt.executeQuery("SELECT ADMIN_ANDROID.GCM_ID " +
                    "FROM ADMIN_FOLLOWER, ADMIN_ANDROID " +
                    "WHERE (ADMIN_FOLLOWER.ID_ADMIN = ADMIN_ANDROID.ID_ADMIN) " +
                    "AND (ADMIN_FOLLOWER.ID_ANNEE = "  + idAnnee + ") "+
                    "AND (ADMIN_FOLLOWER.ID_ADMIN <> " + exceptIdAdmin +");");
            
            while (results.next()) {
                devicesList.add(results.getString("GCM_ID"));
            }
            
            results.close();
            stmt.close();
            
            // Création du message GCM.
            if (devicesList.size() > 0){
                com.google.android.gcm.server.Message notification = new com.google.android.gcm.server.Message.Builder()
                        .collapseKey("1")
                        .timeToLive(3)
                        .delayWhileIdle(true)
                        .addData("message", message)
                        .build();
                
                MulticastResult result = sender.send(notification, devicesList, 1);
                
                sender.send(notification, devicesList, 1);          
                
                if (result.getResults() == null) {
                    Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, "Erreur : " + result.getFailure());
                }
            }
        } catch (SQLException | IOException ex) {
            Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            if (results != null) {
                try {
                    results.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (connexion != null) {
                try {
                    connexion.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    /**
     * Méthode permettant de vérifier le token envoyé pour un utilisateur afin
     * de voir s'il est connecté ou non.
     *
     * @param pseudo Le pseudo donné lors de la requête.
     * @param tokenACheck Le token à verifier.
     *
     * @return <b>true</b> si le token de l'utilisateur est valide.
     * <b>false</b> sinon.
     */
    public static boolean checkToken(String pseudo, String tokenACheck) {
        boolean isOK = false;
        
        if (tokenACheck != null) {
            Connection connexion = null;
            Statement stmt = null;
            ResultSet results = null;
            
            try {
                connexion = SQLUtils.getConnexion();
                stmt = connexion.createStatement();
                results = stmt.executeQuery("SELECT TOKEN "
                        + "FROM ADMINISTRATEUR "
                        + "WHERE (PSEUDO = '" + pseudo + "');");
                
                while (results.next()) {
                    String token = results.getString("TOKEN");
                    
                    if (token.equals(tokenACheck)) {
                        isOK = true;
                        break;
                    }
                }
                
                results.close();
                stmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            finally {
                if (results != null) {
                    try {
                        results.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (connexion != null) {
                    try {
                        connexion.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        
        return isOK;
    }
    
    /**
     * Méthode permettant de vérifier le timestamp d'un utilisateur afin
     * d'éviter les attaques par rejeu.
     *
     * @param pseudo Le pseudo donné lors de la requête.
     * @param timestampACheck Le timestamp à verifier.
     *
     * @return <b>true</b> si le timestamp de l'utilisateur est valide.
     * <b>false</b> sinon.
     */
    public static boolean checkTimestamp(String pseudo, long timestampACheck) {
        boolean isOK = false;
        
        if (timestampACheck != 0) {
            Connection connexion = null;
            Statement stmt = null;
            ResultSet results = null;
            
            try {
                connexion = SQLUtils.getConnexion();
                stmt = connexion.createStatement();
                results = stmt.executeQuery("SELECT TIMESTAMP_USER "
                        + "FROM ADMINISTRATEUR "
                        + "WHERE (PSEUDO = '" + pseudo + "');");
                
                while (results.next()) {
                    long timestamp = results.getLong("TIMESTAMP_USER");
                    
                    if (timestampACheck == timestamp) { // Si 2 timestamp identiques sont envoyés : attaque par rejeu.
                        isOK = false;
                        break;
                    } else if ((timestampACheck - timestamp) < Constantes.TIMESTAMP_LIMIT) { // S'il y a eu plus de 15min avec le dernier appel de services pour l'admin.
// Mise à jour du timestamp.
                        stmt.executeUpdate("UPDATE ADMINISTRATEUR SET TIMESTAMP_USER = " + timestampACheck + " WHERE PSEUDO = '" + pseudo + "';");
                        
                        isOK = true;
                        break;
                    } else {
                        logout(pseudo);
                    }
                }
                
                results.close();
                stmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            finally {
                if (results != null) {
                    try {
                        results.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                if (connexion != null) {
                    try {
                        connexion.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        
        return isOK;
    }
    
    /**
     * Méthode permettant de déconnecter un utilisateur (TOKEN à null et TIMESTAMP
     * à 0).
     *
     * @param pseudo Le pseudo de l'administrateur à déconnecter.
     */
    private static void logout(String pseudo) {
        Connection connexion = null;
        Statement stmt = null;
        
        try {
            connexion = SQLUtils.getConnexion();
            
            stmt = connexion.createStatement();
            stmt.executeUpdate("UPDATE ADMINISTRATEUR SET TOKEN = 'NULL' WHERE PSEUDO = '" + pseudo + "';");
            stmt.executeUpdate("UPDATE ADMINISTRATEUR SET TIMESTAMP_USER = '0' WHERE PSEUDO = '" + pseudo + "';");
            stmt.close();
            
            connexion.close();
        } catch (SQLException ex) {
            Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (connexion != null) {
                try {
                    connexion.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    /**
     * Méthode permettant de générer un code aléatoire (alphabet + 0123456789)
     * de 20 caractères.
     *
     * @return Le code aléatoire généré.
     */
    public static String generateRandomCode() {
        char[] chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        
        for (int i = 0; i < 20; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        
        return sb.toString();
    }
    
    public static int getIdAdmin(String pseudo){
        Connection connexion = null;
        Statement stmt = null;
        ResultSet results = null;
        int toReturn = 0;
        
        try {
            connexion = SQLUtils.getConnexion();
            stmt = connexion.createStatement();
            results = stmt.executeQuery("SELECT ID "
                    + "FROM ADMINISTRATEUR "
                    + "WHERE (PSEUDO = '" + pseudo + "');");
            
            while (results.next()) {
                toReturn = results.getInt("ID");
            }
            
            results.close();
            stmt.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        finally {
            if (results != null) {
                try {
                    results.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (connexion != null) {
                try {
                    connexion.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return toReturn;
    }
}