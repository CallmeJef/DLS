package com.example.demo;
import java.util.List;
import java.time.LocalDate; 

import org.springframework.data.jpa.repository.JpaRepository;
public interface BookRepository extends JpaRepository<Book_manager, Long>{
	
	//searching function DB
	List<Book_manager> findById(float id);
	List<Book_manager> findByTitleIgnoreCase(String title);
	List<Book_manager> findByAuthorIgnoreCase(String author);
	List<Book_manager> findBySubjectIgnoreCase(String subject);
	List<Book_manager> findByDate(LocalDate date);
	
}
