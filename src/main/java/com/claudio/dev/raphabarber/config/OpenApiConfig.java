package com.claudio.dev.raphabarber.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

// Define os metadados exibidos no Swagger UI (/swagger-ui/index.html) e o esquema de autenticação Bearer/JWT,
// necessário para o botão "Authorize" funcionar contra os endpoints protegidos.
// Sem @SecurityRequirement global de propósito: nem todo endpoint exige token (ex: GET /servicos é público) -
// os que exigem declaram @SecurityRequirement(name = "bearerAuth") individualmente no controller.
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "RaphaBarber API",
                version = "v1",
                description = "API REST para gerenciamento de uma barbearia: autenticação, serviços, "
                        + "agendamentos e portfólio de cortes.",
                contact = @Contact(name = "Claudio Nascimento", url = "https://github.com/claudiondev")
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER,
        description = "Token JWT obtido em POST /auth/login. Envie como 'Bearer {token}'."
)
public class OpenApiConfig {
}
