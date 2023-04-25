package pl.allegro.tech.workshops.testsparallelexecution.book


import org.springframework.http.ProblemDetail
import pl.allegro.tech.workshops.testsparallelexecution.BaseResourceTest
import pl.allegro.tech.workshops.testsparallelexecution.books.Book

import static org.springframework.http.HttpStatus.*

class BooksResourceTest extends BaseResourceTest {

    private String title

    def setup() {
        title = "C++ for dummies"
    }

    def cleanup() {
        databaseHelper.removeAll(Book)
    }

    def "return not found when book does not exist"() {
        given:
        assert databaseHelper.count(Book) == 0

        when:
        def result = restClient.get("/books/not-found-book-id", Book)

        then:
        result.statusCode == NOT_FOUND
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
        def documentWithCreatedBook = databaseHelper.findById(result.body.id(), Book)
        assert documentWithCreatedBook != null, 'created document was not found'
        with(documentWithCreatedBook) {
            it.id() == result.body.id()
            it.title() == this.title
            it.author() == "Davis Stephen R."
        }
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
