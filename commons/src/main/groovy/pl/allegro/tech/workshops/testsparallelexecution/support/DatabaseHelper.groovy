package pl.allegro.tech.workshops.testsparallelexecution.support

import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

abstract class DatabaseHelper<T> {

    abstract Class<T> getJavaType()

    MongoOperations mongoOperations

    /**
     * Use {@link pl.allegro.tech.workshops.testsparallelexecution.support.DatabaseHelper#countBy(String, String)} or {@link pl.allegro.tech.workshops.testsparallelexecution.support.DatabaseHelper#exists(Object)} instead.
     */
    int count() {
        mongoOperations.count(new Query(), getJavaType())
    }

    int countBy(String field, String value) {
        mongoOperations.count(Query.query(Criteria.where(field).is(value)), getJavaType())
    }

    boolean exists(Object id) {
        mongoOperations.findById(id, getJavaType()) != null
    }

    /**
     * Usually not for use in parallel execution.
     */
    void removeAll() {
        mongoOperations.findAllAndRemove(new Query(), getJavaType())
    }

    T findById(Object id) {
        mongoOperations.findById(id, getJavaType())
    }
}
