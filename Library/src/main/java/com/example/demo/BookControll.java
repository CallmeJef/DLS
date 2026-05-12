package com.example.demo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/BookInformation")
public class BookControll {
	//dito pumpupunta yung service para ma change ng control
	private  BookService  service;
	public  BookControll (BookService  service)
	{
	this.service = service;
	
	}
	
	@GetMapping
	public List<Book_manager>getallBookManager() 
	{
		return service.getAllBook_manager();
		
	}
	
	@GetMapping("/search/Subject")
	public List<Book_manager>searchSubject(@RequestParam String subject) 
	{
		return service.searchBySubject(subject);
		
	}
	
	@GetMapping("/search/Author")
	public List<Book_manager>searchAuthor(@RequestParam String author) 
	{
		return service.searchByAuthor(author);
		
	}
	
	@GetMapping("/search/Title")
	public List<Book_manager>searchTitle(@RequestParam String title) 
	{
		return service.searchByTitle(title);
		
	}
	@GetMapping("/search/Date")
	public List<Book_manager>searchDate(@RequestParam LocalDate date) 
	{
		return service.searchByDate(date);
		
	}
	
	@GetMapping("/search/Id")
	public Optional<Book_manager>searchiId(@RequestParam BigDecimal id) 
	{
		return service.searchById(id);
		
	}
	
	
}
