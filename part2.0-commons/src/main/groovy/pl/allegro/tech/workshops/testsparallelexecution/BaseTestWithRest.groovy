package pl.allegro.tech.workshops.testsparallelexecution

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.allegro.tech.workshops.testsparallelexecution.support.RestClient
import pl.allegro.tech.workshops.testsparallelexecution.support.generators.TestNameUniqueValueGenerator
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaseTestWithRest extends Specification {

    @Autowired
    protected RestClient restClient

    def generator = new TestNameUniqueValueGenerator(this.specificationContext)

    @org.springframework.context.annotation.Lazy
    @Configuration
    static class TestConfiguration {
        @LocalServerPort
        int port

        @Bean
        RestClient restClient(TestRestTemplate testRestTemplate) {
            new RestClient(url: "http://localhost:$port", restTemplate: testRestTemplate)
        }

    }

}
