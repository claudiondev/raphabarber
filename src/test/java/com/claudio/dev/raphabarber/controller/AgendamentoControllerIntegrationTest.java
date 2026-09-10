package com.claudio.dev.raphabarber.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AgendamentoControllerIntegrationTest extends AbstractIntegrationTest {

    private Number criarServico(String tokenAdmin, String nome, int duracaoMinutos) throws Exception {
        String resposta = mockMvc.perform(post("/servicos")
                        .header(AUTHORIZATION, "Bearer " + tokenAdmin)
                        .contentType("application/json")
                        .content("{\"nome\":\"" + nome + "\",\"duracaoMinutos\":" + duracaoMinutos + ",\"preco\":50.00}"))
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(resposta, "$.id");
    }

    private String horarioAmanha(int hora, int minuto) {
        return LocalDateTime.now().plusDays(1)
                .withHour(hora).withMinute(minuto).withSecond(0).withNano(0)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Test
    void criarAgendamento_deveFuncionar_quandoHorarioLivre() throws Exception {
        Number servicoId = criarServico(tokenAdmin(), "Corte", 60);
        String tokenCliente = registrarClienteERetornarToken("cliente.agenda1@teste.com", "senha123");

        mockMvc.perform(post("/agendamentos")
                        .header(AUTHORIZATION, "Bearer " + tokenCliente)
                        .contentType("application/json")
                        .content("{\"servico\":{\"id\":" + servicoId + "},\"dataHora\":\"" + horarioAmanha(10, 0) + "\"}"))
                .andExpect(status().isCreated());
    }

    // Este é o teste que prova a correção do bug: antes, só o timestamp exato era comparado,
    // então marcar 30 min depois de um corte de 60 min passava batido.
    @Test
    void criarAgendamento_deveRejeitar_quandoConflitaComDuracaoDeOutroServico() throws Exception {
        Number servicoId = criarServico(tokenAdmin(), "Corte Longo", 60);
        String tokenClienteA = registrarClienteERetornarToken("cliente.agenda2a@teste.com", "senha123");
        String tokenClienteB = registrarClienteERetornarToken("cliente.agenda2b@teste.com", "senha123");

        mockMvc.perform(post("/agendamentos")
                        .header(AUTHORIZATION, "Bearer " + tokenClienteA)
                        .contentType("application/json")
                        .content("{\"servico\":{\"id\":" + servicoId + "},\"dataHora\":\"" + horarioAmanha(14, 0) + "\"}"))
                .andExpect(status().isCreated());

        // 14:30 cai dentro do corte de 60 min que começou às 14:00
        mockMvc.perform(post("/agendamentos")
                        .header(AUTHORIZATION, "Bearer " + tokenClienteB)
                        .contentType("application/json")
                        .content("{\"servico\":{\"id\":" + servicoId + "},\"dataHora\":\"" + horarioAmanha(14, 30) + "\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void criarAgendamento_devePermitir_horarioLogoAposOFimDoAnterior() throws Exception {
        Number servicoId = criarServico(tokenAdmin(), "Corte", 60);
        String tokenClienteA = registrarClienteERetornarToken("cliente.agenda5a@teste.com", "senha123");
        String tokenClienteB = registrarClienteERetornarToken("cliente.agenda5b@teste.com", "senha123");

        mockMvc.perform(post("/agendamentos")
                        .header(AUTHORIZATION, "Bearer " + tokenClienteA)
                        .contentType("application/json")
                        .content("{\"servico\":{\"id\":" + servicoId + "},\"dataHora\":\"" + horarioAmanha(16, 0) + "\"}"))
                .andExpect(status().isCreated());

        // 17:00 é exatamente quando o corte de 60 min iniciado às 16:00 termina - não deve conflitar
        mockMvc.perform(post("/agendamentos")
                        .header(AUTHORIZATION, "Bearer " + tokenClienteB)
                        .contentType("application/json")
                        .content("{\"servico\":{\"id\":" + servicoId + "},\"dataHora\":\"" + horarioAmanha(17, 0) + "\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void buscarAgendamento_clienteNaoPodeVerAgendamentoDeOutroCliente() throws Exception {
        Number servicoId = criarServico(tokenAdmin(), "Corte", 30);
        String tokenClienteA = registrarClienteERetornarToken("cliente.agenda3a@teste.com", "senha123");
        String tokenClienteB = registrarClienteERetornarToken("cliente.agenda3b@teste.com", "senha123");

        String respostaCriacao = mockMvc.perform(post("/agendamentos")
                        .header(AUTHORIZATION, "Bearer " + tokenClienteA)
                        .contentType("application/json")
                        .content("{\"servico\":{\"id\":" + servicoId + "},\"dataHora\":\"" + horarioAmanha(9, 0) + "\"}"))
                .andReturn().getResponse().getContentAsString();
        Number agendamentoId = JsonPath.read(respostaCriacao, "$.id");

        mockMvc.perform(get("/agendamentos/" + agendamentoId)
                        .header(AUTHORIZATION, "Bearer " + tokenClienteB))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarAgendaDoDia_deveSerRestritaAoAdmin() throws Exception {
        String tokenCliente = registrarClienteERetornarToken("cliente.agenda4@teste.com", "senha123");
        String data = LocalDate.now().plusDays(1).toString();

        mockMvc.perform(get("/agendamentos/dia").param("data", data)
                        .header(AUTHORIZATION, "Bearer " + tokenCliente))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/agendamentos/dia").param("data", data)
                        .header(AUTHORIZATION, "Bearer " + tokenAdmin()))
                .andExpect(status().isOk());
    }
}
