package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.demo.model.Commande;
import com.example.demo.model.OrdreMission;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String from;

    @Value("${admin.email}") // à définir dans application.properties
    private String adminEmail;

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("tonemail@gmail.com"); // Doit être le même que dans application.properties
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
 // ✅ Méthode spécifique pour ordre de mission
    public void sendOrdreMissionEmailToAdmin(OrdreMission ordre) {
        String subject = "Nouvelle demande d'ordre de mission";

        String immatriculation = (ordre.getVehicule() != null && ordre.getVehicule().getImmatriculation() != null)
                ? ordre.getVehicule().getImmatriculation()
                : "non spécifiée";

        String text = "Une nouvelle demande a été soumise par " +
                ordre.getUtilisateur().getNom() + " (" + ordre.getUtilisateur().getEmail() + ")\n\n" +
                "Objet : " + ordre.getObjetMission() + "\n" +
                "Départ : " + ordre.getDestination() + "\n" +
                "Arrivée : " + ordre.getMotif() + "\n" +
                "Date départ : " + ordre.getDateDepart() + "\n" +
                "Date retour : " + ordre.getDateRetour() + "\n" +
                "Véhicule : " + immatriculation;

        sendEmail(adminEmail, subject, text);
    }
    // Méthode pour envoyer la commande au fournisseur
    public void sendCommandeEmail(Commande commande) {
        if (commande.getFournisseur() == null || commande.getFournisseur().getContact() == null) {
            System.out.println("⚠️ Aucun contact email pour le fournisseur !");
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(commande.getFournisseur().getContact());
        message.setSubject("Nouvelle commande #" + commande.getNumeroCommande());
        message.setText("Bonjour,\n\n" +
                "Une nouvelle commande a été générée pour vos articles.\n" +
                "Montant HT : " + commande.getMontant_ht() + "€\n" +
                "Montant TTC : " + commande.getMontant_ttc() + "€\n\n" +
                "Merci de la traiter rapidement.\n\nCordialement,\nL'équipe d'approvisionnement");

        mailSender.send(message);
        System.out.println("📧 Email envoyé au fournisseur : " + commande.getFournisseur().getContact());
    }
    public void sendEmailWithAttachment(String to, String subject, String text, byte[] attachmentData, String filename) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
            helper.addAttachment(filename, new ByteArrayResource(attachmentData));
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email avec pièce jointe", e);
        }
    }
    // ✅ Méthode pour envoyer un email HTML
    public void sendHtmlMail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // Le "true" indique que c'est du HTML
            mailSender.send(message);
            System.out.println("📧 Email HTML envoyé à : " + to);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email HTML", e);
        }
    }
}



