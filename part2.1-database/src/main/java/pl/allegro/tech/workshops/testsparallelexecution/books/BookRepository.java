package pl.allegro.tech.workshops.testsparallelexecution.books;

import org.springframework.data.repository.CrudRepository;

public interface BookRepository extends CrudRepository<Book, String>, CustomBookRepository<Book, String> {

    Integer deleteWithResultById(String id);
}
