package pl.allegro.tech.workshops.testsparallelexecution.email.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponseException;

@Component
public class EmailService {

    private final EmailClient emailClient;

    @Autowired
    public EmailService(EmailClient emailClient) {
        this.emailClient = emailClient;
    }

    public void sendEmail(EmailRequest email) {
        try {
            emailClient.send(email);
        } catch (Exception e) {
            throw new ErrorResponseException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Email service communication error. " + e.getMessage()),
                    e);
        }
    }

    public EmailRequest getEmail(String id) {
        try {
            return emailClient.read(id);
        } catch (Exception e) {
            throw new ErrorResponseException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Email service communication error. " + e.getMessage()),
                    e);
        }

    }
}
