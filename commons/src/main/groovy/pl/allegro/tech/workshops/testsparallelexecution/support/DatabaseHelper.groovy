package pl.allegro.tech.workshops.testsparallelexecution.support

import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

class DatabaseHelper {

    MongoOperations mongoOperations

    /**
     * Use {@link DatabaseHelper#countBy(java.lang.Class, java.lang.String, java.lang.String)} or {@link DatabaseHelper#exists(java.lang.Class, java.lang.Object)} instead.
     */
    int count(Class<?> entityClass) {
        mongoOperations.count(new Query(), entityClass)
    }

    int countBy(Class<?> entityClass, String field, String value) {
        mongoOperations.count(Query.query(Criteria.where(field).is(value)), entityClass)
    }

    <T> boolean exists(Class<T> entityClass, Object id) {
        mongoOperations.findById(id, entityClass) != null
    }

    /**
     * Usually not for use in parallel execution.
     */
    <T> void removeAll(Class<T> entityClass) {
        mongoOperations.findAllAndRemove(new Query(), entityClass)
    }

    <T> T findById(Object id, Class<T> entityClass) {
        mongoOperations.findById(id, entityClass)
    }
}
