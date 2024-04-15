package pl.allegro.tech.workshops.testsparallelexecution.email.rest;

public interface EmailClient {
    void send(EmailRequest email);

    EmailRequest read(String id);
}
