# ✂️ RaphaBarber — Sistema Inteligente de Barbearia

<div align="center">

![Java](https://img.shields.io/badge/Java%2017-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot%203.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL%208.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

![Status](https://img.shields.io/badge/Status-✅%20Production%20Ready-brightgreen?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)

**Uma plataforma completa para gerenciar sua barbearia com segurança, profissionalismo e facilidade.** 💈✨

</div>

---

## 📖 Sobre o Projeto

O **RaphaBarber** é um sistema Full Stack moderno desenvolvido para barbearias gerenciarem seus negócios de forma eficiente. Com autenticação segura, agendamentos inteligentes e um painel administrativo, o Rapha pode focar no que faz de melhor: cortes impecáveis! 💇

> **Fase Atual:** Backend 100% funcional com toda a lógica de segurança, serviços e agendamentos implementada. 🚀

---

## ✨ Funcionalidades Principais

### 👤 Autenticação & Segurança
- ✅ Registro seguro de usuários (clientes e admin)
- ✅ Login com geração de **JWT Token**
- ✅ Senhas criptografadas (bcrypt)
- ✅ Controle de acesso por Roles (ADMIN/CLIENTE)
- ✅ Token com expiração de 1 hora

### ✂️ Gerenciamento de Serviços
- ✅ CRUD completo de serviços
- ✅ Definição de preços e duração
- ✅ Descrições detalhadas
- ✅ Validações profissionais

### 📅 Sistema de Agendamentos
- ✅ Agendamento com validação de data/hora
- ✅ Controle de disponibilidade (sem conflitos)
- ✅ Status do agendamento (AGENDADO, CONCLUÍDO, CANCELADO)
- ✅ Agenda diária do Rapha
- ✅ Histórico completo de cada agendamento
- ✅ Atualização e cancelamento de agendamentos

### 🛡️ Segurança & Validações
- ✅ Spring Security integrado
- ✅ Validação em todas as camadas (Controller → Service → Database)
- ✅ Mensagens de erro claras e úteis
- ✅ Proteção contra SQL Injection (JPA)

---

## 🏗️ Arquitetura e Estrutura

```
raphabarber/
├── src/main/java/com/claudio/dev/raphabarber/
│   ├── controller/          🌐 Endpoints HTTP
│   │   ├── AuthController.java
│   │   ├── ServicoController.java
│   │   └── AgendamentoController.java
│   │
│   ├── service/             🧠 Lógica de Negócio
│   │   ├── UsuarioService.java
│   │   ├── ServicoService.java
│   │   ├── AgendamentoService.java
│   │   └── JwtService.java
│   │
│   ├── repository/          🗄️ Acesso ao Banco
│   │   ├── UsuarioRepository.java
│   │   ├── ServicoRepository.java
│   │   └── AgendamentoRepository.java
│   │
│   ├── model/               📦 Entidades (JPA)
│   │   ├── Usuario.java
│   │   ├── Servico.java
│   │   ├── Agendamento.java
│   │   ├── StatusAgendamento.java
│   │   └── UserRole.java
│   │
│   └── RaphabarberApplication.java
│
└── src/main/resources/
    └── application.properties    ⚙️ Configurações
```

### 📊 Fluxo de Dados

```
Cliente HTTP Request
        ↓
    Controller (Valida entrada)
        ↓
    Service (Lógica de negócio)
        ↓
    Repository (JPA)
        ↓
    MySQL Database
        ↓
    Response JSON 200/400/404
```

---

## 🔌 Endpoints da API

### 🔑 **Autenticação** (`/auth`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/registrar` | Registrar novo usuário |
| POST | `/login` | Fazer login (retorna JWT) |

### ✂️ **Serviços** (`/servicos`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/servicos` | Listar todos os serviços |
| GET | `/servicos/{id}` | Buscar serviço por ID |
| POST | `/servicos` | Criar novo serviço ⭐ |
| PUT | `/servicos/{id}` | Atualizar serviço ⭐ |
| DELETE | `/servicos/{id}` | Deletar serviço ⭐ |

### 📅 **Agendamentos** (`/agendamentos`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/agendamentos` | Listar agendamentos ativos |
| GET | `/agendamentos/{id}` | Buscar agendamento por ID |
| GET | `/agendamentos/dia?data=YYYY-MM-DD` | 📅 Agenda do dia (Rapha) |
| POST | `/agendamentos` | Criar novo agendamento |
| PUT | `/agendamentos/{id}` | Atualizar agendamento |
| DELETE | `/agendamentos/{id}` | Cancelar agendamento |

⭐ Requer autenticação (token JWT)

---

## 🚀 Tecnologias Stack

### Backend
<div align="center">

| Tecnologia | Versão | Função |
|---|---|---|
| **Java** | 17 | Linguagem principal |
| **Spring Boot** | 3.5.13 | Framework Web/Rest |
| **Spring Security** | 6.x | Autenticação |
| **Spring Data JPA** | 3.5 | ORM Hibernate |
| **JWT (JJWT)** | 0.11.5 | Tokens seguros |
| **MySQL** | 8.0+ | Banco de dados |
| **Maven** | 3.9+ | Build & Dependências |

</div>

### Database
- **MySQL 8.0+**
- **Hibernate (JPA)** para mapeamento objeto-relacional
- **Queries customizadas** com `@Query`

### Segurança
- 🔐 **Spring Security** para autenticação/autorização
- 🔑 **JWT (JSON Web Tokens)** para sessões
- 🔒 **BCrypt** para hash de senhas
- ✅ **Validações em camadas** (Controller → Service)

---

## 📋 Checklist de Funcionalidades

### ✅ Fase 1: Motor e Segurança
- ✅ Repositório e dependências
- ✅ Banco de dados MySQL
- ✅ Spring Security + JWT
- ✅ AuthController (Cadastro/Login)

### ✅ Fase 2: Coração do Negócio
- ✅ Classe Servico (criado e tabela criada)
- ✅ Classe Agendamento (criado e tabela criada)
- ✅ Métodos de agendamento
- ✅ Métodos de visualização de horários
- ✅ Validações completas

### ⏳ Fase 3: Interface (Frontend)
- ⏳ Projeto React
- ⏳ Tailwind CSS
- ⏳ Página inicial pública
- ⏳ Tela de login/cadastro
- ⏳ Painel de agendamentos

### ⏳ Fase 4: Lançamento
- ⏳ Deploy do banco (Railway/AWS)
- ⏳ Deploy da API (Railway/Heroku)
- ⏳ Deploy do Frontend (Vercel/Netlify)
- ⏳ Testes em produção

---

## 🧪 Como Testar os Endpoints

### Pré-requisitos
- MySQL rodando localmente
- Postman ou Insomnia instalado
- Java 17+

### Configuração Rápida

1. **Clone o repositório:**
```bash
git clone https://github.com/claudiondev/raphabarber.git
cd raphabarber
```

2. **Configure o banco de dados** (em `application.properties`):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/raphabarber
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

3. **Compile e rode:**
```bash
./mvnw spring-boot:run
```

4. **Teste os endpoints:**
- 📍 URL base: `http://localhost:8080`
- 📄 Veja o arquivo `GUIA_TESTES_POSTMAN.md` para exemplos completos

---

## 📚 Conceitos Implementados

### Backend Architecture
- 🏗️ **MVC Pattern** - Model, View, Controller
- 🔄 **RESTful API Design** - GET, POST, PUT, DELETE
- 🧩 **Dependency Injection** - Spring IoC
- 🛡️ **Exception Handling** - Try-catch com ResponseEntity
- ✅ **Validação em Camadas** - Controller → Service → Repository

### Banco de Dados
- 📊 **ORM com JPA/Hibernate** - Mapeamento automático
- 🔗 **Relacionamentos** - @ManyToOne, @OneToMany
- 📅 **Timestamps** - @PrePersist, @PreUpdate
- 🔍 **Queries Customizadas** - @Query com JPQL

### Segurança
- 🔐 **Autenticação com JWT** - Stateless, escalável
- 👥 **Autorização por Roles** - ADMIN, CLIENTE
- 🔒 **Criptografia de Senha** - BCrypt
- 🛡️ **CSRF Protection** - Spring Security default

---

## 🎓 Aprendizados Principais

Este projeto demonstra:
- ✅ Desenvolvimento Full Stack moderno
- ✅ API RESTful profissional
- ✅ Autenticação segura com JWT
- ✅ Validações robustas
- ✅ Boas práticas de código limpo
- ✅ Tratamento de erros apropriado
- ✅ Organização em camadas (Controller/Service/Repository)

---

## 📞 Suporte

### Dúvidas sobre implementação?
Consulte os arquivos de documentação:
- 📖 `GUIA_TESTES_POSTMAN.md` - Exemplos de requisições
- 📚 `EXPLICACAO_ATUALIZACOES_FASE2B.md` - Detalhes técnicos

---

## 👨‍💻 Autor

**Claudio Nascimento**

- 🔗 GitHub: [@claudiondev](https://github.com/claudiondev)
- 💼 LinkedIn: [Claudio Nascimento](https://linkedin.com/in/claudiondev)
- 📧 Email: claudio.dev@example.com

---

## 📄 Licença - ⚠️ IMPORTANTE

**LICENÇA PROPRIETÁRIA - VISUALIZAÇÃO APENAS**

Este código é um **projeto de portfólio** protegido por direitos autorais.

### ✅ Permitido:
- 👀 Visualizar e estudar o código
- 💼 Usar como referência em entrevistas
- 📚 Aprender com as implementações

### ❌ Proibido:
- 🚫 Copiar ou usar comercialmente
- 🚫 Criar sistemas concorrentes
- 🚫 Distribuir sem permissão
- 🚫 Remover avisos de copyright

**Para uso comercial:** Entre em contato com o autor

📖 Veja o arquivo `LICENSE.md` para termos completos.

```
Copyright © 2026 Claudio Nascimento. Todos os direitos reservados.
```

---

<div align="center">

### ⭐ Se este projeto foi útil, deixe uma star! ⭐

**Desenvolvido com ❤️ para a RaphaBarber**

*Status: 🟢 Production Ready | Última atualização: 2026-04-16*

</div>
