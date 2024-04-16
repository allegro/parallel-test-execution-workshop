package pl.allegro.tech.workshops.testsparallelexecution.email.rest

import com.github.tomakehurst.wiremock.WireMockServer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class WiremockConfig {

    @Bean
    WireMockServer getWiremockServer(@Value('${wiremock.port}') int wiremockPort) {
        return new WireMockServer(wiremockPort).tap {
            it.start()
        }
    }
}
