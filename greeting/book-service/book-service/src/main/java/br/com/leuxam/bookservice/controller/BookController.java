package br.com.leuxam.bookservice.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import br.com.leuxam.bookservice.model.Book;
import br.com.leuxam.bookservice.repository.BookRepository;
import br.com.leuxam.bookservice.response.Cambio;

@RestController
@RequestMapping("book-service")
public class BookController {
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private BookRepository repository;
	
	@GetMapping("/{id}/{currency}")
	public Book findBook(
			@PathVariable("id") Long id,
			@PathVariable("currency") String currency) {
		
		var port = environment.getProperty("local.server.port");
		
		var book = repository.findById(id);
		
		if(!book.isPresent()) throw new RuntimeException("Book not found!");
		
		HashMap<String, String> params = new HashMap<>();
		params.put("amount", book.get().getPrice().toString());
		params.put("from", "USD");
		params.put("to", currency);
		
		var response = new RestTemplate().getForEntity("http://localhost:8000/cambio-service/" + "{amount}/{from}/{to}", Cambio.class, params);
		
		book.get().setEnvironment(port);
		book.get().setPrice(response.getBody().getConvertedValue());
		
		return book.get();
	}
}
