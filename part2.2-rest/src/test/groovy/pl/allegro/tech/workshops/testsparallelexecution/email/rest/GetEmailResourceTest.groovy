package pl.allegro.tech.workshops.testsparallelexecution.email.rest

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.stubbing.Scenario
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.http.ProblemDetail
import pl.allegro.tech.workshops.testsparallelexecution.BaseTestWithRest

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import static com.github.tomakehurst.wiremock.http.Fault.CONNECTION_RESET_BY_PEER
import static com.github.tomakehurst.wiremock.http.Fault.EMPTY_RESPONSE
import static org.springframework.http.HttpHeaders.CONTENT_TYPE
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@Import(WiremockConfig)
class GetEmailResourceTest extends BaseTestWithRest implements WiremockPortSupport {

    @Autowired
    private WireMockServer wiremockServer

    private static final String VALID_BODY = """{"subject": "test subject", "sender": "from@example.com", "recipient": "to@example.com"}"""

    private String emailId = "2"

    def cleanup() {
        wiremockServer.resetAll()
        wiremockServer.resetScenarios()
    }

    def "get e-mail"() {
        given:
        wiremockServer.stubFor(get(urlPathMatching("/external-api-service/emails/.*"))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody("""{"subject": "test subject", "sender": "from@example.com", "recipient": "to@example.com"}""")
                )
        )

        when:
        def result = restClient.get("/emails/$emailId", Email)

        then:
        result.statusCode == OK
        result.body == Email.of("test subject", "from@example.com", "to@example.com")
    }

    def "handle email service errors (status=#errorResponse.status, fault=#errorResponse.fault, delay=#errorResponse.fixedDelayMilliseconds)"() {
        given:
        wiremockServer.stubFor(get(urlPathMatching("/external-api-service/emails/.*"))
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
        aResponse().withStatus(500)                               || "500 Internal Server Error"
        aResponse().withFault(EMPTY_RESPONSE)                     || "EOF reached while reading"
        aResponse().withFault(CONNECTION_RESET_BY_PEER)           || "Connection reset"
        aResponse().withFixedDelay(600)
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .withStatus(200)
                .withBody(VALID_BODY)                             || "Request timed out"
    }

    def "retry email fetching after error response (status=#errorResponse.status, fault=#errorResponse.fault, delay=#errorResponse.fixedDelayMilliseconds)"() {
        given:
        wiremockServer.stubFor(get(urlPathMatching("/external-api-service/emails/.*"))
                .willReturn(errorResponse)
                .inScenario("retry scenario")
                .whenScenarioStateIs(Scenario.STARTED)
                .willSetStateTo('after error')
        )
        wiremockServer.stubFor(get(urlPathMatching("/external-api-service/emails/.*"))
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
        def result = restClient.get("/emails/$emailId", Email)

        then:
        result.statusCode == OK
        result.body == Email.of("test subject", "from@example.com", "to@example.com")

        where:
        errorResponse << [
                aResponse().withStatus(400),
                aResponse().withStatus(500),
                aResponse().withFault(EMPTY_RESPONSE),
                aResponse().withFault(CONNECTION_RESET_BY_PEER),
                aResponse().withFixedDelay(600)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(VALID_BODY)
        ]
    }

}
