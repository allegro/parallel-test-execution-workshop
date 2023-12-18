package pl.allegro.tech.workshops.testsparallelexecution.email.messagebroker

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.util.TestSocketUtils

trait HermesMockPortSupport {

    @DynamicPropertySource
    static void configurePort(DynamicPropertyRegistry registry) {
        int port = TestSocketUtils.findAvailableTcpPort()
        registry.add("hermes-mock.port", () -> port)
    }
}
