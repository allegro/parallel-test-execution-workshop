package pl.allegro.tech.workshops.testsparallelexecution.email.rest;

public interface EmailClient {
    void send(Email email);

    Email read(String id);
}
