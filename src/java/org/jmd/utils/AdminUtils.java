package org.jmd.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Properties;
import java.util.Random;
import java.util.logging.*;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.jmd.Constantes;
import org.jmd.service.AdminService;
import org.jmd.service.DiplomeService;

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
            Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Méthode permettant de notifier par mail les administrateurs suivant l'année
     * spécifiée en argument lors d'une modification de celle-ci.
     * 
     * @param pseudo Le pseudo de l'administrateur ayant fait la modification.
     * @param idAnnee L'identificant de l'année modifiée.
     */
    public static void notify(String pseudo, int idAnnee) {
        Connection connexion = null;
        Statement stmt = null;
        ResultSet results = null;

        try {
            connexion = SQLUtils.getConnexion();            
            stmt = connexion.createStatement();
            
            results = stmt.executeQuery("SELECT * "
                    + "FROM DIPLOME, ANNEE, ADMINISTRATEUR, ADMIN_FOLLOWER "
                    + "WHERE (DIPLOME.ID = ANNEE.ID_DIPLOME) "
                        + "AND (ANNEE.ID = ADMIN_FOLLOWER.ID_ANNEE) "
                        + "AND (ADMINISTRATEUR.ID = ADMIN_FOLLOWER.ID_ADMIN) "
                        + "AND (ANNEE.ID = " + idAnnee + ");");

            while (results.next()) {
                if (results.getInt("ANNEE.ID") == idAnnee) {
                    String sujet = "JMD - Modification d'une année suivi";
                    
                    String text = "Bonjour<br />"
                            + "Une modification a été effectuée par '" + pseudo + "' sur l'année suivante :<br />"
                            + "- Diplôme : '" + results.getString("DIPLOME.NOM") + "'.<br />"
                            + "- Année : '" + results.getString("ANNEE.NOM") + "'."
                            + "<br /><br />"
                            + "Cordialement,<br />"
                            + "L'équipe de JMD,";
                    
                    sendMail(sujet, text, results.getString("ADMINISTRATEUR.EMAIL"));
                    
                    break;
                }
            }

            results.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
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
            Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
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

                if ((timestampACheck - timestamp) < Constantes.TIMESTAMP_LIMIT) {
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
            Logger.getLogger(DiplomeService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
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
        } catch (SQLException ex) {
            Logger.getLogger(AdminUtils.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
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
     * Méthode permettant d'hasher une chaîne de caractères en SHA-256.
     *
     * @param passwordToHash La chaîne à hasher.
     *
     * @return La chaîne hashée en SHA-256.
     */
    public static String sha256(String passwordToHash) {
        String generatedPassword = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < bytes.length; i++) {
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
}
