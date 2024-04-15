package pl.allegro.tech.workshops.testsparallelexecution.email.rest

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.stubbing.Scenario
import org.springframework.http.ProblemDetail
import pl.allegro.tech.workshops.testsparallelexecution.BaseTestWithRest
import spock.lang.Shared

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import static com.github.tomakehurst.wiremock.http.Fault.CONNECTION_RESET_BY_PEER
import static com.github.tomakehurst.wiremock.http.Fault.EMPTY_RESPONSE
import static org.springframework.http.HttpHeaders.CONTENT_TYPE
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

class GetEmailResourceTest extends BaseTestWithRest {

    private static final String VALID_BODY = """{"subject": "test subject", "sender": "from@example.com", "recipient": "to@example.com"}"""

    @Shared
    WireMockServer wiremockServer

    private String emailId = "2"

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

    def "get e-mail"() {
        given:
        wiremockServer.stubFor(get(urlEqualTo("/external-api-service/emails/$emailId"))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody("""{"subject": "test subject", "sender": "from@example.com", "recipient": "to@example.com"}""")
                )
        )

        when:
        def result = restClient.get("/emails/$emailId", EmailRequest)

        then:
        result.statusCode == OK
        result.body == EmailRequest.of("test subject", "from@example.com", "to@example.com")
    }

    def "handle email service errors (status=#errorResponse.status, fault=#errorResponse.fault, delay=#errorResponse.fixedDelayMilliseconds)"() {
        given:
        wiremockServer.stubFor(get(urlEqualTo("/external-api-service/emails/$emailId"))
                .willReturn(errorResponse)
        )

        when:
        def result = restClient.get("/emails/$emailId", ProblemDetail)

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
                .withStatus(200)
                .withBody(VALID_BODY)                             || "Read timed out"
    }

    def "retry email fetching after error response (status=#errorResponse.status, fault=#errorResponse.fault, delay=#errorResponse.fixedDelayMilliseconds)"() {
        given:
        wiremockServer.stubFor(get(urlEqualTo("/external-api-service/emails/$emailId"))
                .willReturn(errorResponse)
                .inScenario("retry scenario")
                .whenScenarioStateIs(Scenario.STARTED)
                .willSetStateTo('after error')
        )
        wiremockServer.stubFor(get(urlEqualTo("/external-api-service/emails/$emailId"))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody("""{"subject": "test subject", "sender": "from@example.com", "recipient": "to@example.com"}""")
                )
                .inScenario("retry scenario")
                .whenScenarioStateIs('after error')
                .willSetStateTo('after ok')
        )

        when:
        def result = restClient.get("/emails/$emailId", EmailRequest)

        then:
        result.statusCode == OK
        result.body == EmailRequest.of("test subject", "from@example.com", "to@example.com")

        where:
        errorResponse << [
                aResponse().withStatus(400),
                aResponse().withStatus(500),
                aResponse().withFault(EMPTY_RESPONSE),
                aResponse().withFault(CONNECTION_RESET_BY_PEER),
                aResponse().withFixedDelay(1000)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(VALID_BODY)
        ]
    }

}
