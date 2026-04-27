package com.example.demo;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import java.time.LocalDate; 
@Entity
@Table(name="Book information")
public class Book_manager {
	//int data type muna para sa date. Ang gagawin ay lalagay diyan ang inputs galing ui tas lalagay sa loob ng local date para mapagana.
	//hindi ko pa nagagawa yung logic para sa date, wait muna
	//baka lipat ko yan sa ui part para mas madali input?
	int month;
	int day;
	int year;
	
	@Id
	@Column(name="Classification_id")
	private float id;
	
	@Column(name="Author")
	private String author;
	
	@Column(name="Title")
	private String title;
	
	@Column(name="Subject")
	private String subject;
	
	@Column(name="Date")
	private LocalDate date;
	
	public Book_manager(float id, String author, String title, String subject, LocalDate date) 
	{
		this.id=id;
		this.author=author;
		this.title=title;
		this.subject=subject;
		this.date=date;
	}
	//Tiga kuha ng data para ma pasok sa Db gamit getter 
	public float getid() 
	{return id;}
	public String getauthor() 
	{return author;}
	public String gettitle()
	{return title;}
	public String getsubject() 
	{return subject;}
	public LocalDate getdate() 
	{return date;}
	
	//Tiga pasok ng data sa db galing sa getter.
	public void setid(float id) 
	{this.id=id;}
	public void setauthor(String author) 
	{this.author=author;}
	public void settitle(String title) 
	{this.title=title;}
	public void setsubject(String subject) 
	{this.subject=subject;}
	public void setdate(LocalDate date)
	{this.date=date;}
}
