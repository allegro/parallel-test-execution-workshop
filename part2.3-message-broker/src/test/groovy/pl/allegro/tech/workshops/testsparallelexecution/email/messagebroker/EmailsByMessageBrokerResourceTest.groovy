package pl.allegro.tech.workshops.testsparallelexecution.email.messagebroker

import org.junit.Rule
import org.springframework.beans.factory.annotation.Value
import pl.allegro.tech.hermes.mock.HermesMockRule
import pl.allegro.tech.workshops.testsparallelexecution.BaseTestWithRest

import static java.time.Duration.ofMillis
import static org.springframework.http.HttpStatus.*
import static pl.allegro.tech.hermes.mock.exchange.Response.Builder.aResponse

class EmailsByMessageBrokerResourceTest extends BaseTestWithRest {

    @Value('${application.services.message-broker.topic}')
    private String topic

    @Rule
    private HermesMockRule hermesMock = new HermesMockRule(8089)

    private String subject = "New workshops!"

    def "send e-mail"() {
        given:
        def email = EmailRequest.of(subject, "from@example.com", "to@example.com")
        hermesMock.define().jsonTopic(topic)

        when:
        def result = restClient.post("/emails", email, EmailRequest)

        then:
        result.statusCode == OK
        hermesMock.expect().singleJsonMessageOnTopicAs(topic, EmailServiceEvent)
    }

    def "do not sent email without sender"() {
        given:
        def email = EmailRequest.of(subject, sender, "to@example.com")
        hermesMock.define().jsonTopic(topic)

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
