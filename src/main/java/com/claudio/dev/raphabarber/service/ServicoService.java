package com.claudio.dev.raphabarber.service;

import com.claudio.dev.raphabarber.model.Servico;
import com.claudio.dev.raphabarber.repository.ServicoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ServicoService {
    private final ServicoRepository servicoRepository;

    public ServicoService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    /**
     * Lista todos os serviços disponíveis
     */
    public List<Servico> listarTodos() {
        return servicoRepository.findAll();
    }

    /**
     * Busca um serviço por ID
     */
    public Servico buscarPorId(Long id) {
        return servicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado com ID: " + id));
    }

    /**
     * Cria um novo serviço com validações
     */
    public Servico criarServico(Servico servico) {
        // Validar nome
        if (servico.getNome() == null || servico.getNome().trim().isEmpty()) {
            throw new RuntimeException("Nome do serviço é obrigatório!");
        }

        // Validar duração
        if (servico.getDuracaoMinutos() == null || servico.getDuracaoMinutos() <= 0) {
            throw new RuntimeException("Duração deve ser maior que 0 minutos!");
        }

        // Validar preço
        if (servico.getPreco() == null || servico.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Preço deve ser maior que 0!");
        }

        return servicoRepository.save(servico);
    }

    /**
     * Atualiza um serviço existente
     */
    public Servico atualizarServico(Long id, Servico servicoAtualizado) {
        Servico servico = buscarPorId(id);

        // Validar e atualizar nome
        if (servicoAtualizado.getNome() != null && !servicoAtualizado.getNome().trim().isEmpty()) {
            servico.setNome(servicoAtualizado.getNome());
        }

        // Validar e atualizar duração
        if (servicoAtualizado.getDuracaoMinutos() != null && servicoAtualizado.getDuracaoMinutos() > 0) {
            servico.setDuracaoMinutos(servicoAtualizado.getDuracaoMinutos());
        }

        // Validar e atualizar preço
        if (servicoAtualizado.getPreco() != null && servicoAtualizado.getPreco().compareTo(BigDecimal.ZERO) > 0) {
            servico.setPreco(servicoAtualizado.getPreco());
        }

        // Atualizar descrição (pode ser null)
        if (servicoAtualizado.getDescricao() != null) {
            servico.setDescricao(servicoAtualizado.getDescricao());
        }

        return servicoRepository.save(servico);
    }

    /**
     * Deleta um serviço
     */
    public void deletarServico(Long id) {
        Servico servico = buscarPorId(id);
        servicoRepository.delete(servico);
    }

    /**
     * Busca serviços com preço inferior a um valor específico
     */
    public List<Servico> buscarServicosComPrecoMenorQue(BigDecimal preco) {
        return servicoRepository.findAll().stream()
                .filter(s -> s.getPreco().compareTo(preco) < 0)
                .toList();
    }
}
