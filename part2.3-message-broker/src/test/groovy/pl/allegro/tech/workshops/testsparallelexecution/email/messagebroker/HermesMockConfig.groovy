package pl.allegro.tech.workshops.testsparallelexecution.email.messagebroker

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pl.allegro.tech.hermes.mock.HermesMock

@TestConfiguration
class HermesMockConfig {
    @Bean
    HermesMock getHermesMock(@Value('${hermes-mock.port}') int hermesMockPort) {
        return new HermesMock.Builder().withPort(hermesMockPort).build()
    }
}
