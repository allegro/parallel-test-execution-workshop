package pl.allegro.tech.workshops.testsparallelexecution.book


import pl.allegro.tech.workshops.testsparallelexecution.books.Book

import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.OK

class BooksDeleteResourceTest extends BaseBookResourceTest {

    private String title

    def setup() {
        title = "C++ for dummies"
    }

    def cleanup() {
        bookDatabaseHelper.removeAll()
    }

    def "delete existing book"() {
        given:
        def book = Book.of(title, "Davis Stephen R.")
        def createdBook = store(book)

        when:
        def result = restClient.delete("/books/${createdBook.id()}")

        then:
        result.statusCode == OK
        bookDatabaseHelper.count() == 0
    }

    def "delete does not remove other books"() {
        given:
        def book = Book.of(title, "Davis Stephen R.")
        def createdBook = store(book)
        def otherBook = Book.of("$title (other)", "Davis Stephen R.")
        def otherCreatedBook = store(otherBook)

        when:
        def result = restClient.delete("/books/${createdBook.id()}")

        then:
        result.statusCode == OK
        // the "other" book exists
        bookDatabaseHelper.count() == 1
    }

    def "delete return not found for non-existent book"() {
        given:
        def bookId = "not-found-book-id"
        assert bookDatabaseHelper.count() == 0

        when:
        def result = restClient.delete("/books/$bookId")

        then:
        result.statusCode == NOT_FOUND
    }

}
