package pl.allegro.tech.workshops.testsparallelexecution.book


import pl.allegro.tech.workshops.testsparallelexecution.BaseTestWithRestAndDatabase
import pl.allegro.tech.workshops.testsparallelexecution.books.Book

import static org.springframework.http.HttpStatus.OK

class BooksDeleteResourceTest extends BaseTestWithRestAndDatabase {

    private String title

    def setup() {
        title = "C++ for dummies"
    }

    def cleanup() {
        databaseHelper.removeAll(Book)
    }

    def "delete existing book"() {
        given:
        def book = Book.of(title, "Davis Stephen R.")
        def createdBook = store(book)

        when:
        def result = restClient.delete("/books/${createdBook.id()}")

        then:
        result.statusCode == OK
        databaseHelper.count(Book) == 0
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
        databaseHelper.count(Book) == 1
    }

    def "delete return not found for non-existent book"() {
        given:
        assert databaseHelper.count(Book) == 0

        when:
        def result = restClient.delete("/books/not-found-book-id")

        then:
        result.statusCode == OK
    }

    private Book store(Book book) {
        def response = restClient.post("/books", book, Book)
        assert response.statusCode == OK
        assert response.body != null
        response.body
    }

}
