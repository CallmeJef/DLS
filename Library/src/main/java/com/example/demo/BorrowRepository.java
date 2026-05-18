package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BorrowRepository extends JpaRepository<BorrowRecord, Long> {

    // All active borrows
    List<BorrowRecord> findByIsReturnedFalse();

    // Active borrow for a specific book (
    Optional<BorrowRecord> findByBookIdAndIsReturnedFalse(BigDecimal bookId);

    // All borrows by a student
    List<BorrowRecord> findByStudentEmail(String studentEmail);

    // Overdue records
    List<BorrowRecord> findByDueDateBeforeAndIsReturnedFalse(LocalDate today);
}