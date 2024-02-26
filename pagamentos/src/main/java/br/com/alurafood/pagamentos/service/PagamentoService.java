package br.com.alurafood.pagamentos.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.alurafood.pagamentos.dto.PagamentoDTO;
import br.com.alurafood.pagamentos.model.Pagamento;
import br.com.alurafood.pagamentos.model.Status;
import br.com.alurafood.pagamentos.repository.PagamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class PagamentoService {

	private PagamentoRepository pagamentoRepository;
	private ModelMapper modelMapper;

	@Autowired
	public PagamentoService(PagamentoRepository pagamentoRepository, ModelMapper modelMapper) {
		this.pagamentoRepository = pagamentoRepository;
		this.modelMapper = modelMapper;
	}

	public Page<PagamentoDTO> obterTodos(Pageable paginacao) {
		return pagamentoRepository.findAll(paginacao).map(p -> modelMapper.map(p, PagamentoDTO.class));
	}

	public PagamentoDTO obterPorId(Long id) {
		Pagamento pagamento = pagamentoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());

		return modelMapper.map(pagamento, PagamentoDTO.class);
	}

	public PagamentoDTO criarPagamento(PagamentoDTO dto) {
		Pagamento pagamento = modelMapper.map(dto, Pagamento.class);
		pagamento.setStatus(Status.CRIADO);
		pagamentoRepository.save(pagamento);

		return modelMapper.map(pagamento, PagamentoDTO.class);
	}
	
	@Transactional
	public PagamentoDTO atualizarPagamento(Long id, PagamentoDTO dto) {
		Pagamento pagamento = modelMapper.map(dto, Pagamento.class);
		pagamento.setId(id);
		pagamento = pagamentoRepository.save(pagamento);
		return modelMapper.map(pagamento, PagamentoDTO.class);
	}
	
	@Transactional
	public void excluirPagamento(Long id) {
		pagamentoRepository.deleteById(id);
	}
}
