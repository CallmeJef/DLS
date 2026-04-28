package com.example.demo;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class BookServiceImpl implements BookService{
    private BookRepository BookRepo;	
     
		public BookServiceImpl(BookRepository BookRepo) 
		{this.BookRepo=BookRepo;}
		//Sa understanding ko, dito pumupunta lahat ng data para pasok papuntang controller to db. Like tiga set o convert sa data type ng kayang basahin ng db
		
		@Override
		public List<Book_manager> getAllBook_manager(){
			return BookRepo.findAll();
		}
		
		@Override
		public List<Book_manager>searchById(float id)
		{
			return BookRepo.findById(id);
		}
		
		@Override
		public List<Book_manager> searchByAuthor(String author)
		{
			return BookRepo.findByAuthorIgnoreCase(author);
			
		}
		@Override
		public List<Book_manager>searchByTitle(String title)
		{
			return BookRepo.findByTitleIgnoreCase(title);
			
		}
		@Override
		public List<Book_manager>searchBySubject(String subject)
		{
			return BookRepo.findBySubjectIgnoreCase(subject);
			
		}
		@Override
		public List<Book_manager>searchByDate(LocalDate date)
		{
			return BookRepo.findByDate(date);
			
		}
	

}
