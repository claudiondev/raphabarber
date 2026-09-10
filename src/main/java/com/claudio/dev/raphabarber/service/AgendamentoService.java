package com.claudio.dev.raphabarber.service;

import com.claudio.dev.raphabarber.exception.AcessoNegadoException;
import com.claudio.dev.raphabarber.model.Agendamento;
import com.claudio.dev.raphabarber.model.Servico;
import com.claudio.dev.raphabarber.model.StatusAgendamento;
import com.claudio.dev.raphabarber.model.Usuario;
import com.claudio.dev.raphabarber.model.UserRole;
import com.claudio.dev.raphabarber.repository.AgendamentoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class AgendamentoService {
    // Limite superior assumido para a duração de qualquer serviço - define o quão "para trás" a busca de
    // conflitos precisa olhar (cobre inclusive um agendamento que começou no dia anterior e viraria a meia-noite).
    private static final int DURACAO_MAXIMA_ASSUMIDA_MINUTOS = 360;

    private final AgendamentoRepository agendamentoRepository;
    private final ServicoService servicoService;

    public AgendamentoService(AgendamentoRepository agendamentoRepository, ServicoService servicoService) {
        this.agendamentoRepository = agendamentoRepository;
        this.servicoService = servicoService;
    }

    public List<Agendamento> listarTodos(Usuario usuarioLogado) {
        if (isAdmin(usuarioLogado)) {
            return agendamentoRepository.findAtivos();
        }
        return agendamentoRepository.findAtivosDoCliente(usuarioLogado);
    }

    public Agendamento buscarPorId(Long id, Usuario usuarioLogado) {
        Agendamento agendamento = buscarPorId(id);
        validarPropriedade(agendamento, usuarioLogado);
        return agendamento;
    }

    // sem checagem de propriedade - uso interno, quando o dono já foi validado ou não importa
    private Agendamento buscarPorId(Long id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado com ID: " + id));
    }

    public Agendamento criarAgendamento(Agendamento agendamento, Usuario usuarioLogado) {
        // ignora qualquer "cliente" vindo no body - o dono é sempre quem está autenticado
        agendamento.setCliente(usuarioLogado);

        // carrega o serviço de verdade (o body só traz o id) - garante que ele existe e nos dá a duração real
        Servico servico = carregarServico(agendamento.getServico());
        agendamento.setServico(servico);

        validarDataFutura(agendamento.getDataHora());
        validarDisponibilidade(agendamento.getDataHora(), servico.getDuracaoMinutos(), null);

        return agendamentoRepository.save(agendamento);
    }

    public Agendamento atualizarAgendamento(Long id, Agendamento agendamentoAtualizado, Usuario usuarioLogado) {
        Agendamento agendamento = buscarPorId(id);
        validarPropriedade(agendamento, usuarioLogado);

        // update parcial: só altera os campos que vieram preenchidos no body
        if (agendamentoAtualizado.getDataHora() != null && !agendamento.getDataHora().equals(agendamentoAtualizado.getDataHora())) {
            validarDataFutura(agendamentoAtualizado.getDataHora());
            // duração vem do serviço já persistido - esta atualização não permite trocar o serviço
            validarDisponibilidade(agendamentoAtualizado.getDataHora(), agendamento.getServico().getDuracaoMinutos(), agendamento.getId());
            agendamento.setDataHora(agendamentoAtualizado.getDataHora());
        }

        if (agendamentoAtualizado.getStatus() != null) {
            // mudar status (ex: marcar como CONCLUIDO) é ação do barbeiro, não do cliente dono do agendamento
            if (!isAdmin(usuarioLogado)) {
                throw new AcessoNegadoException("Apenas o administrador pode alterar o status do agendamento!");
            }
            agendamento.setStatus(agendamentoAtualizado.getStatus());
        }

        return agendamentoRepository.save(agendamento);
    }

    public Agendamento cancelarAgendamento(Long id, Usuario usuarioLogado) {
        Agendamento agendamento = buscarPorId(id);
        validarPropriedade(agendamento, usuarioLogado);
        agendamento.setStatus(StatusAgendamento.CANCELADO);
        return agendamentoRepository.save(agendamento);
    }

    public List<Agendamento> listarAgendamentosDoCliente(Usuario cliente) {
        return agendamentoRepository.findByCliente(cliente);
    }

    // agenda diária do Rapha - apenas admin
    public List<Agendamento> listarAgendamentosDoDia(LocalDate data, Usuario usuarioLogado) {
        if (!isAdmin(usuarioLogado)) {
            throw new AcessoNegadoException("Apenas o administrador pode ver a agenda do dia!");
        }
        return agendamentoRepository.findByData(data);
    }

    public List<Agendamento> listarAgendamentosDoClientePorPeriodo(Usuario cliente, LocalDate dataInicio, LocalDate dataFim) {
        return agendamentoRepository.findByClienteAndPeriodo(cliente, dataInicio, dataFim);
    }

    private void validarDataFutura(LocalDateTime dataHora) {
        if (dataHora.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Não é permitido agendar em data/hora passada!");
        }
    }

    // Conflito real de horário: considera a duração de cada serviço, não só o timestamp de início.
    // idParaIgnorar é o próprio agendamento sendo atualizado (null na criação, onde nada deve ser ignorado).
    private void validarDisponibilidade(LocalDateTime inicio, Integer duracaoMinutos, Long idParaIgnorar) {
        LocalDateTime fim = inicio.plusMinutes(duracaoMinutos);
        LocalDateTime inicioJanela = inicio.minusMinutes(DURACAO_MAXIMA_ASSUMIDA_MINUTOS);

        List<Agendamento> candidatos = agendamentoRepository.findAtivosNoIntervalo(inicioJanela, fim);

        boolean conflito = candidatos.stream()
                .filter(a -> !Objects.equals(a.getId(), idParaIgnorar))
                .anyMatch(a -> {
                    LocalDateTime existenteFim = a.getDataHora().plusMinutes(a.getServico().getDuracaoMinutos());
                    return inicio.isBefore(existenteFim) && a.getDataHora().isBefore(fim);
                });

        if (conflito) {
            throw new RuntimeException("Este horário conflita com outro agendamento já existente!");
        }
    }

    // Busca o serviço de verdade no banco - o body da requisição só traz o id (ex: { "servico": { "id": 2 } }).
    // Também garante que o id enviado realmente existe, evitando um erro de constraint no banco mais adiante.
    private Servico carregarServico(Servico servicoRecebido) {
        if (servicoRecebido == null || servicoRecebido.getId() == null) {
            throw new RuntimeException("Serviço inválido!");
        }
        return servicoService.buscarPorId(servicoRecebido.getId());
    }

    private boolean isAdmin(Usuario usuario) {
        return usuario.getRole() == UserRole.ADMIN;
    }

    private void validarPropriedade(Agendamento agendamento, Usuario usuarioLogado) {
        if (!isAdmin(usuarioLogado) && !agendamento.getCliente().getId().equals(usuarioLogado.getId())) {
            throw new AcessoNegadoException("Você não tem permissão para acessar este agendamento!");
        }
    }
}