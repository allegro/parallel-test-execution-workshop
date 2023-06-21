package pl.allegro.tech.workshops.testsparallelexecution.email.rest

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.stubbing.Scenario
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

class EmailsByRestResourceTest extends BaseTestWithRest {

    @Shared
    WireMockServer wiremockServer

    private String subject = "New workshops!"

    def setupSpec() {
        wiremockServer = new WireMockServer(8089)
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
        def result = restClient.post("/emails", email, EmailRequest)

        then:
        result.statusCode == OK
        wiremockServer.verify(postRequestedFor(urlEqualTo("/external-api-service/emails"))
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

        when:
        def result = restClient.post("/emails", email, EmailRequest)

        then:
        result.statusCode == BAD_REQUEST
        wiremockServer.verify(0, postRequestedFor(urlEqualTo("/external-api-service/emails"))
                .withHeader(ACCEPT, equalTo("application/json, application/*+json"))
        )

        where:
        sender << [null, '', ' ']
    }

    def "handle email service errors"() {
        def email = EmailRequest.of(subject, "from@example.com", "to@example.com")
        wiremockServer.stubFor(post(urlEqualTo("/external-api-service/emails"))
                .willReturn(aResponse().tap(errorResponseBuilder))
        )

        when:
        def result = restClient.post("/emails", email, EmailRequest)

        then:
        result.statusCode == INTERNAL_SERVER_ERROR

        where:
        errorResponseBuilder << [
                { ResponseDefinitionBuilder builder -> builder.withStatus(400) },
                { ResponseDefinitionBuilder builder -> builder.withStatus(500) },
                { ResponseDefinitionBuilder builder -> builder.withFault(EMPTY_RESPONSE) },
                { ResponseDefinitionBuilder builder -> builder.withFault(CONNECTION_RESET_BY_PEER) },
                { ResponseDefinitionBuilder builder ->
                    builder.withFixedDelay(1000)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                            .withStatus(200)
                }
        ]
    }

    def "retry email sending"() {
        def email = EmailRequest.of(subject, "from@example.com", "to@example.com")
        wiremockServer.stubFor(post(urlEqualTo("/external-api-service/emails"))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withStatus(200)
                ).inScenario('retry scenario')
                .whenScenarioStateIs(Scenario.STARTED)
                .willSetStateTo('after error')
        )
        wiremockServer.stubFor(post(urlEqualTo("/external-api-service/emails"))
                .willReturn(aResponse().tap(errorResponseBuilder)).inScenario('retry scenario')
                .whenScenarioStateIs('after error')
                .willSetStateTo('after ok')
        )

        when:
        def result = restClient.post("/emails", email, EmailRequest)

        then:
        result.statusCode == OK

        where:
        errorResponseBuilder << [
                { ResponseDefinitionBuilder builder -> builder.withStatus(400) },
                { ResponseDefinitionBuilder builder -> builder.withStatus(500) },
                { ResponseDefinitionBuilder builder -> builder.withFault(EMPTY_RESPONSE) },
                { ResponseDefinitionBuilder builder -> builder.withFault(CONNECTION_RESET_BY_PEER) },
                { ResponseDefinitionBuilder builder ->
                    builder.withFixedDelay(1000)
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                            .withStatus(200)
                }
        ]
    }
}
