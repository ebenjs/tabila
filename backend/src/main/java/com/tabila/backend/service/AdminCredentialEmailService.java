package com.tabila.backend.service;

import com.tabila.backend.domain.Restaurant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@Service
public class AdminCredentialEmailService {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final String fromEmail;
    private final String backofficeUrl;

    public AdminCredentialEmailService(
            ObjectProvider<JavaMailSender> mailSenderProvider,
            @Value("${app.mail.from:no-reply@tabila.local}") String fromEmail,
            @Value("${app.backoffice.url:http://localhost:5174}") String backofficeUrl) {
        this.mailSenderProvider = mailSenderProvider;
        this.fromEmail = fromEmail;
        this.backofficeUrl = backofficeUrl;
    }

    public void sendRestaurantAdminCredentials(String targetEmail, Restaurant restaurant, String generatedPassword) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            throw new ResponseStatusException(
                    SERVICE_UNAVAILABLE,
                    "Service email non configure. Definissez spring.mail.host et les variables SMTP");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(targetEmail);
        message.setSubject("Vos acces administrateur Tabila");
        message.setText(buildBody(restaurant.getName(), targetEmail, generatedPassword));

        try {
            mailSender.send(message);
        } catch (MailException ex) {
            throw new ResponseStatusException(SERVICE_UNAVAILABLE, "Impossible d'envoyer l'email de credentials", ex);
        }
    }

    private String buildBody(String restaurantName, String email, String generatedPassword) {
        return "Bonjour,\n\n"
                + "Votre compte administrateur Tabila a ete cree pour le restaurant: " + restaurantName + ".\n\n"
                + "Email: " + email + "\n"
                + "Mot de passe temporaire: " + generatedPassword + "\n"
                + "Backoffice: " + backofficeUrl + "\n\n"
                + "Nous vous recommandons de changer votre mot de passe apres la premiere connexion.\n\n"
                + "Cordialement,\n"
                + "Equipe Tabila";
    }
}
