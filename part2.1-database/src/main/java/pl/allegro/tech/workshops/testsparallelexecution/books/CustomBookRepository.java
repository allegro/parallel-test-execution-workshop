package pl.allegro.tech.workshops.testsparallelexecution.books;

import java.util.Optional;

public interface CustomBookRepository<T, ID> {
    Optional<T> updateById(T entity);

    T create(T entity);
}
