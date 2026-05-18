package com.example.demo;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRepository borrowRepo;
    private final BookRepository bookRepo;
    private final JavaMailSender mailSender;

   //librarian email notif
    private static final String LIBRARIAN_EMAIL = "librarian@dls.edu";

    public BorrowServiceImpl(BorrowRepository borrowRepo,
                             BookRepository bookRepo,
                             JavaMailSender mailSender) {
        this.borrowRepo = borrowRepo;
        this.bookRepo = bookRepo;
        this.mailSender = mailSender;
    }

    @Override
    public BorrowRecord borrowBook(BigDecimal bookId, String studentEmail,
                                   String studentName, String studentNumber) {
        // check  if the book exists
        Book_manager book = bookRepo.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Book not found: " + bookId));

        // check if its not already borrowed
        if (book.isBorrowed()) {
            throw new RuntimeException("Book '" + book.getTitle() + "' is already borrowed.");
        }

        // Create the borrow record with today's date 
        BorrowRecord record = new BorrowRecord(
            bookId, book.getTitle(), studentEmail,
            studentName, studentNumber, LocalDate.now()
        );

        // Mark the book as borrowed
        book.setBorrowed(true);
        bookRepo.save(book);

        return borrowRepo.save(record);
    }

    @Override
    public BorrowRecord extendDue(Long borrowId) {
        BorrowRecord record = borrowRepo.findById(borrowId)
            .orElseThrow(() -> new RuntimeException("Borrow record not found: " + borrowId));

        if (record.isReturned()) {
            throw new RuntimeException("This borrow record is already completed.");
        }

        // Extend due date by one week from current due date
        record.setDueDate(record.getDueDate().plusWeeks(1));
        record.setExtensionCount(record.getExtensionCount() + 1);

        return borrowRepo.save(record);
    }

    @Override
    public BorrowRecord completeDue(Long borrowId) {
        BorrowRecord record = borrowRepo.findById(borrowId)
            .orElseThrow(() -> new RuntimeException("Borrow record not found: " + borrowId));

        // Mark borrow as returned
        record.setReturned(true);
        borrowRepo.save(record);

        // Mark the book as available again
        Book_manager book = bookRepo.findById(record.getBookId())
            .orElseThrow(() -> new RuntimeException("Book not found: " + record.getBookId()));
        book.setBorrowed(false);
        bookRepo.save(book);

        return record;
    }

    @Override
    public List<BorrowRecord> getAllActiveBorrows() {
        return borrowRepo.findByIsReturnedFalse();
    }

    @Override
    public List<BorrowRecord> getOverdueBorrows() {
        return borrowRepo.findByDueDateBeforeAndIsReturnedFalse(LocalDate.now());
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void sendOverdueNotification() {
        List<BorrowRecord> overdue = getOverdueBorrows();
        if (overdue.isEmpty()) return;

        StringBuilder body = new StringBuilder("Overdue Books as of " + LocalDate.now() + ":\n\n");
        for (BorrowRecord r : overdue) {
            body.append("• ").append(r.getBookTitle())
                .append(" — borrowed by ").append(r.getStudentName())
                .append(" (").append(r.getStudentEmail()).append(")")
                .append(" — due: ").append(r.getDueDate()).append("\n");
        }

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(LIBRARIAN_EMAIL);
        msg.setSubject("DLS Library — Overdue Books Notification");
        msg.setText(body.toString());
        mailSender.send(msg);

        System.out.println("Overdue notification sent for " + overdue.size() + " book(s).");
    }
}