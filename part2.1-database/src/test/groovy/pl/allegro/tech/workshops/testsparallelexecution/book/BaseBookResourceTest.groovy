package pl.allegro.tech.workshops.testsparallelexecution.book

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoOperations
import pl.allegro.tech.workshops.testsparallelexecution.BaseTestWithRest
import pl.allegro.tech.workshops.testsparallelexecution.books.Book
import pl.allegro.tech.workshops.testsparallelexecution.support.MongoTestContainerSupport

import static org.springframework.http.HttpStatus.OK

class BaseBookResourceTest extends BaseTestWithRest implements MongoTestContainerSupport {

    @Autowired
    protected BookDatabaseHelper bookDatabaseHelper

    @org.springframework.context.annotation.Lazy
    @Configuration
    static class TestConfiguration {

        @Bean
        BookDatabaseHelper databaseHelper(MongoOperations mongoOperations) {
            new BookDatabaseHelper(mongoOperations: mongoOperations)
        }
    }

    protected Book store(Book book) {
        def response = restClient.post("/books", book, Book)
        assert response.statusCode == OK
        assert response.body != null
        response.body
    }

}
