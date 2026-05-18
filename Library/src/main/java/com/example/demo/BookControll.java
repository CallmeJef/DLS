package com.example.demo;

import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/BookInformation")
public class BookControll {

    private final BookService service;

    public BookControll(BookService service) {
        this.service = service;
    }

    @GetMapping
    public List<Book_manager> getallBookManager() {
        return service.getAllBook_manager();
    }

    @GetMapping("/search/Subject")
    public List<Book_manager> searchSubject(@RequestParam String subject) {
        return service.searchBySubject(subject);
    }

    @GetMapping("/search/Author")
    public List<Book_manager> searchAuthor(@RequestParam String author) {
        return service.searchByAuthor(author);
    }

    @GetMapping("/search/Title")
    public List<Book_manager> searchTitle(@RequestParam String title) {
        return service.searchByTitle(title);
    }

    @GetMapping("/search/Date")
    public List<Book_manager> searchDate(@RequestParam LocalDate date) {
        return service.searchByDate(date);
    }

    @GetMapping("/search/Id")
    public Optional<Book_manager> searchId(@RequestParam BigDecimal id) {
        return service.searchById(id);
    }

    // --- Librarian control ---

    @PostMapping("/create")
    public Book_manager createBook(@RequestBody Book_manager book) {
        return service.saveBook(book);
    }

    @PutMapping("/update/{id}")
    public Book_manager updateBook(@PathVariable BigDecimal id,
                                   @RequestBody Book_manager updated) {
        return service.updateBook(id, updated);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteBook(@PathVariable BigDecimal id) {
        service.deleteBook(id);
        return "Book deleted successfully.";
    }
}