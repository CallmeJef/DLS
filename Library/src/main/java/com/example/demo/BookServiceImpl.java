package com.example.demo;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepo;

   
    public BookServiceImpl(BookRepository bookRepo) {
        this.bookRepo = bookRepo;
    }

    @Override
    public List<Book_manager> getAllBook_manager() {
        return bookRepo.findAll();
    }

    @Override
    public Optional<Book_manager> searchById(BigDecimal id) {
        return bookRepo.findById(id);
    }

    @Override
    public List<Book_manager> searchByAuthor(String author) {
        return bookRepo.findByAuthorContainingIgnoreCase(author);
    }

    @Override
    public List<Book_manager> searchByTitle(String title) {
        return bookRepo.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public List<Book_manager> searchBySubject(String subject) {
        return bookRepo.findBySubjectContainingIgnoreCase(subject);
    }

    @Override
    public List<Book_manager> searchByDate(LocalDate date) {
        return bookRepo.findByDate(date);
    }

    @Override
    public Book_manager saveBook(Book_manager book) {
        return bookRepo.save(book);
    }

    @Override
    public void deleteBook(BigDecimal id) {
        bookRepo.deleteById(id);
    }

    @Override
    public Book_manager updateBook(BigDecimal id, Book_manager updated) {
        // pang chech if meron nung Id
        Book_manager existing = bookRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        existing.setAuthor(updated.getAuthor());
        existing.setTitle(updated.getTitle());
        existing.setSubject(updated.getSubject());
        existing.setDate(updated.getDate());
        existing.setImageUrl(updated.getImageUrl());

        return bookRepo.save(existing);
    }
}