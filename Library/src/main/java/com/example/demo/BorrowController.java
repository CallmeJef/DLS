package com.example.demo;

import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Borrow")
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @PostMapping("/borrow")
    public BorrowRecord borrowBook(@RequestBody Map<String, String> body) {
        BigDecimal bookId = new BigDecimal(body.get("bookId"));
        return borrowService.borrowBook(
            bookId,
            body.get("studentEmail"),
            body.get("studentName"),
            body.get("studentNumber")
        );
    }

    // Librarian extend a borrow's due date by one week
    @PutMapping("/extend/{borrowId}")
    public BorrowRecord extendDue(@PathVariable Long borrowId) {
        return borrowService.extendDue(borrowId);
    }

    // Librarian mark a borrow as returned (book becomes available again)
    @PutMapping("/complete/{borrowId}")
    public BorrowRecord completeDue(@PathVariable Long borrowId) {
        return borrowService.completeDue(borrowId);
    }

    // Librarian currently active borrows
    @GetMapping("/active")
    public List<BorrowRecord> getActiveBorrows() {
        return borrowService.getAllActiveBorrows();
    }

    // Librarian overdue borrows
    @GetMapping("/overdue")
    public List<BorrowRecord> getOverdueBorrows() {
        return borrowService.getOverdueBorrows();
    }
}