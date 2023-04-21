package pl.allegro.tech.workshops.testsparallelexecution.email.rest;


import jakarta.validation.constraints.NotBlank;

public record EmailRequest(String subject, @NotBlank String sender, String recipient) {

    static public EmailRequest of(String subject, String sender, String recipient) {
        return new EmailRequest(subject, sender, recipient);
    }

}