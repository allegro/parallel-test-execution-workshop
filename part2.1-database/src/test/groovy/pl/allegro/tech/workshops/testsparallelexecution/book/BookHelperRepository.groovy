package pl.allegro.tech.workshops.testsparallelexecution.book

import org.springframework.data.repository.CrudRepository
import pl.allegro.tech.workshops.testsparallelexecution.books.Book

interface BookHelperRepository extends CrudRepository<Book, String> {
    long countByTitle(String title)
}
