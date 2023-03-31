package pl.allegro.tech.workshops.testsparallelexecution.books;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Optional<Book> findBook(String id) {
        return bookRepository.findById(id);
    }

    public Book createBook(Book book) {
        return bookRepository.create(book);
    }

    public Optional<Book> updateBook(Book book) {
        return bookRepository.updateById(book);
    }

    public void deleteBook(String id) {
        bookRepository.deleteById(id);
    }
}
