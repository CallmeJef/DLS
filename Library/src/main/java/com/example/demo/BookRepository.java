package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface BookRepository extends JpaRepository<Book_manager, BigDecimal> {

    // FIX: use Containing for partial match search (much better for a search bar)
    List<Book_manager> findByTitleContainingIgnoreCase(String title);
    List<Book_manager> findByAuthorContainingIgnoreCase(String author);
    List<Book_manager> findBySubjectContainingIgnoreCase(String subject);
    List<Book_manager> findByDate(LocalDate date);

    // Filter panel: get all books under a subject category
    List<Book_manager> findBySubjectIgnoreCase(String subject);

    // Availability filter
    List<Book_manager> findByIsBorrowed(boolean isBorrowed);

    // NOTE: findById(BigDecimal) is already provided by JpaRepository — no need to re-declare
}