package com.example.demo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookService {

    List<Book_manager> getAllBook_manager();
    Optional<Book_manager> searchById(BigDecimal id);
    List<Book_manager> searchByAuthor(String author);
    List<Book_manager> searchByTitle(String title);
    List<Book_manager> searchBySubject(String subject);
    List<Book_manager> searchByDate(LocalDate date);

    // Librarian operations
    Book_manager saveBook(Book_manager book);
    void deleteBook(BigDecimal id);
    Book_manager updateBook(BigDecimal id, Book_manager updated);
	}
