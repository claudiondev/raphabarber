package com.claudio.dev.raphabarber.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ServicoControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void listarTodos_devePermitirAcessoSemAutenticacao() throws Exception {
        mockMvc.perform(get("/servicos"))
                .andExpect(status().isOk());
    }

    @Test
    void criarServico_deveRejeitar_semToken() throws Exception {
        mockMvc.perform(post("/servicos")
                        .contentType("application/json")
                        .content("{\"nome\":\"Corte\",\"duracaoMinutos\":30,\"preco\":50.00}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void criarServico_deveRejeitar_comTokenDeCliente() throws Exception {
        String tokenCliente = registrarClienteERetornarToken("cliente.servico@teste.com", "senha123");

        mockMvc.perform(post("/servicos")
                        .header(AUTHORIZATION, "Bearer " + tokenCliente)
                        .contentType("application/json")
                        .content("{\"nome\":\"Corte\",\"duracaoMinutos\":30,\"preco\":50.00}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void criarServico_devePermitir_comTokenDeAdmin() throws Exception {
        mockMvc.perform(post("/servicos")
                        .header(AUTHORIZATION, "Bearer " + tokenAdmin())
                        .contentType("application/json")
                        .content("{\"nome\":\"Corte Degradê\",\"duracaoMinutos\":45,\"preco\":60.00}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Corte Degradê"));
    }

    @Test
    void atualizarServico_deveAlterarApenasOCampoEnviado() throws Exception {
        String tokenAdmin = tokenAdmin();
        String respostaCriacao = mockMvc.perform(post("/servicos")
                        .header(AUTHORIZATION, "Bearer " + tokenAdmin)
                        .contentType("application/json")
                        .content("{\"nome\":\"Barba\",\"duracaoMinutos\":20,\"preco\":30.00}"))
                .andReturn().getResponse().getContentAsString();
        Number id = JsonPath.read(respostaCriacao, "$.id");

        mockMvc.perform(put("/servicos/" + id)
                        .header(AUTHORIZATION, "Bearer " + tokenAdmin)
                        .contentType("application/json")
                        .content("{\"preco\":35.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preco").value(35.0))
                .andExpect(jsonPath("$.nome").value("Barba")); // não deveria mudar
    }

    @Test
    void deletarServico_deveRemover_quandoAdmin() throws Exception {
        String tokenAdmin = tokenAdmin();
        String respostaCriacao = mockMvc.perform(post("/servicos")
                        .header(AUTHORIZATION, "Bearer " + tokenAdmin)
                        .contentType("application/json")
                        .content("{\"nome\":\"Sobrancelha\",\"duracaoMinutos\":10,\"preco\":15.00}"))
                .andReturn().getResponse().getContentAsString();
        Number id = JsonPath.read(respostaCriacao, "$.id");

        mockMvc.perform(delete("/servicos/" + id).header(AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/servicos/" + id))
                .andExpect(status().isNotFound());
    }
}
