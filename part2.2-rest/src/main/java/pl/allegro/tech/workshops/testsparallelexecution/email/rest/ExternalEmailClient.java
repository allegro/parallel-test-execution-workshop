package pl.allegro.tech.workshops.testsparallelexecution.email.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Component
public class ExternalEmailClient implements EmailClient {

    private final RestTemplate restTemplate;

    private final RetryTemplate retryTemplate;

    public ExternalEmailClient(RestTemplateBuilder builder, @Value("${application.services.email.url}") String serviceUrl) {
        this.restTemplate = builder
                .rootUri(serviceUrl)
                .setConnectTimeout(Duration.ofMillis(300))
                .setReadTimeout(Duration.ofMillis(500))
                .build();
        this.retryTemplate = new RetryTemplate();
        this.retryTemplate.setRetryPolicy(new MaxAttemptsRetryPolicy(2));
    }

    @Override
    public void send(EmailRequest email) {
        retryTemplate.execute(context -> restTemplate.postForEntity("/external-api-service/emails", email, Void.class));
    }

    @Override
    public EmailRequest read(String id) {
        return retryTemplate.execute(context -> restTemplate.getForEntity("/external-api-service/emails/" + id, EmailRequest.class).getBody());
    }

}
