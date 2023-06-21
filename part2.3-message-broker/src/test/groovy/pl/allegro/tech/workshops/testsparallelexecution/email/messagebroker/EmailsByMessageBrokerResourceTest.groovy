package pl.allegro.tech.workshops.testsparallelexecution.email.messagebroker

import org.junit.ClassRule
import org.springframework.beans.factory.annotation.Value
import pl.allegro.tech.hermes.mock.HermesMockRule
import pl.allegro.tech.workshops.testsparallelexecution.BaseTestWithRest
import spock.lang.Shared

import static java.time.Duration.ofMillis
import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import static org.springframework.http.HttpStatus.OK
import static pl.allegro.tech.hermes.mock.exchange.Response.Builder.aResponse

class EmailsByMessageBrokerResourceTest extends BaseTestWithRest {

    @Value('${application.services.message-broker.topic}')
    private String topic

    @ClassRule
    @Shared
    private HermesMockRule hermesMock = new HermesMockRule(8089)

    private String subject = "New workshops!"

    def cleanup() {
        hermesMock.resetReceivedRequest()
    }

    def "send e-mail"() {
        given:
        def email = EmailRequest.of(subject, "from@example.com", "to@example.com")
        /**
         * Hint:
         * - Replace with {@link pl.allegro.tech.hermes.mock.HermesMockDefine#jsonTopic(java.lang.String, pl.allegro.tech.hermes.mock.exchange.Response, java.lang.Class, java.util.function.Predicate)}
         * - Use {@link EmailServiceEvent} as a event class
         * - Define predicate using Groovy closure e.g. { it.subject() == subject }
         */
        hermesMock.define().jsonTopic(topic, aResponse().build())

        when:
        def result = restClient.post("/emails", email, EmailRequest)

        then:
        result.statusCode == OK
        /**
         * Hint:
         * - Replace with {@link pl.allegro.tech.hermes.mock.HermesMockExpect#jsonMessagesOnTopicAs(java.lang.String, int, java.lang.Class, java.util.function.Predicate)}
         */
        hermesMock.expect().singleJsonMessageOnTopicAs(topic, EmailServiceEvent)
    }

    def "do not sent email without sender"() {
        given:
        def email = EmailRequest.of(subject, sender, "to@example.com")
        hermesMock.define().jsonTopic(topic, aResponse().build())

        when:
        def result = restClient.post("/emails", email, EmailRequest)

        then:
        result.statusCode == BAD_REQUEST
        hermesMock.expect().jsonMessagesOnTopicAs(topic, 0, EmailServiceEvent)

        where:
        sender << [null, '', ' ']
    }

    def "handle email service errors"() {
        def email = EmailRequest.of(subject, "from@example.com", "to@example.com")
        hermesMock.define().jsonTopic(topic, errorResponse)

        when:
        def result = restClient.post("/emails", email, EmailRequest)

        then:
        result.statusCode == INTERNAL_SERVER_ERROR

        where:
        errorResponse << [
                aResponse().withStatusCode(400).build(),
                aResponse().withStatusCode(500).build(),
                aResponse().withFixedDelay(ofMillis(1000)).build()
        ]
    }

}
