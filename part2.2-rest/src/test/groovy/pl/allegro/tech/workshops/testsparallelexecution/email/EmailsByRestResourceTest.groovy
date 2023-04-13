package pl.allegro.tech.workshops.testsparallelexecution.email

import com.github.tomakehurst.wiremock.WireMockServer
import pl.allegro.tech.workshops.testsparallelexecution.BaseResourceTest
import pl.allegro.tech.workshops.testsparallelexecution.support.FaultResponse
import pl.allegro.tech.workshops.testsparallelexecution.support.Request
import pl.allegro.tech.workshops.testsparallelexecution.support.Response
import spock.lang.Shared

import static com.github.tomakehurst.wiremock.http.Fault.CONNECTION_RESET_BY_PEER
import static com.github.tomakehurst.wiremock.http.Fault.EMPTY_RESPONSE
import static java.time.Duration.ofMillis
import static org.springframework.http.HttpStatus.*

class EmailsByRestResourceTest extends BaseResourceTest implements EmailServerStub {

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
        def email = Email.of(subject, "from@example.com", "to@example.com")
        stubPostJson(new Request(path: "/external-api-service/emails"), Response.OK)

        when:
        def result = restClient.post("/emails", email, Email)

        then:
        result.statusCode == OK
        verifyPostJson(new Request(path: "/external-api-service/emails"), new Response(body: [subject: subject, from: "from@example.com", to: "to@example.com"]))
    }

    def "do not sent email without sender"() {
        given:
        def email = Email.of(subject, sender, "to@example.com")
        stubPostJson(new Request(path: "/external-api-service/emails"), Response.OK)

        when:
        def result = restClient.post("/emails", email, Email)

        then:
        result.statusCode == BAD_REQUEST
        verifyNoPostJson(new Request(path: "/external-api-service/emails"))

        where:
        sender << [null, '', ' ']
    }

    def "handle email service errors"() {
        def email = Email.of(subject, "from@example.com", "to@example.com")
        stubPostJson(new Request(path: "/external-api-service/emails"), errorResponse)

        when:
        def result = restClient.post("/emails", email, Email)

        then:
        result.statusCode == INTERNAL_SERVER_ERROR

        where:
        errorResponse << [
                new Response(status: 400),
                new Response(status: 500),
                new FaultResponse(fault: EMPTY_RESPONSE),
                new FaultResponse(fault: CONNECTION_RESET_BY_PEER),
                new Response(delay: ofMillis(1000)),
        ]
    }

    def "retry email sending"() {
        def email = Email.of(subject, "from@example.com", "to@example.com")
        stubPostJson(new Request(path: "/external-api-service/emails"), [errorResponse, Response.OK])

        when:
        def result = restClient.post("/emails", email, Email)

        then:
        result.statusCode == OK

        where:
        errorResponse << [
                new Response(status: 400),
                new Response(status: 500),
                new FaultResponse(fault: EMPTY_RESPONSE),
                new FaultResponse(fault: CONNECTION_RESET_BY_PEER),
                new Response(delay: ofMillis(1000)),
        ]
    }
}
