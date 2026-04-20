package com.claudio.dev.raphabarber.service;

import com.claudio.dev.raphabarber.model.Agendamento;
import com.claudio.dev.raphabarber.model.StatusAgendamento;
import com.claudio.dev.raphabarber.model.Usuario;
import com.claudio.dev.raphabarber.repository.AgendamentoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgendamentoService {
    private final AgendamentoRepository agendamentoRepository;

    public AgendamentoService(AgendamentoRepository agendamentoRepository) {
        this.agendamentoRepository = agendamentoRepository;
    }

    /**
     * Lista todos os agendamentos ativos (no cancelados)
     */
    public List<Agendamento> listarTodos() {
        return agendamentoRepository.findAtivos();
    }

    /**
     * Busca um agendamento por ID
     */
    public Agendamento buscarPorId(Long id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento no encontrado com ID: " + id));
    }

    /**
     * Cria um novo agendamento com validaes
     */
    public Agendamento criarAgendamento(Agendamento agendamento) {
        // Validar se a data  no futuro
        validarDataFutura(agendamento.getDataHora());

        // Validar se o horrio est disponvel
        validarDisponibilidade(agendamento.getDataHora());

        // Validar cliente
        validarCliente(agendamento.getCliente());

        // Validar servio
        validarServico(agendamento.getServico());

        return agendamentoRepository.save(agendamento);
    }

    /**
     * Atualiza um agendamento existente
     */
    public Agendamento atualizarAgendamento(Long id, Agendamento agendamentoAtualizado) {
        Agendamento agendamento = buscarPorId(id);

        // Se mudou a data/hora, validar (Adicionada checagem de nulo para evitar erro na setinha do Admin)
        if (agendamentoAtualizado.getDataHora() != null && !agendamento.getDataHora().equals(agendamentoAtualizado.getDataHora())) {
            validarDataFutura(agendamentoAtualizado.getDataHora());
            validarDisponibilidade(agendamentoAtualizado.getDataHora());
            agendamento.setDataHora(agendamentoAtualizado.getDataHora());
        }

        // Atualizar status se fornecido
        if (agendamentoAtualizado.getStatus() != null) {
            agendamento.setStatus(agendamentoAtualizado.getStatus());
        }

        return agendamentoRepository.save(agendamento);
    }

    /**
     * Cancela um agendamento
     */
    public Agendamento cancelarAgendamento(Long id) {
        Agendamento agendamento = buscarPorId(id);
        agendamento.setStatus(StatusAgendamento.CANCELADO);
        return agendamentoRepository.save(agendamento);
    }

    /**
     * Lista agendamentos de um cliente especfico
     */
    public List<Agendamento> listarAgendamentosDoCliente(Usuario cliente) {
        return agendamentoRepository.findByCliente(cliente);
    }

    /**
     * Lista agendamentos de um dia especfico (agenda diria do Rapha)
     */
    public List<Agendamento> listarAgendamentosDoDia(LocalDate data) {
        return agendamentoRepository.findByData(data);
    }

    /**
     * Lista agendamentos do cliente em um perodo
     */
    public List<Agendamento> listarAgendamentosDoClientePorPeriodo(Usuario cliente, LocalDate dataInicio, LocalDate dataFim) {
        return agendamentoRepository.findByClienteAndPeriodo(cliente, dataInicio, dataFim);
    }

    /**
     * Valida se a data  no futuro
     */
    private void validarDataFutura(LocalDateTime dataHora) {
        if (dataHora.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("No  permitido agendar em data/hora passada!");
        }
    }

    /**
     * Valida se o horrio est disponvel (sem conflito)
     */
    private void validarDisponibilidade(LocalDateTime dataHora) {
        boolean existe = agendamentoRepository.existsAgendamentoNoHorario(dataHora);
        if (existe) {
            throw new RuntimeException("Este horrio j est ocupado!");
        }
    }

    /**
     * Valida se o cliente existe
     */
    private void validarCliente(Usuario cliente) {
        if (cliente == null || cliente.getId() == null) {
            throw new RuntimeException("Cliente invlido!");
        }
    }

    /**
     * Valida se o servio existe
     */
    private void validarServico(com.claudio.dev.raphabarber.model.Servico servico) {
        if (servico == null || servico.getId() == null) {
            throw new RuntimeException("Servio invlido!");
        }
    }
}