package br.com.alurafood.pagamentos.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.alurafood.pagamentos.dto.PagamentoDTO;
import br.com.alurafood.pagamentos.service.PagamentoService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

	@Autowired
	private PagamentoService service;

	@GetMapping
	public Page<PagamentoDTO> listar(@PageableDefault(size = 10) Pageable paginacao) {
		return service.obterTodos(paginacao);
	}

	@GetMapping("/{id}")
	public ResponseEntity<PagamentoDTO> detalhar(@PathVariable @NotNull Long id) {
		PagamentoDTO dto = service.obterPorId(id);
		return ResponseEntity.ok(dto);
	}

	@PostMapping
	public ResponseEntity<PagamentoDTO> cadastrar(@RequestBody @Valid PagamentoDTO dto,
			UriComponentsBuilder uriBuilder) {
		PagamentoDTO pagamento = service.criarPagamento(dto);
		URI endereco = uriBuilder.path("/pagamentos/{id}").buildAndExpand(pagamento.getId()).toUri();

		return ResponseEntity.created(endereco).body(pagamento);
	}

	@PutMapping("/{id}")
	public ResponseEntity<PagamentoDTO> atualizar(@PathVariable @NotNull Long id,
			@RequestBody @Valid PagamentoDTO dto) {
		PagamentoDTO atualizado = service.atualizarPagamento(id, dto);
		return ResponseEntity.ok(atualizado);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<PagamentoDTO> remover(@PathVariable @NotNull Long id) {
		service.excluirPagamento(id);
		return ResponseEntity.noContent().build();
	}
	
	@PatchMapping("/{id}/confirmar")
	@CircuitBreaker(name = "atualizaPedido", fallbackMethod = "pagamentoAutorizadoComIntegracaoPendente")
	public void confirmarPagamento(@PathVariable @NotNull Long id) {
		service.confirmarPagamento(id);
	}
	
	public void pagamentoAutorizadoComIntegracaoPendente(Long id, Exception e) {
		service.alterarStatus(id);
	}
}
