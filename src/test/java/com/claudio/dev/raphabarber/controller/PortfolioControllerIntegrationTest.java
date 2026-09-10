package com.claudio.dev.raphabarber.controller;

import org.junit.jupiter.api.Test;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PortfolioControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void listarTodos_devePermitirAcessoPublico() throws Exception {
        mockMvc.perform(get("/portfolio"))
                .andExpect(status().isOk());
    }

    @Test
    void adicionar_deveRejeitar_semToken() throws Exception {
        mockMvc.perform(post("/portfolio")
                        .contentType("application/json")
                        .content("{\"urlImagem\":\"https://cdn.exemplo.com/corte.png\",\"legenda\":\"Degradê\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adicionar_devePermitir_comTokenDeAdmin() throws Exception {
        mockMvc.perform(post("/portfolio")
                        .header(AUTHORIZATION, "Bearer " + tokenAdmin())
                        .contentType("application/json")
                        .content("{\"urlImagem\":\"https://cdn.exemplo.com/corte.png\",\"legenda\":\"Degradê\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void adicionar_deveRejeitar_quandoUrlNaoEhImagemHttps() throws Exception {
        mockMvc.perform(post("/portfolio")
                        .header(AUTHORIZATION, "Bearer " + tokenAdmin())
                        .contentType("application/json")
                        .content("{\"urlImagem\":\"javascript:alert(1)\",\"legenda\":\"Degradê\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deletar_deveRetornar404_quandoIdNaoExiste() throws Exception {
        mockMvc.perform(delete("/portfolio/999999").header(AUTHORIZATION, "Bearer " + tokenAdmin()))
                .andExpect(status().isNotFound());
    }
}
