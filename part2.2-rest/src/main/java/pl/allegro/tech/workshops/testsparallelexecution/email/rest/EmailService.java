package pl.allegro.tech.workshops.testsparallelexecution.email.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailService {

    private final EmailClient emailClient;

    @Autowired
    public EmailService(EmailClient emailClient) {
        this.emailClient = emailClient;
    }
    public void sendEmail(EmailRequest email) {
        emailClient.send(email);
    }
}
