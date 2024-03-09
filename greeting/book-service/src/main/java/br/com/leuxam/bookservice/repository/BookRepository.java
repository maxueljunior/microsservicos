package br.com.leuxam.bookservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.leuxam.bookservice.model.Book;

public interface BookRepository extends JpaRepository<Book, Long>{
	
}
