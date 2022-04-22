package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Book;

import java.util.List;

public interface BookDao {
    Book getById(Long id);

    Book findBookByTitle(String clean_code);

    Book saveNewBook(Book book);

    Book updateBook(Book saved);

    void deleteBookById(Long id);

    Book findByIsbn(String isbn);

    List<Book> findAll();

    Book findBookByTitleCriteria(String title);
}
