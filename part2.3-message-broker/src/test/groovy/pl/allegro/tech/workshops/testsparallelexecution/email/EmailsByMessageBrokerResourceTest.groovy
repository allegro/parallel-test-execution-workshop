package pl.allegro.tech.workshops.testsparallelexecution.email

import org.junit.Rule
import pl.allegro.tech.hermes.mock.HermesMockRule
import pl.allegro.tech.workshops.testsparallelexecution.BaseResourceTest

import static java.time.Duration.ofMillis
import static org.springframework.http.HttpStatus.*
import static pl.allegro.tech.hermes.mock.exchange.Response.Builder.aResponse

class EmailsByMessageBrokerResourceTest extends BaseResourceTest {

    @Rule
    HermesMockRule hermesMock = new HermesMockRule(8089)

    private String subject = "New workshops!"

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

        when:
        def result = restClient.post("/emails", email, Email)

        then:
        result.statusCode == BAD_REQUEST
        hermesMock.expect().jsonMessagesOnTopicAs('pl.allegro.tech.workshops.testsparallelexecution.email', 0, EmailServiceEvent.class)

        where:
        sender << [null, '', ' ']
    }

    def "handle email service errors"() {
        def email = Email.of(subject, "from@example.com", "to@example.com")
        hermesMock.define().jsonTopic('pl.allegro.tech.workshops.testsparallelexecution.email', errorResponse)

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
