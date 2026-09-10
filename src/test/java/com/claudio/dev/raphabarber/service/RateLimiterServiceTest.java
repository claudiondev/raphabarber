package com.claudio.dev.raphabarber.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimiterServiceTest {

    private static final int LIMITE_MAXIMO = 5; // precisa bater com RateLimiterService.LIMITE_MAXIMO

    private RateLimiterService rateLimiterService;

    @BeforeEach
    void setUp() {
        rateLimiterService = new RateLimiterService();
    }

    @Test
    void excedeuLimite_deveRetornarFalse_enquantoDentroDoLimite() {
        for (int i = 0; i < LIMITE_MAXIMO; i++) {
            assertThat(rateLimiterService.excedeuLimite("login:1.2.3.4")).isFalse();
        }
    }

    @Test
    void excedeuLimite_deveRetornarTrue_aoUltrapassarOLimite() {
        for (int i = 0; i < LIMITE_MAXIMO; i++) {
            rateLimiterService.excedeuLimite("login:1.2.3.4");
        }

        assertThat(rateLimiterService.excedeuLimite("login:1.2.3.4")).isTrue();
    }

    @Test
    void excedeuLimite_deveSerIndependentePorIdentificador() {
        for (int i = 0; i <= LIMITE_MAXIMO; i++) {
            rateLimiterService.excedeuLimite("login:1.1.1.1");
        }

        // outro identificador (ex: outro IP ou outra rota) não é afetado pelas tentativas acima
        assertThat(rateLimiterService.excedeuLimite("login:2.2.2.2")).isFalse();
    }
}
