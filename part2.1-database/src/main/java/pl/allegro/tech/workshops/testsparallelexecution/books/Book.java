package pl.allegro.tech.workshops.testsparallelexecution.books;


import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record Book(String id, @Indexed(unique = true) String title, String author) {

    static public Book of(String title, String author) {
        return new Book(null, title, author);
    }

    public Book withId(String id) {
        return new Book(id, this.title, this.author);
    }

}