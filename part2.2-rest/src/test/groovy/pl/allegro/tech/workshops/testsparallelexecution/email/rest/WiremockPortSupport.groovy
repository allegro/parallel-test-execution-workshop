package pl.allegro.tech.workshops.testsparallelexecution.email.rest

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.util.TestSocketUtils

trait WiremockPortSupport {

    @DynamicPropertySource
    static void configurePort(DynamicPropertyRegistry registry) {
        int port = TestSocketUtils.findAvailableTcpPort()
        registry.add("wiremock.port", () -> port)
    }
}
