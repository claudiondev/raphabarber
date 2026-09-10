package com.claudio.dev.raphabarber.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

// Teste unitário puro (sem contexto Spring) - o @Value do secret é injetado manualmente via reflection.
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "segredo-de-teste-com-mais-de-32-bytes-1234567890");
    }

    @Test
    void gerarTokenEExtrairEmail_deveFuncionarDeIdaEVolta() {
        String token = jwtService.gerarToken("cliente@teste.com");

        assertThat(jwtService.extrairEmail(token)).isEqualTo("cliente@teste.com");
        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    void extractSubject_deveRetornarNull_quandoTokenInvalido() {
        assertThat(jwtService.extractSubject("token-completamente-invalido")).isNull();
    }

    @Test
    void isTokenValid_deveRetornarFalse_quandoTokenMalformado() {
        assertThat(jwtService.isTokenValid("abc.def.ghi")).isFalse();
    }

    @Test
    void extractSubject_deveRetornarNull_quandoAssinadoComOutraChave() {
        JwtService outroServico = new JwtService();
        ReflectionTestUtils.setField(outroServico, "secret", "outra-chave-de-teste-com-mais-de-32-bytes-000000");
        String tokenDeOutraChave = outroServico.gerarToken("cliente@teste.com");

        assertThat(jwtService.extractSubject(tokenDeOutraChave)).isNull();
    }

    @Test
    void validarToken_deveConferirSeOEmailBateComOSubjectDoToken() {
        String token = jwtService.gerarToken("cliente@teste.com");

        assertThat(jwtService.validarToken(token, "cliente@teste.com")).isTrue();
        assertThat(jwtService.validarToken(token, "outro@teste.com")).isFalse();
    }
}
