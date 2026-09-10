package com.claudio.dev.raphabarber.controller;

import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Base para os testes de integração dos controllers: sobe o contexto Spring completo (Security, filtro JWT,
 * exception handler) com um banco H2 em memória e MockMvc no lugar de um servidor HTTP real.
 *
 * @Transactional garante que cada teste roda dentro de uma transação revertida ao final - MockMvc executa a
 * requisição na mesma thread do teste, então as chamadas ao banco feitas pelo controller entram nessa mesma
 * transação e são desfeitas automaticamente, sem precisar limpar tabelas manualmente entre os testes.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
abstract class AbstractIntegrationTest {

    // Compartilhado por TODAS as subclasses (campo estático) - garante um IP diferente por chamada de
    // /auth/login ou /auth/registrar em toda a suíte, já que o RateLimiterService é um bean singleton cujo
    // estado em memória não é resetado pelo rollback transacional entre testes.
    private static final AtomicInteger CONTADOR_IP = new AtomicInteger(0);

    @Autowired
    protected MockMvc mockMvc;

    @Value("${app.admin.email}")
    protected String adminEmail;

    @Value("${app.admin.password}")
    protected String adminPassword;

    protected String proximoIpDeTeste() {
        // faixa reservada para documentação/teste (RFC 5737) - nunca é um IP real
        return "203.0.113." + CONTADOR_IP.incrementAndGet();
    }

    protected MockHttpServletRequestBuilder comIp(MockHttpServletRequestBuilder builder, String ip) {
        return builder.with(request -> {
            request.setRemoteAddr(ip);
            return request;
        });
    }

    protected String login(String email, String senha) throws Exception {
        String body = "{\"email\":\"" + email + "\",\"senha\":\"" + senha + "\"}";

        String resposta = mockMvc.perform(comIp(post("/auth/login"), proximoIpDeTeste())
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(resposta, "$.token");
    }

    protected String registrarClienteERetornarToken(String email, String senha) throws Exception {
        String body = "{\"email\":\"" + email + "\",\"senha\":\"" + senha + "\"}";

        mockMvc.perform(comIp(post("/auth/registrar"), proximoIpDeTeste())
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk());

        return login(email, senha);
    }

    protected String tokenAdmin() throws Exception {
        return login(adminEmail, adminPassword);
    }
}
