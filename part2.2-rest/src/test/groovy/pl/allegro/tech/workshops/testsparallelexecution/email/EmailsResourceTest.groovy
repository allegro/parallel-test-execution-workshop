package pl.allegro.tech.workshops.testsparallelexecution.email

import com.github.tomakehurst.wiremock.WireMockServer
import pl.allegro.tech.workshops.testsparallelexecution.BaseResourceTest
import pl.allegro.tech.workshops.testsparallelexecution.spock.RandomizedOrder
import pl.allegro.tech.workshops.testsparallelexecution.support.Request
import pl.allegro.tech.workshops.testsparallelexecution.support.Response
import spock.lang.Shared

import static com.github.tomakehurst.wiremock.http.Fault.CONNECTION_RESET_BY_PEER
import static com.github.tomakehurst.wiremock.http.Fault.EMPTY_RESPONSE
import static java.time.Duration.ofMillis
import static org.springframework.http.HttpStatus.*

@RandomizedOrder
class EmailsResourceTest extends BaseResourceTest implements EmailServerStub {

    @Shared
    WireMockServer wiremockServer

    private String subject = "New workshops!"

//    def setup() {
//        subject = "New workshops! ${generator.next()}"
//    }

    def setupSpec() {
        wiremockServer = new WireMockServer(8089)
        wiremockServer.start()
    }

    def cleanupSpec() {
        wiremockServer.stop()
    }

    def cleanup() {
//
        wiremockServer.resetAll()
//
        wiremockServer.resetScenarios()
    }

    def "send e-mail"() {
        given:
        def email = Email.of(subject, "from@example.com", "to@example.com")
        stubPostJson("/external-api-service/emails", [sent: true])
//        stubPostJson("/external-api-service/emails", [subject: subject, from: "from@example.com", to: "to@example.com"], [sent: true])

        when:
        def result = restClient.post("/emails", email, Email)

        then:
        result.statusCode == OK
        verifyPostJson("/external-api-service/emails", [subject: subject, from: "from@example.com", to: "to@example.com"])
    }

    def "do not sent email without sender"() {
        given:
        def email = Email.of(subject, sender, "to@example.com")
        stubPostJson("/external-api-service/emails", [sent: true])
//        stubPostJson("/external-api-service/emails", [subject: subject, from: sender, to: "to@example.com"], [sent: true])

        when:
        def result = restClient.post("/emails", email, Email)

        then:
        result.statusCode == BAD_REQUEST
        verifyNoPostJson("/external-api-service/emails")
//        verifyNoPostJson("/external-api-service/emails", [subject: subject, from: "from@example.com", to: "to@example.com"])

        where:
        sender << [null, '', ' ']
    }

    def "handle email service errors"() {
        def email = Email.of(subject, "from@example.com", "to@example.com")
        stubPostJson("/external-api-service/emails", errorResponse)
//        stubPostJson("/external-api-service/emails", new Request(body: [subject: subject, from: "from@example.com", to: "to@example.com"]), errorResponse)

        when:
        def result = restClient.post("/emails", email, Email)

        then:
        result.statusCode == INTERNAL_SERVER_ERROR

        where:
        errorResponse << [
                new Response(status: 400),
                new Response(status: 500),
                new Response(fault: EMPTY_RESPONSE),
                new Response(fault: CONNECTION_RESET_BY_PEER),
                new Response(delay: ofMillis(1000)),
        ]
    }

    def "retry email sending"() {
        def email = Email.of(subject, "from@example.com", "to@example.com")
        stubPostJson("/external-api-service/emails", [errorResponse, Response.OK])
//        stubPostJson("/external-api-service/emails",
//                new Request([scenario: new Request.RequestScenario(name: "scenario for $subject"),
//                             body    : [subject: subject, from: "from@example.com", to: "to@example.com"]]),
//                [errorResponse, Response.OK])

        when:
        def result = restClient.post("/emails", email, Email)

        then:
        result.statusCode == OK

        where:
        errorResponse << [
                new Response(status: 400),
                new Response(status: 500),
                new Response(fault: EMPTY_RESPONSE),
                new Response(fault: CONNECTION_RESET_BY_PEER),
                new Response(delay: ofMillis(1000)),
        ]
    }
}
