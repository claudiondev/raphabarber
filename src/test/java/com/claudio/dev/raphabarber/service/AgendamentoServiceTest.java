package com.claudio.dev.raphabarber.service;

import com.claudio.dev.raphabarber.exception.AcessoNegadoException;
import com.claudio.dev.raphabarber.model.Agendamento;
import com.claudio.dev.raphabarber.model.Servico;
import com.claudio.dev.raphabarber.model.StatusAgendamento;
import com.claudio.dev.raphabarber.model.UserRole;
import com.claudio.dev.raphabarber.model.Usuario;
import com.claudio.dev.raphabarber.repository.AgendamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Regras de negócio de agendamento, com foco especial na correção do conflito de horário:
 * antes só comparava o timestamp exato, ignorando a duração do serviço.
 */
@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private ServicoService servicoService;

    @InjectMocks
    private AgendamentoService agendamentoService;

    private Usuario cliente;
    private Usuario admin;
    private Servico servicoCorte; // 60 minutos

    @BeforeEach
    void setUp() {
        cliente = new Usuario();
        cliente.setId(1L);
        cliente.setEmail("cliente@teste.com");
        cliente.setRole(UserRole.CLIENTE);

        admin = new Usuario();
        admin.setId(99L);
        admin.setEmail("admin@teste.com");
        admin.setRole(UserRole.ADMIN);

        servicoCorte = new Servico();
        servicoCorte.setId(2L);
        servicoCorte.setNome("Corte de Cabelo");
        servicoCorte.setDuracaoMinutos(60);
        servicoCorte.setPreco(BigDecimal.valueOf(50));
    }

    private LocalDateTime amanha(int hora, int minuto) {
        return LocalDateTime.now().plusDays(1).withHour(hora).withMinute(minuto).withSecond(0).withNano(0);
    }

    // Simula o que chega no body da requisição: só o id do serviço, sem os outros campos
    private Agendamento novoAgendamento(Long servicoId, LocalDateTime dataHora) {
        Servico referencia = new Servico();
        referencia.setId(servicoId);
        Agendamento agendamento = new Agendamento();
        agendamento.setServico(referencia);
        agendamento.setDataHora(dataHora);
        return agendamento;
    }

    private Agendamento agendamentoExistente(Long id, Usuario dono, LocalDateTime dataHora) {
        Agendamento agendamento = new Agendamento();
        agendamento.setId(id);
        agendamento.setCliente(dono);
        agendamento.setServico(servicoCorte);
        agendamento.setDataHora(dataHora);
        agendamento.setStatus(StatusAgendamento.AGENDADO);
        return agendamento;
    }

    @Test
    void criarAgendamento_deveCriarComSucesso_quandoHorarioLivre() {
        Agendamento novo = novoAgendamento(2L, amanha(14, 0));
        when(servicoService.buscarPorId(2L)).thenReturn(servicoCorte);
        when(agendamentoRepository.findAtivosNoIntervalo(any(), any())).thenReturn(List.of());
        when(agendamentoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Agendamento resultado = agendamentoService.criarAgendamento(novo, cliente);

        assertThat(resultado.getCliente()).isEqualTo(cliente);
        assertThat(resultado.getServico()).isEqualTo(servicoCorte);
        verify(agendamentoRepository).save(novo);
    }

    @Test
    void criarAgendamento_deveLancarExcecao_quandoDataForNoPassado() {
        Agendamento novo = novoAgendamento(2L, LocalDateTime.now().minusDays(1));

        assertThatThrownBy(() -> agendamentoService.criarAgendamento(novo, cliente))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("passada");

        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    void criarAgendamento_deveLancarExcecao_quandoServicoNaoInformado() {
        Agendamento novo = new Agendamento();
        novo.setDataHora(amanha(10, 0));
        novo.setServico(null);

        assertThatThrownBy(() -> agendamentoService.criarAgendamento(novo, cliente))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("inválido");

        verifyNoInteractions(agendamentoRepository);
    }

    @Test
    void criarAgendamento_deveLancarExcecao_quandoServicoNaoExiste() {
        Agendamento novo = novoAgendamento(404L, amanha(10, 0));
        when(servicoService.buscarPorId(404L))
                .thenThrow(new RuntimeException("Serviço não encontrado com ID: 404"));

        assertThatThrownBy(() -> agendamentoService.criarAgendamento(novo, cliente))
                .hasMessageContaining("não encontrado");

        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    void criarAgendamento_deveLancarExcecao_quandoConflitaComOutroAgendamentoDentroDaDuracao() {
        // Agendamento existente: 14:00 às 15:00 (serviço de 60 min)
        Agendamento existente = agendamentoExistente(10L, cliente, amanha(14, 0));
        // Novo pedido às 14:30 - não bate o horário exato, mas cai DENTRO do corte em andamento
        Agendamento novo = novoAgendamento(2L, amanha(14, 30));

        when(servicoService.buscarPorId(2L)).thenReturn(servicoCorte);
        when(agendamentoRepository.findAtivosNoIntervalo(any(), any())).thenReturn(List.of(existente));

        assertThatThrownBy(() -> agendamentoService.criarAgendamento(novo, cliente))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("conflita");

        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    void criarAgendamento_devePermitir_quandoComecaExatamenteQuandoOAnteriorTermina() {
        // Existente: 14:00 às 15:00. Novo começando às 15:00 em ponto não deve conflitar.
        Agendamento existente = agendamentoExistente(10L, cliente, amanha(14, 0));
        Agendamento novo = novoAgendamento(2L, amanha(15, 0));

        when(servicoService.buscarPorId(2L)).thenReturn(servicoCorte);
        when(agendamentoRepository.findAtivosNoIntervalo(any(), any())).thenReturn(List.of(existente));
        when(agendamentoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThatCode(() -> agendamentoService.criarAgendamento(novo, cliente))
                .doesNotThrowAnyException();
    }

    @Test
    void listarTodos_admin_deveRetornarTodosOsAgendamentosAtivos() {
        when(agendamentoRepository.findAtivos()).thenReturn(List.of(new Agendamento()));

        List<Agendamento> resultado = agendamentoService.listarTodos(admin);

        assertThat(resultado).hasSize(1);
        verify(agendamentoRepository, never()).findAtivosDoCliente(any());
    }

    @Test
    void listarTodos_cliente_deveRetornarApenasOsProprios() {
        when(agendamentoRepository.findAtivosDoCliente(cliente)).thenReturn(List.of(new Agendamento()));

        List<Agendamento> resultado = agendamentoService.listarTodos(cliente);

        assertThat(resultado).hasSize(1);
        verify(agendamentoRepository).findAtivosDoCliente(cliente);
    }

    @Test
    void buscarPorId_deveLancarAcessoNegado_quandoClienteNaoForDono() {
        Usuario outroCliente = new Usuario();
        outroCliente.setId(2L);
        outroCliente.setRole(UserRole.CLIENTE);
        Agendamento agendamento = agendamentoExistente(5L, outroCliente, amanha(9, 0));

        when(agendamentoRepository.findById(5L)).thenReturn(Optional.of(agendamento));

        assertThatThrownBy(() -> agendamentoService.buscarPorId(5L, cliente))
                .isInstanceOf(AcessoNegadoException.class);
    }

    @Test
    void buscarPorId_devePermitirAdmin_mesmoNaoSendoDono() {
        Agendamento agendamento = agendamentoExistente(5L, cliente, amanha(9, 0));
        when(agendamentoRepository.findById(5L)).thenReturn(Optional.of(agendamento));

        assertThatCode(() -> agendamentoService.buscarPorId(5L, admin)).doesNotThrowAnyException();
    }

    @Test
    void buscarPorId_deveLancarExcecao_quandoNaoEncontrado() {
        when(agendamentoRepository.findById(123L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> agendamentoService.buscarPorId(123L, cliente))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("não encontrado");
    }

    @Test
    void atualizarAgendamento_clienteNaoPodeAlterarStatus() {
        Agendamento existente = agendamentoExistente(1L, cliente, amanha(10, 0));
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(existente));

        Agendamento atualizacao = new Agendamento();
        atualizacao.setStatus(StatusAgendamento.CONCLUIDO);

        assertThatThrownBy(() -> agendamentoService.atualizarAgendamento(1L, atualizacao, cliente))
                .isInstanceOf(AcessoNegadoException.class);
    }

    @Test
    void atualizarAgendamento_adminPodeAlterarStatus() {
        Agendamento existente = agendamentoExistente(1L, cliente, amanha(10, 0));
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(agendamentoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Agendamento atualizacao = new Agendamento();
        atualizacao.setStatus(StatusAgendamento.CONCLUIDO);

        Agendamento resultado = agendamentoService.atualizarAgendamento(1L, atualizacao, admin);

        assertThat(resultado.getStatus()).isEqualTo(StatusAgendamento.CONCLUIDO);
    }

    @Test
    void atualizarAgendamento_naoDeveConsiderarOProprioAgendamentoComoConflito() {
        // Movendo o próprio agendamento de 10:00 para 10:30 - ainda cai dentro do seu intervalo original (10:00-11:00),
        // mas não deve ser tratado como conflito consigo mesmo.
        Agendamento existente = agendamentoExistente(1L, cliente, amanha(10, 0));
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(agendamentoRepository.findAtivosNoIntervalo(any(), any())).thenReturn(List.of(existente));
        when(agendamentoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Agendamento atualizacao = new Agendamento();
        atualizacao.setDataHora(amanha(10, 30));

        assertThatCode(() -> agendamentoService.atualizarAgendamento(1L, atualizacao, cliente))
                .doesNotThrowAnyException();
    }

    @Test
    void atualizarAgendamento_deveLancarExcecao_quandoNovoHorarioConflitaComOutroAgendamento() {
        Agendamento existente = agendamentoExistente(1L, cliente, amanha(10, 0));
        Agendamento outro = agendamentoExistente(2L, cliente, amanha(14, 0)); // 14:00-15:00

        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(agendamentoRepository.findAtivosNoIntervalo(any(), any())).thenReturn(List.of(outro));

        Agendamento atualizacao = new Agendamento();
        atualizacao.setDataHora(amanha(14, 15)); // dentro do intervalo de "outro"

        assertThatThrownBy(() -> agendamentoService.atualizarAgendamento(1L, atualizacao, cliente))
                .hasMessageContaining("conflita");
    }

    @Test
    void cancelarAgendamento_deveMarcarComoCancelado() {
        Agendamento existente = agendamentoExistente(7L, cliente, amanha(9, 0));
        when(agendamentoRepository.findById(7L)).thenReturn(Optional.of(existente));
        when(agendamentoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Agendamento resultado = agendamentoService.cancelarAgendamento(7L, cliente);

        assertThat(resultado.getStatus()).isEqualTo(StatusAgendamento.CANCELADO);
    }

    @Test
    void cancelarAgendamento_deveLancarAcessoNegado_quandoNaoForDonoNemAdmin() {
        Agendamento existente = agendamentoExistente(7L, cliente, amanha(9, 0));
        Usuario outroCliente = new Usuario();
        outroCliente.setId(55L);
        outroCliente.setRole(UserRole.CLIENTE);

        when(agendamentoRepository.findById(7L)).thenReturn(Optional.of(existente));

        assertThatThrownBy(() -> agendamentoService.cancelarAgendamento(7L, outroCliente))
                .isInstanceOf(AcessoNegadoException.class);
    }

    @Test
    void listarAgendamentosDoDia_deveLancarAcessoNegado_quandoNaoForAdmin() {
        assertThatThrownBy(() -> agendamentoService.listarAgendamentosDoDia(amanha(0, 0).toLocalDate(), cliente))
                .isInstanceOf(AcessoNegadoException.class);
        verifyNoInteractions(agendamentoRepository);
    }
}
