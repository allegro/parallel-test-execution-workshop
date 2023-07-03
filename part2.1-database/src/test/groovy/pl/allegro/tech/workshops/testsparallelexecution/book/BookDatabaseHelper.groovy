package pl.allegro.tech.workshops.testsparallelexecution.book

import pl.allegro.tech.workshops.testsparallelexecution.books.Book
import pl.allegro.tech.workshops.testsparallelexecution.support.DatabaseHelper


class BookDatabaseHelper extends DatabaseHelper<Book> {
    @Override
    Class<Book> getJavaType() {
        return Book
    }
}
