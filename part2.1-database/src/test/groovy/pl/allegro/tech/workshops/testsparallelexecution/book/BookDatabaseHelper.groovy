package pl.allegro.tech.workshops.testsparallelexecution.book

import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import pl.allegro.tech.workshops.testsparallelexecution.books.Book


class BookDatabaseHelper {

    MongoOperations mongoOperations

    /**
     * Use {@link BookDatabaseHelper#countByTitle(java.lang.String)} or {@link BookDatabaseHelper#exists(java.lang.Object)}  instead.
     */
    int count() {
        mongoOperations.count(new Query(), Book)
    }

    int countByTitle(String value) {
        mongoOperations.count(Query.query(Criteria.where("title").is(value)), Book)
    }

    boolean exists(Object id) {
        mongoOperations.findById(id, Book) != null
    }

    /**
     * Usually not for use in parallel execution.
     */
    void removeAll() {
        mongoOperations.findAllAndRemove(new Query(), Book)
    }

    Book findById(Object id) {
        mongoOperations.findById(id, Book)
    }
}
