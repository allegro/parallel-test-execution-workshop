package pl.allegro.tech.workshops.testsparallelexecution.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Component
public class RestEmailClient implements EmailClient {

    private final RestTemplate restTemplate;

    private final RetryTemplate retryTemplate;

    public RestEmailClient(RestTemplateBuilder builder, @Value("${application.services.email.url}") String serviceUrl) {
        this.restTemplate = builder
                .rootUri(serviceUrl)
                .setConnectTimeout(Duration.ofMillis(100))
                .setReadTimeout(Duration.ofMillis(100))
                .build();
        this.retryTemplate = new RetryTemplate();
        this.retryTemplate.setRetryPolicy(new MaxAttemptsRetryPolicy(2));
    }

    @Override
    public void send(Email email) {
        retryTemplate.execute(context -> restTemplate.postForEntity("/external-api-service/emails", EmailServiceRequest.from(email), Void.class));
    }

    private record EmailServiceRequest(String from, String to, String subject) {
        public static EmailServiceRequest from(Email email) {
            return new EmailServiceRequest(email.sender(), email.recipient(), email.subject());
        }
    }
}
