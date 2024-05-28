package pl.allegro.tech.workshops.testsparallelexecution.email.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
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
                .additionalInterceptors(new PassRequestIdHeaderRequestInterceptor())
                .build();
        this.retryTemplate = new RetryTemplate();
        this.retryTemplate.setRetryPolicy(new MaxAttemptsRetryPolicy(2));
    }

    @Override
    public void send(Email email) {
        retryTemplate.execute(context -> restTemplate.postForEntity("/external-api-service/emails", email, Void.class));
    }

    @Override
    public Email read(String id) {
        return retryTemplate.execute(context -> restTemplate.getForEntity("/external-api-service/emails/" + id, Email.class).getBody());
    }

    private static class PassRequestIdHeaderRequestInterceptor implements ClientHttpRequestInterceptor {

        private static final String REQUEST_ID_HEADER_NAME = "X-request-id";

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes instanceof ServletRequestAttributes) {
                String requestId = ((ServletRequestAttributes) requestAttributes).getRequest().getHeader(REQUEST_ID_HEADER_NAME);
                HttpHeaders headers = request.getHeaders();
                headers.set(REQUEST_ID_HEADER_NAME, requestId);
            }
            return execution.execute(request, body);
        }
    }
}
