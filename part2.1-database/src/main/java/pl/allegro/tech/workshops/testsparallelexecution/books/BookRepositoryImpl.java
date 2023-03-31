package pl.allegro.tech.workshops.testsparallelexecution.books;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.MongoOperations;

import java.util.Optional;
import java.util.function.Function;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class BookRepositoryImpl implements CustomBookRepository<Book, String> {
    private final MongoOperations mongoOperations;

    public BookRepositoryImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public Optional<Book> updateById(Book book) {
        return execute(this::update, book);
    }

    @Override
    public Book create(Book book) {
        return execute(mongoOperations::save, book);
    }

    private static <R> R execute(Function<Book, R> function, Book book) {
        try {
            return function.apply(book);
        } catch (DuplicateKeyException e) {
            throw new DuplicatedTitleException();
        }
    }

    private Optional<Book> update(Book replacement) {
        return mongoOperations.update(Book.class)
                .matching(query(where("_id").is(replacement.id())))
                .replaceWith(replacement)
                .withOptions(FindAndReplaceOptions.options().returnNew())
                .findAndReplace();
    }
}
