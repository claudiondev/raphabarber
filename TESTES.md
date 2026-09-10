# Testes automatizados

## Como rodar

```bash
./mvnw test
```

Não precisa de MySQL rodando: os testes usam um banco H2 em memória, configurado só para o escopo de teste em `src/test/resources/application.properties` (schema recriado do zero a cada execução via `ddl-auto=create-drop`).

## Estratégia

A suíte tem duas camadas, cada uma cobrindo um tipo de risco diferente:

### Testes unitários (`src/test/java/.../service/*Test.java`)

Sem contexto Spring — usam Mockito para isolar cada `Service` das suas dependências (repositórios). Rápidos (a suíte inteira roda em segundos) e focados em regra de negócio:

- **`AgendamentoServiceTest`** — o mais importante. Cobre a correção do bug de conflito de horário: antes, a checagem de disponibilidade só comparava o timestamp exato (`existsAgendamentoNoHorario`), então marcar um horário 30 minutos depois de um corte de 60 minutos passava batido. Os testes provam o overlap real considerando a duração do serviço (incluindo o caso de borda "começa exatamente quando o anterior termina", que deve ser permitido), além de: data no passado, serviço inexistente, cliente só enxerga os próprios agendamentos, cliente não pode alterar `status`, e as regras de propriedade (`AcessoNegadoException`) em buscar/atualizar/cancelar.
- **`ServicoServiceTest`** — validações de nome/duração/preço e a semântica de update parcial (só altera o que veio preenchido).
- **`PortfolioServiceTest`** — deletar item inexistente lança exceção.
- **`JwtServiceTest`** — geração e extração do e-mail do token; token assinado com outra chave, malformado ou inválido retorna `null` em vez de estourar exceção.
- **`RateLimiterServiceTest`** — bloqueia a partir da tentativa que excede o limite configurado; identificadores diferentes (ex: IPs diferentes) não interferem entre si.

### Testes de integração (`src/test/java/.../controller/*IntegrationTest.java`)

Sobem o contexto Spring completo (`@SpringBootTest` + `MockMvc`) e batem nos endpoints de verdade, passando por toda a cadeia: `SecurityConfig` → `JwtAuthenticationFilter` → `Controller` → `Service` → `Repository` → H2 → `GlobalExceptionHandler`. Servem para garantir que a segurança e as regras HTTP (status code, corpo da resposta) estão corretas de ponta a ponta, não só na lógica isolada do service.

- **`AuthControllerIntegrationTest`** — registro (sucesso, e-mail duplicado, senha curta), login (sucesso com token, senha errada → 401) e rate limiting (6ª tentativa seguida → 429).
- **`ServicoControllerIntegrationTest`** — leitura pública; escrita (`POST`/`PUT`/`DELETE`) exige `ROLE_ADMIN` (403 para cliente comum).
- **`AgendamentoControllerIntegrationTest`** — prova o bug fix de ponta a ponta (segunda marcação dentro da duração do primeiro serviço é rejeitada com 400), cliente não acessa agendamento de outro cliente (403), `/agendamentos/dia` restrito a admin.
- **`PortfolioControllerIntegrationTest`** — leitura pública, escrita restrita a admin, validação da URL da imagem, 404 ao deletar item inexistente.
- **`OpenApiIntegrationTest`** — garante que a documentação (`/v3/api-docs`, `/swagger-ui/index.html`) continua acessível sem autenticação e que o esquema `bearerAuth` está declarado corretamente.

Cada teste roda dentro de uma transação revertida ao final (`@Transactional` em `AbstractIntegrationTest`) — não precisa limpar tabelas manualmente entre os testes. Como o `RateLimiterService` é um bean singleton com estado em memória (não é resetado pelo rollback), cada chamada a `/auth/login` ou `/auth/registrar` usa um IP simulado diferente (faixa `203.0.113.0/24`, reservada para documentação pela RFC 5737), exceto no teste que verifica o próprio rate limit, que usa o mesmo IP de propósito.

## O que ainda não está coberto

- Testes de carga/concorrência real (dois agendamentos simultâneos disputando o mesmo horário) — a validação atual não usa lock otimista/pessimista no banco, então uma race condition teórica ainda existe sob alta concorrência.
- Cobertura de código não é medida automaticamente (sem JaCoCo configurado ainda).
