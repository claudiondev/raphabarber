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

    public List<Servico> listarTodos() {
        return servicoRepository.findAll();
    }

    public Servico buscarPorId(Long id) {
        return servicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado com ID: " + id));
    }

    // duplica as constraints de Bean Validation da entidade - @Valid já cobre o POST,
    // mas esse método também pode ser chamado internamente sem passar pelo controller
    public Servico criarServico(Servico servico) {
        if (servico.getNome() == null || servico.getNome().trim().isEmpty()) {
            throw new RuntimeException("Nome do serviço é obrigatório!");
        }

        if (servico.getDuracaoMinutos() == null || servico.getDuracaoMinutos() <= 0) {
            throw new RuntimeException("Duração deve ser maior que 0 minutos!");
        }

        if (servico.getPreco() == null || servico.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Preço deve ser maior que 0!");
        }

        return servicoRepository.save(servico);
    }

    // update parcial: só altera o que veio preenchido e válido no body
    public Servico atualizarServico(Long id, Servico servicoAtualizado) {
        Servico servico = buscarPorId(id);

        if (servicoAtualizado.getNome() != null && !servicoAtualizado.getNome().trim().isEmpty()) {
            servico.setNome(servicoAtualizado.getNome());
        }

        if (servicoAtualizado.getDuracaoMinutos() != null && servicoAtualizado.getDuracaoMinutos() > 0) {
            servico.setDuracaoMinutos(servicoAtualizado.getDuracaoMinutos());
        }

        if (servicoAtualizado.getPreco() != null && servicoAtualizado.getPreco().compareTo(BigDecimal.ZERO) > 0) {
            servico.setPreco(servicoAtualizado.getPreco());
        }

        if (servicoAtualizado.getDescricao() != null) {
            servico.setDescricao(servicoAtualizado.getDescricao());
        }

        return servicoRepository.save(servico);
    }

    public void deletarServico(Long id) {
        Servico servico = buscarPorId(id);
        servicoRepository.delete(servico);
    }

    public List<Servico> buscarServicosComPrecoMenorQue(BigDecimal preco) {
        return servicoRepository.findAll().stream()
                .filter(s -> s.getPreco().compareTo(preco) < 0)
                .toList();
    }
}
