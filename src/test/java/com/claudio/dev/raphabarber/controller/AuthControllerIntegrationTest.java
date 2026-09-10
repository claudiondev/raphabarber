package com.claudio.dev.raphabarber.controller;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void registrar_deveCriarClienteComSucesso() throws Exception {
        mockMvc.perform(comIp(post("/auth/registrar"), proximoIpDeTeste())
                        .contentType("application/json")
                        .content("{\"email\":\"novo.cliente@teste.com\",\"senha\":\"senha123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").exists());
    }

    @Test
    void registrar_deveRejeitar_quandoEmailJaCadastrado() throws Exception {
        String email = "duplicado@teste.com";
        registrarClienteERetornarToken(email, "senha123");

        mockMvc.perform(comIp(post("/auth/registrar"), proximoIpDeTeste())
                        .contentType("application/json")
                        .content("{\"email\":\"" + email + "\",\"senha\":\"outrasenha\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("E-mail já cadastrado!"));
    }

    @Test
    void registrar_deveRejeitar_quandoSenhaMuitoCurta() throws Exception {
        mockMvc.perform(comIp(post("/auth/registrar"), proximoIpDeTeste())
                        .contentType("application/json")
                        .content("{\"email\":\"senhacurta@teste.com\",\"senha\":\"123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.senha").exists());
    }

    @Test
    void login_deveRetornarToken_quandoCredenciaisCorretas() throws Exception {
        String email = "login.ok@teste.com";
        registrarClienteERetornarToken(email, "senha123");

        mockMvc.perform(comIp(post("/auth/login"), proximoIpDeTeste())
                        .contentType("application/json")
                        .content("{\"email\":\"" + email + "\",\"senha\":\"senha123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("CLIENTE"));
    }

    @Test
    void login_deveRetornar401_quandoSenhaErrada() throws Exception {
        String email = "senha.errada@teste.com";
        registrarClienteERetornarToken(email, "senhaCorreta1");

        mockMvc.perform(comIp(post("/auth/login"), proximoIpDeTeste())
                        .contentType("application/json")
                        .content("{\"email\":\"" + email + "\",\"senha\":\"senhaErrada1\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_deveRetornar429_aposExcederLimiteDeTentativas() throws Exception {
        String ip = proximoIpDeTeste(); // mesmo IP em todas as chamadas, de propósito, para acionar o rate limit
        String corpo = "{\"email\":\"qualquer@teste.com\",\"senha\":\"senhaErrada\"}";

        for (int i = 0; i < 5; i++) {
            mockMvc.perform(comIp(post("/auth/login"), ip)
                            .contentType("application/json")
                            .content(corpo))
                    .andExpect(status().isUnauthorized());
        }

        mockMvc.perform(comIp(post("/auth/login"), ip)
                        .contentType("application/json")
                        .content(corpo))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.erro").exists());
    }
}
