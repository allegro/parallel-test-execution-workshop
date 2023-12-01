package pl.allegro.tech.workshops.testsparallelexecution.book


import pl.allegro.tech.workshops.testsparallelexecution.books.Book

import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.OK

/**
 * Hints
 *
 * <p>Use other methods from {@link pl.allegro.tech.workshops.testsparallelexecution.support.DatabaseHelper}.
 */
class BooksGetResourceTest extends BaseBookResourceTest {

    private String title

    def setup() {
        title = "C++ for dummies"
    }

    def cleanup() {
        bookDatabaseHelper.removeAll()
    }

    def "return not found when book does not exist"() {
        given:
        def bookId = "not-found-book-id"
        assert bookDatabaseHelper.count() == 0

        when:
        def result = restClient.get("/books/$bookId", Book)

        then:
        result.statusCode == NOT_FOUND
    }

    def "return existing book"() {
        given:
        def book = Book.of(title, "Davis Stephen R.")
        def createdBook = store(book)

        when:
        def result = restClient.get("/books/${createdBook.id()}", Book)

        then:
        result.statusCode == OK
        with(result.body) {
            it.id() == createdBook.id()
            it.title() == this.title
            it.author() == "Davis Stephen R."
        }
    }

}
