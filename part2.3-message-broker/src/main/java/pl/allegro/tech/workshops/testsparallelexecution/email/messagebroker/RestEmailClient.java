package pl.allegro.tech.workshops.testsparallelexecution.email.messagebroker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pl.allegro.tech.hermes.client.HermesClient;
import pl.allegro.tech.hermes.client.HermesClientBuilder;
import pl.allegro.tech.hermes.client.HermesResponse;
import pl.allegro.tech.hermes.client.webclient.WebClientHermesSender;
import reactor.netty.http.client.HttpClient;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.ExecutionException;

@Component
public class RestEmailClient implements EmailClient {

    private final HermesClient client;

    public RestEmailClient(@Value("${application.services.message-broker.url}") String serviceUrl) {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMillis(500));
        client = HermesClientBuilder.hermesClient(new WebClientHermesSender(WebClient.builder()
                        .clientConnector(new ReactorClientHttpConnector(httpClient))
                        .build()))
                .withURI(URI.create(serviceUrl))
                .withRetries(2)
                .build();
    }

    @Override
    public void send(Email email) {
        try {
            String message = new ObjectMapper().writeValueAsString(EmailServiceEvent.from(email));
            HermesResponse hermesResponse = client.publishJSON("pl.allegro.tech.workshops.testsparallelexecution.email", message).get();
            if (hermesResponse.isFailure()) {
                throw new RuntimeException(hermesResponse.getDebugLog());
            }
        } catch (JsonProcessingException | InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}

record EmailServiceEvent(String from, String to, String subject) {
    public static EmailServiceEvent from(Email email) {
        return new EmailServiceEvent(email.sender(), email.recipient(), email.subject());
    }
}
