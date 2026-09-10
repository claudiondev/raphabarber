package com.claudio.dev.raphabarber.controller;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Garante que a documentação da API (Swagger/OpenAPI) continua acessível sem autenticação e que o
 * esquema de segurança "bearerAuth" está declarado - se o SecurityConfig um dia passar a exigir
 * token nessas rotas por engano, esse teste quebra e avisa.
 */
class OpenApiIntegrationTest extends AbstractIntegrationTest {

    @Test
    void especificacaoOpenApi_deveEstarAcessivelSemAutenticacao() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("RaphaBarber API"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"));
    }

    @Test
    void swaggerUi_deveEstarAcessivelSemAutenticacao() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
}
