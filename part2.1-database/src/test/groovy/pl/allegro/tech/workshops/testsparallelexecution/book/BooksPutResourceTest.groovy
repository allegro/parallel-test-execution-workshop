package pl.allegro.tech.workshops.testsparallelexecution.book

import org.springframework.http.ProblemDetail
import pl.allegro.tech.workshops.testsparallelexecution.BaseTestWithRestAndDatabase
import pl.allegro.tech.workshops.testsparallelexecution.books.Book

import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY

class BooksPutResourceTest extends BaseTestWithRestAndDatabase {

    private String title

    def setup() {
        title = "C++ for dummies"
    }

    def cleanup() {
        databaseHelper.removeAll(Book)
    }

    def "update book"() {
        given:
        def book = Book.of(title, "Davis Stephen R.")
        def createdBook = store(book)
        def updatedBook = new Book(createdBook.id(), "C++", "Davis Stephen R.")

        when:
        def result = restClient.put("/books/${createdBook.id()}", updatedBook, Book)

        then:
        result.statusCode == OK
        with(result.body) {
            it.id() == createdBook.id()
            it.title() == "C++"
            it.author() == "Davis Stephen R."
        }
        def documentWithUpdatedBook = databaseHelper.findById(result.body.id(), Book)
        assert documentWithUpdatedBook != null, 'updated document was not found'
        with(documentWithUpdatedBook) {
            it.id() == result.body.id()
            it.title() == "C++"
            it.author() == "Davis Stephen R."
        }
    }

    def "update does not create book when updated book does not exist"() {
        given:
        def updatedBook = Book.of(title, "Davis Stephen R.")
        assert databaseHelper.count(Book) == 0

        when:
        def result = restClient.put("/books/not-found-book-id", updatedBook, Book)

        then:
        result.statusCode == NOT_FOUND
        databaseHelper.count(Book) == 0
    }

    def "return error on updating book with not-unique title"() {
        given:
        def otherBook = Book.of(title, "Davis Stephen R.")
        store(otherBook)
        def book = Book.of("$title vol. 2", "Davis Stephen R.")
        def createdBook = store(book)
        def updatedBook = Book.of(title, "Davis Stephen R.")

        when:
        def result = restClient.put("/books/${createdBook.id()}", updatedBook, ProblemDetail)

        then:
        result.statusCode == UNPROCESSABLE_ENTITY
        result.body.title == 'Book with this title already exists'
    }

    private Book store(Book book) {
        def response = restClient.post("/books", book, Book)
        assert response.statusCode == OK
        assert response.body != null
        response.body
    }

}
