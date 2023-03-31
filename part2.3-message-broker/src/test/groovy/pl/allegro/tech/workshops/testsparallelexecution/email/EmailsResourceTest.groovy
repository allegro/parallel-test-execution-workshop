package pl.allegro.tech.workshops.testsparallelexecution.email

import org.junit.Rule
import pl.allegro.tech.hermes.mock.HermesMockRule
import pl.allegro.tech.workshops.testsparallelexecution.BaseResourceTest

import static java.time.Duration.ofMillis
import static org.springframework.http.HttpStatus.*
import static pl.allegro.tech.hermes.mock.exchange.Response.Builder.aResponse

class EmailsResourceTest extends BaseResourceTest {

//    @Shared
//    @ClassRule
    @Rule
    HermesMockRule hermesMock = new HermesMockRule(8089)

    private String subject = "New workshops!"

    def setup() {
//        hermesMock.resetReceivedRequest()
//        subject = "New workshops! ${generator.next()}"
    }

    def setupSpec() {
    }

    def cleanupSpec() {
    }

    def cleanup() {
    }

    def "send e-mail"() {
        given:
        def email = Email.of(subject, "from@example.com", "to@example.com")
        hermesMock.define().jsonTopic('pl.allegro.tech.workshops.testsparallelexecution.email')

        when:
        def result = restClient.post("/emails", email, Email)

        then:
        result.statusCode == OK
        hermesMock.expect().singleJsonMessageOnTopicAs('pl.allegro.tech.workshops.testsparallelexecution.email', EmailServiceEvent.class)
    }

    def "do not sent email without sender"() {
        given:
        def email = Email.of(subject, sender, "to@example.com")
        hermesMock.define().jsonTopic('pl.allegro.tech.workshops.testsparallelexecution.email')
//        stubPostJson("/external-api-service/emails", [subject: subject, from: sender, to: "to@example.com"], [sent: true])

        when:
        def result = restClient.post("/emails", email, Email)

        then:
        result.statusCode == BAD_REQUEST
        hermesMock.expect().jsonMessagesOnTopicAs('pl.allegro.tech.workshops.testsparallelexecution.email', 0, EmailServiceEvent.class)
//        verifyNoPostJson("/external-api-service/emails", [subject: subject, from: "from@example.com", to: "to@example.com"])

        where:
        sender << [null, '', ' ']
    }

    def "handle email service errors"() {
        def email = Email.of(subject, "from@example.com", "to@example.com")
        hermesMock.define().jsonTopic('pl.allegro.tech.workshops.testsparallelexecution.email', errorResponse)
//        stubPostJson("/external-api-service/emails", new Request(body: [subject: subject, from: "from@example.com", to: "to@example.com"]), errorResponse)

        when:
        def result = restClient.post("/emails", email, Email)

        then:
        result.statusCode == INTERNAL_SERVER_ERROR

        where:
        errorResponse << [
                aResponse().withStatusCode(400).build(),
                aResponse().withStatusCode(500).build(),
                aResponse().withFixedDelay(ofMillis(1000)).build()
                // HermesMock does not allow to define response with fault (similar to com.github.tomakehurst.wiremock.http.Fault)
                // Feel free to add such feature to https://hermes-pubsub.readthedocs.io/en/latest/user/hermes-mock/
        ]
    }

}
