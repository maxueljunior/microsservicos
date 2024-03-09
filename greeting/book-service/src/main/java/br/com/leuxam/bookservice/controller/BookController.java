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
import br.com.leuxam.bookservice.proxy.CambioProxy;
import br.com.leuxam.bookservice.repository.BookRepository;
import br.com.leuxam.bookservice.response.Cambio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Book endpoint")
@RestController
@RequestMapping("book-service")
public class BookController {
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private BookRepository repository;
	
	@Autowired
	private CambioProxy proxy;
	
	@Operation(summary = "Find a specific book by your ID")
	@GetMapping("/{id}/{currency}")
	public Book findBook(
			@PathVariable("id") Long id,
			@PathVariable("currency") String currency) {
		
		var port = environment.getProperty("local.server.port");
		
		var book = repository.findById(id);
		
		if(!book.isPresent()) throw new RuntimeException("Book not found!");
		
		var cambio = proxy.getCambio(book.get().getPrice(), "USD", currency);
		
		book.get().setEnvironment(port + " Cambio Port: " + cambio.getEnvironment());
		book.get().setPrice(cambio.getConvertedValue());
		
		return book.get();
	}
	
//	@GetMapping("/{id}/{currency}")
//	public Book findBook(
//			@PathVariable("id") Long id,
//			@PathVariable("currency") String currency) {
//		
//		var port = environment.getProperty("local.server.port");
//		
//		var book = repository.findById(id);
//		
//		if(!book.isPresent()) throw new RuntimeException("Book not found!");
//		
//		HashMap<String, String> params = new HashMap<>();
//		params.put("amount", book.get().getPrice().toString());
//		params.put("from", "USD");
//		params.put("to", currency);
//		
//		var response = new RestTemplate().getForEntity("http://localhost:8000/cambio-service/" + "{amount}/{from}/{to}", Cambio.class, params);
//		
//		book.get().setEnvironment(port);
//		book.get().setPrice(response.getBody().getConvertedValue());
//		
//		return book.get();
//	}
}
