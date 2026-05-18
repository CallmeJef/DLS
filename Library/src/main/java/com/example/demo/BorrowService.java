package com.example.demo;

import java.math.BigDecimal;
import java.util.List;

public interface BorrowService {

    // Student borrowed book
    BorrowRecord borrowBook(BigDecimal bookId, String studentEmail,
                            String studentName, String studentNumber);

    // Librarian extends due 
    BorrowRecord extendDue(Long borrowId);

    // Librarian marks book as returned 
    BorrowRecord completeDue(Long borrowId);

    // All active borrows 
    List<BorrowRecord> getAllActiveBorrows();

    // Overdue borrows notification
    List<BorrowRecord> getOverdueBorrows();
}