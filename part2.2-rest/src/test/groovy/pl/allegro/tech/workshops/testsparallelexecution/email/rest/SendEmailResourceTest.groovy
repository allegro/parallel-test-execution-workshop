package pl.allegro.tech.workshops.testsparallelexecution.email.rest

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.stubbing.Scenario
import org.springframework.http.ProblemDetail
import pl.allegro.tech.workshops.testsparallelexecution.BaseTestWithRest
import spock.lang.Shared

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo
import static com.github.tomakehurst.wiremock.client.WireMock.post
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import static com.github.tomakehurst.wiremock.http.Fault.CONNECTION_RESET_BY_PEER
import static com.github.tomakehurst.wiremock.http.Fault.EMPTY_RESPONSE
import static org.springframework.http.HttpHeaders.ACCEPT
import static org.springframework.http.HttpHeaders.CONTENT_TYPE
import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

/**
 * Hints:
 *
 * <p>{@link com.github.tomakehurst.wiremock.client.MappingBuilder#withRequestBody(com.github.tomakehurst.wiremock.matching.ContentPattern <?>)},
 * {@link com.github.tomakehurst.wiremock.client.WireMock#matchingJsonPath(String, com.github.tomakehurst.wiremock.matching.StringValuePattern)}
 * and {@link com.github.tomakehurst.wiremock.client.WireMock#equalTo(java.lang.String)} (<a href="https://wiremock.org/docs/request-matching/#nested-value-matching">docs</a>)
 * can be used to match request body in stubs and in verifications.
 * <p>Sample usage: <code>.withRequestBody(matchingJsonPath('$.my-json-path', equalTo(...)))</code>
 *
 * <p>An example payload send to /external-api-service/emails resource:
 * <pre>
 {
 "subject": "New workshops!",
 "sender": "from@example.com",
 "recipient": "to@example.com"
 }
 </pre>
 */
class SendEmailResourceTest extends BaseTestWithRest {

    @Shared
    WireMockServer wiremockServer

    private String subject = "New workshops!"

    def setupSpec() {
        wiremockServer = new WireMockServer(8099)
        wiremockServer.start()
    }

    def cleanupSpec() {
        wiremockServer.stop()
    }

    def cleanup() {
        wiremockServer.resetAll()
        wiremockServer.resetScenarios()
    }

    def "send e-mail"() {
        given:
        def email = EmailRequest.of(subject, "from@example.com", "to@example.com")
        wiremockServer.stubFor(post(urlEqualTo("/external-api-service/emails"))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withStatus(200)
                )
        )

        when:
        def result = restClient.post("/emails", email, String)

        then:
        result.statusCode == OK
        wiremockServer.verify(1, postRequestedFor(urlEqualTo("/external-api-service/emails"))
                .withHeader(ACCEPT, equalTo("application/json, application/*+json"))
        )
    }

    def "do not sent email without sender"() {
        given:
        def email = EmailRequest.of(subject, sender, "to@example.com")
        wiremockServer.stubFor(post(urlEqualTo("/external-api-service/emails"))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withStatus(200)
                )
        )
        // sleep to simulate long response
        sleep 1000

        when:
        def result = restClient.post("/emails", email, ProblemDetail)

        then:
        result.statusCode == BAD_REQUEST
        wiremockServer.verify(0, postRequestedFor(urlEqualTo("/external-api-service/emails"))
                .withHeader(ACCEPT, equalTo("application/json, application/*+json"))
        )

        where:
        sender << [null, '', ' ']
    }

    def "handle email service errors (status=#errorResponse.status, fault=#errorResponse.fault, delay=#errorResponse.fixedDelayMilliseconds)"() {
        given:
        def email = EmailRequest.of(subject, "from@example.com", "to@example.com")
        wiremockServer.stubFor(post(urlEqualTo("/external-api-service/emails"))
                .willReturn(errorResponse)
        )

        when:
        def result = restClient.post("/emails", email, ProblemDetail)

        then:
        result.statusCode == INTERNAL_SERVER_ERROR
        result.body.detail.contains expectedDetail

        where:
        errorResponse                                             || expectedDetail
        aResponse().withStatus(400)                               || "400 Bad Request"
        aResponse().withStatus(500)                               || "500 Server Error"
        aResponse().withFault(EMPTY_RESPONSE)                     || "Unexpected end of file from server"
        aResponse().withFault(CONNECTION_RESET_BY_PEER)           || "Connection reset"
        aResponse().withFixedDelay(1000)
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .withStatus(200)                                  || "Read timed out"
    }

    def "retry email sending after error response (status=#errorResponse.status, fault=#errorResponse.fault, delay=#errorResponse.fixedDelayMilliseconds)"() {
        given:
        def email = EmailRequest.of(subject, "from@example.com", "to@example.com")
        wiremockServer.stubFor(post(urlEqualTo("/external-api-service/emails"))
                .willReturn(errorResponse)
                .inScenario("retry scenario")
                .whenScenarioStateIs(Scenario.STARTED)
                .willSetStateTo('after error')
        )
        wiremockServer.stubFor(post(urlEqualTo("/external-api-service/emails"))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withStatus(200)
                )
                .inScenario("retry scenario")
                .whenScenarioStateIs('after error')
                .willSetStateTo('after ok')
        )

        when:
        def result = restClient.post("/emails", email, String)

        then:
        result.statusCode == OK

        where:
        errorResponse << [
                aResponse().withStatus(400),
                aResponse().withStatus(500),
                aResponse().withFault(EMPTY_RESPONSE),
                aResponse().withFault(CONNECTION_RESET_BY_PEER),
                aResponse().withFixedDelay(1000)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withStatus(200)
        ]
    }
}
