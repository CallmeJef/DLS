package com.example.demo;
import java.util.List;
import java.time.LocalDate;

public interface BookService {
	//Eto yung logic na hinahanap yung data sa loob from repo.
	List<Book_manager> getAllBook_manager();
	List<Book_manager> searchById(float id);
	List<Book_manager> searchByAuthor(String author);
	List<Book_manager> searchByTitle(String title);
	List<Book_manager> searchBySubject(String subject);
	List<Book_manager> searchByDate(LocalDate date);
	
}
