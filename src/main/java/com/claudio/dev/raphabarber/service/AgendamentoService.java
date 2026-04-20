package com.claudio.dev.raphabarber.service;

import com.claudio.dev.raphabarber.exception.AcessoNegadoException;
import com.claudio.dev.raphabarber.model.Agendamento;
import com.claudio.dev.raphabarber.model.StatusAgendamento;
import com.claudio.dev.raphabarber.model.Usuario;
import com.claudio.dev.raphabarber.model.UserRole;
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

        validarDataFutura(agendamento.getDataHora());
        validarDisponibilidade(agendamento.getDataHora());
        validarServico(agendamento.getServico());

        return agendamentoRepository.save(agendamento);
    }

    public Agendamento atualizarAgendamento(Long id, Agendamento agendamentoAtualizado, Usuario usuarioLogado) {
        Agendamento agendamento = buscarPorId(id);
        validarPropriedade(agendamento, usuarioLogado);

        // update parcial: só altera os campos que vieram preenchidos no body
        if (agendamentoAtualizado.getDataHora() != null && !agendamento.getDataHora().equals(agendamentoAtualizado.getDataHora())) {
            validarDataFutura(agendamentoAtualizado.getDataHora());
            validarDisponibilidade(agendamentoAtualizado.getDataHora());
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

    private void validarDisponibilidade(LocalDateTime dataHora) {
        boolean existe = agendamentoRepository.existsAgendamentoNoHorario(dataHora);
        if (existe) {
            throw new RuntimeException("Este horário já está ocupado!");
        }
    }

    private void validarServico(com.claudio.dev.raphabarber.model.Servico servico) {
        if (servico == null || servico.getId() == null) {
            throw new RuntimeException("Serviço inválido!");
        }
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