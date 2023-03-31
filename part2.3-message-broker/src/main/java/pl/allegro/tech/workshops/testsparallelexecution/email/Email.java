package pl.allegro.tech.workshops.testsparallelexecution.email;


import jakarta.validation.constraints.NotBlank;

public record Email(String subject, @NotBlank String sender, String recipient) {

    static public Email of(String subject, String sender, String recipient) {
        return new Email(subject, sender, recipient);
    }

}