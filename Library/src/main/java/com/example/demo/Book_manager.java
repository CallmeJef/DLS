package com.example.demo;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "book_information")  
public class Book_manager {

    @Id
    @Column(name = "classification_id")
    private BigDecimal id;

    @Column(name = "author")
    private String author;

    @Column(name = "title")
    private String title;

    @Column(name = "tubject")
    private String subject;

    @Column(name = "date")
    private LocalDate date;

   
    @Column(name = "image_url")
    private String imageUrl;

    //tracks kapag borrowed ang books
    @Column(name = "is_borrowed", nullable = false)
    private boolean isBorrowed = false;

    //jpa constructors 
    public Book_manager() {}

    public Book_manager(BigDecimal id, String author, String title,
                        String subject, LocalDate date, String imageUrl) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.subject = subject;
        this.date = date;
        this.imageUrl = imageUrl;
        this.isBorrowed = false;
    }

    public BigDecimal getId() { return id; }
    public void setId(BigDecimal id) { this.id = id; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isBorrowed() { return isBorrowed; }
    public void setBorrowed(boolean borrowed) { isBorrowed = borrowed; }
}