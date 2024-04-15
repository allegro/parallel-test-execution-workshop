package pl.allegro.tech.workshops.testsparallelexecution.email.rest

import com.github.tomakehurst.wiremock.WireMockServer
import pl.allegro.tech.workshops.testsparallelexecution.BaseTestWithRest
import spock.lang.Shared

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import static org.springframework.http.HttpHeaders.CONTENT_TYPE
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

class GetEmailResourceTest extends BaseTestWithRest {

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
    }

    def "get e-mail"() {
        given:
        wiremockServer.stubFor(get(urlEqualTo("/external-api-service/emails/$emailId"))
                .willReturn(aResponse()
                        .withBody("""{"subject": "test subject", "sender": "from@example.com", "recipient": "to@example.com"}""")
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withStatus(200)
                )
        )

        when:
        def result = restClient.get("/emails/$emailId", EmailRequest)

        then:
        result.statusCode == OK
        result.body == EmailRequest.of("test subject", "from@example.com", "to@example.com")
    }

}
