package com.example.demo;


import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "borrow_records")
public class BorrowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long borrowId;

    // Which book was borrowed 
    @Column(name = "book_id", nullable = false)
    private BigDecimal bookId;

    @Column(name = "book_title", nullable = false)
    private String bookTitle;

    // Student info captured at borrow time
    @Column(name = "student_email", nullable = false)
    private String studentEmail;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(name = "student_number", nullable = false)
    private String studentNumber;

    // Borrow window
    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    // Librarian can can change if its borrowed o hindi
    @Column(name = "is_returned", nullable = false)
    private boolean isReturned = false;

    // Pinapakita kung illang beses na, na extend ang due
    @Column(name = "extension_count", nullable = false)
    private int extensionCount = 0;

    public BorrowRecord() {}

    public BorrowRecord(BigDecimal bookId, String bookTitle, String studentEmail,
                        String studentName, String studentNumber, LocalDate borrowDate) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.studentEmail = studentEmail;
        this.studentName = studentName;
        this.studentNumber = studentNumber;
        this.borrowDate = borrowDate;
        this.dueDate = borrowDate.plusWeeks(1);  // 1 week due lang
        this.isReturned = false;
        this.extensionCount = 0;
    }

    public Long getBorrowId() { return borrowId; }

    public BigDecimal getBookId() { return bookId; }
    public void setBookId(BigDecimal bookId) { this.bookId = bookId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public boolean isReturned() { return isReturned; }
    public void setReturned(boolean returned) { isReturned = returned; }

    public int getExtensionCount() { return extensionCount; }
    public void setExtensionCount(int extensionCount) { this.extensionCount = extensionCount; }

  
    public boolean isOverdue() {
        return !isReturned && LocalDate.now().isAfter(dueDate);
    }
}