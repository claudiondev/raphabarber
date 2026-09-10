package com.claudio.dev.raphabarber.service;

import com.claudio.dev.raphabarber.model.Servico;
import com.claudio.dev.raphabarber.repository.ServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServicoServiceTest {

    @Mock
    private ServicoRepository servicoRepository;

    @InjectMocks
    private ServicoService servicoService;

    private Servico servico(String nome, Integer duracao, BigDecimal preco) {
        Servico s = new Servico();
        s.setNome(nome);
        s.setDuracaoMinutos(duracao);
        s.setPreco(preco);
        return s;
    }

    @Test
    void criarServico_deveSalvar_quandoDadosValidos() {
        Servico valido = servico("Corte", 30, BigDecimal.valueOf(50));
        when(servicoRepository.save(valido)).thenReturn(valido);

        Servico resultado = servicoService.criarServico(valido);

        assertThat(resultado).isEqualTo(valido);
        verify(servicoRepository).save(valido);
    }

    @Test
    void criarServico_deveLancarExcecao_quandoNomeVazio() {
        Servico invalido = servico("   ", 30, BigDecimal.valueOf(50));

        assertThatThrownBy(() -> servicoService.criarServico(invalido))
                .hasMessageContaining("Nome");
    }

    @Test
    void criarServico_deveLancarExcecao_quandoDuracaoInvalida() {
        Servico invalido = servico("Corte", 0, BigDecimal.valueOf(50));

        assertThatThrownBy(() -> servicoService.criarServico(invalido))
                .hasMessageContaining("Duração");
    }

    @Test
    void criarServico_deveLancarExcecao_quandoPrecoInvalido() {
        Servico invalido = servico("Corte", 30, BigDecimal.ZERO);

        assertThatThrownBy(() -> servicoService.criarServico(invalido))
                .hasMessageContaining("Preço");
    }

    @Test
    void buscarPorId_deveLancarExcecao_quandoNaoEncontrado() {
        when(servicoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicoService.buscarPorId(1L))
                .hasMessageContaining("não encontrado");
    }

    @Test
    void atualizarServico_deveAlterarApenasOsCamposEnviados() {
        Servico existente = servico("Corte", 30, BigDecimal.valueOf(50));
        existente.setDescricao("Descrição original");
        when(servicoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(servicoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Servico atualizacao = new Servico();
        atualizacao.setPreco(BigDecimal.valueOf(70)); // só o preço vem preenchido

        Servico resultado = servicoService.atualizarServico(1L, atualizacao);

        assertThat(resultado.getNome()).isEqualTo("Corte"); // preservado
        assertThat(resultado.getDuracaoMinutos()).isEqualTo(30); // preservado
        assertThat(resultado.getDescricao()).isEqualTo("Descrição original"); // preservado
        assertThat(resultado.getPreco()).isEqualByComparingTo(BigDecimal.valueOf(70)); // alterado
    }
}
