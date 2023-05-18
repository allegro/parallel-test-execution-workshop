package pl.allegro.tech.workshops.testsparallelexecution

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoOperations
import pl.allegro.tech.workshops.testsparallelexecution.support.DatabaseHelper
import pl.allegro.tech.workshops.testsparallelexecution.support.MongoTestContainerSupport
import pl.allegro.tech.workshops.testsparallelexecution.support.generators.TestNameUniqueValueGenerator

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaseTestWithRestAndDatabase extends BaseTestWithRest implements MongoTestContainerSupport {

    @Autowired
    protected DatabaseHelper databaseHelper

    def generator = new TestNameUniqueValueGenerator(this.specificationContext)

    @org.springframework.context.annotation.Lazy
    @Configuration
    static class TestConfiguration {

        @Bean
        DatabaseHelper databaseHelper(MongoOperations mongoOperations) {
            new DatabaseHelper(mongoOperations: mongoOperations)
        }
    }

}
