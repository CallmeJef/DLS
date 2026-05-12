package com.example.demo;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.time.LocalDate; 

import org.springframework.data.jpa.repository.JpaRepository;
public interface BookRepository extends JpaRepository<Book_manager, BigDecimal>{
	
	//searching function DB
	Optional<Book_manager> findById(BigDecimal id);
	List<Book_manager> findByTitleIgnoreCase(String title);
	List<Book_manager> findByAuthorIgnoreCase(String author);
	List<Book_manager> findBySubjectIgnoreCase(String subject);
	List<Book_manager> findByDate(LocalDate date);
	
}
