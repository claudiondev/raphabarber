package com.claudio.dev.raphabarber.service;

import com.claudio.dev.raphabarber.model.Portfolio;
import com.claudio.dev.raphabarber.repository.PortfolioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    @Test
    void listarTodos_deveRetornarTodosOsItens() {
        Portfolio item = new Portfolio();
        item.setUrlImagem("https://cdn.exemplo.com/corte.png");
        item.setLegenda("Corte degradê");
        when(portfolioRepository.findAll()).thenReturn(List.of(item));

        List<Portfolio> resultado = portfolioService.listarTodos();

        assertThat(resultado).containsExactly(item);
    }

    @Test
    void salvar_devePersistirOItem() {
        Portfolio item = new Portfolio();
        item.setUrlImagem("https://cdn.exemplo.com/corte.png");
        item.setLegenda("Corte degradê");
        when(portfolioRepository.save(item)).thenReturn(item);

        Portfolio resultado = portfolioService.salvar(item);

        assertThat(resultado).isEqualTo(item);
    }

    @Test
    void deletar_deveLancarExcecao_quandoIdNaoExiste() {
        when(portfolioRepository.existsById(404L)).thenReturn(false);

        assertThatThrownBy(() -> portfolioService.deletar(404L))
                .hasMessageContaining("não encontrado");

        verify(portfolioRepository, never()).deleteById(404L);
    }

    @Test
    void deletar_deveRemover_quandoIdExiste() {
        when(portfolioRepository.existsById(1L)).thenReturn(true);

        portfolioService.deletar(1L);

        verify(portfolioRepository).deleteById(1L);
    }
}
