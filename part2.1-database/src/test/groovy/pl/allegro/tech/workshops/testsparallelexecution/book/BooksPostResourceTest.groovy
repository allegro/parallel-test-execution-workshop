package pl.allegro.tech.workshops.testsparallelexecution.book

import org.springframework.http.ProblemDetail
import pl.allegro.tech.workshops.testsparallelexecution.books.Book

import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY

/**
 * Hints
 *
 * <p>Use other methods from {@link pl.allegro.tech.workshops.testsparallelexecution.support.DatabaseHelper}.
 */
class BooksPostResourceTest extends BaseBookResourceTest {

    private String title

    def setup() {
        title = "C++ for dummies"
    }

    def cleanup() {
        bookDatabaseHelper.removeAll()
    }

    def "create book"() {
        given:
        def book = Book.of(title, "Davis Stephen R.")

        when:
        def result = restClient.post("/books", book, Book)

        then:
        result.statusCode == OK
        with(result.body) {
            it.id() != null
            it.title() == this.title
            it.author() == "Davis Stephen R."
        }
        def documentWithCreatedBook = bookDatabaseHelper.findById(result.body.id())
        assert documentWithCreatedBook != null, 'created document was not found'
        with(documentWithCreatedBook) {
            it.id() == result.body.id()
            it.title() == this.title
            it.author() == "Davis Stephen R."
        }
    }

    def "return error on creating book with not-unique title"() {
        given:
        def otherBook = Book.of(title, "Davis Stephen R.")
        store(otherBook)
        def book = Book.of(title, "Davis Stephen R.")

        when:
        def result = restClient.post("/books", book, ProblemDetail)

        then:
        result.statusCode == UNPROCESSABLE_ENTITY
        result.body.title == 'Book with this title already exists'
    }

}
