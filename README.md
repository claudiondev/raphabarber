# ✂️ RaphaBarber — Sistema Inteligente de Barbearia

<div align="center">

![Java](https://img.shields.io/badge/Java%2017-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot%203-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL%208.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

![Status](https://img.shields.io/badge/Status-✅%20Production%20Ready-brightgreen?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)

**Uma plataforma completa para gerenciar sua barbearia com segurança, profissionalismo e facilidade.** 💈✨

[![Acesse o Sistema](https://img.shields.io/badge/Acesse_o_Sistema-Clique_Aqui-blue?style=for-the-badge&logo=vercel)](https://raphabarbercg-front.vercel.app/)

</div>

---

## 📖 Sobre o Projeto

O **RaphaBarber** é um sistema Full Stack moderno desenvolvido para barbearias gerenciarem seus negócios de forma eficiente. Com autenticação segura, agendamentos inteligentes e um painel administrativo, o Rapha pode focar no que faz de melhor: cortes impecáveis! 💇

> **Fase Atual:** Sistema completo (Frontend + Backend) com toda a lógica de segurança, serviços e agendamentos implementada e em produção. 🚀

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

raphabarber/
├── src/main/java/com/claudio/dev/raphabarber/
│   ├── controller/        🌐 Endpoints HTTP
│   │   ├── AuthController.java
│   │   ├── ServicoController.java
│   │   └── AgendamentoController.java
│   │
│   ├── service/               🧠 Lógica de Negócio
│   │   ├── UsuarioService.java
│   │   ├── ServicoService.java
│   │   ├── AgendamentoService.java
│   │   └── JwtService.java
│   │
│   ├── repository/           🗄️ Acesso ao Banco
│   │   ├── UsuarioRepository.java
│   │   ├── ServicoRepository.java
│   │   └── AgendamentoRepository.java
│   │
│   ├── model/                📦 Entidades (JPA)
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


### 📊 Fluxo de Dados

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
| **Spring Boot** | 3.x | Framework Web/Rest |
| **Spring Security** | 6.x | Autenticação |
| **Spring Data JPA** | 3.x | ORM Hibernate |
| **JWT (JJWT)** | 0.11.5 | Tokens seguros |
| **MySQL** | 8.0+ | Banco de dados |
| **Maven** | 3.9+ | Build & Dependências |

</div>

### Frontend
<div align="center">

| Tecnologia | Função |
|---|---|
| **React.js** | Interface do Usuário |
| **Vite** | Build Tool rápida |
| **Tailwind CSS** | Estilização Responsiva |
| **Axios** | Consumo da API |

</div>

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
- ✅ Classe Servico e Agendamento
- ✅ Métodos de agendamento e visualização de horários
- ✅ Validações de conflito de horários

### ✅ Fase 3: Interface (Frontend)
- ✅ Projeto React com Vite e Tailwind CSS
- ✅ Tela de login/cadastro integrada ao JWT
- ✅ Painel de agendamentos funcional (Admin e Cliente)
- ✅ Consumo de rotas protegidas com Axios

### ✅ Fase 4: Lançamento
- ✅ Deploy do banco e API (Railway)
- ✅ Deploy do Frontend (Vercel)
- ✅ Vídeo de demonstração (LinkedIn)

---

## 🧪 Como Testar Localmente

### Pré-requisitos
- MySQL rodando localmente
- Java 17+
- Node.js instalado

### Configuração Rápida

1. **Clone o repositório:**
```bash
git clone [https://github.com/claudiondev/raphabarber.git](https://github.com/claudiondev/raphabarber.git)
Backend: Configure o application.properties com suas credenciais do MySQL e rode:

Bash
./mvnw spring-boot:run
Frontend:

Bash
npm install
npm run dev




👨‍💻 Autor
Claudio Nascimento

🔗 GitHub: @claudiondev

💼 LinkedIn: Claudio Nascimento

📧 Email: claudinhon152@gmail.com

⭐ Se este projeto foi útil, deixe uma star! ⭐

Desenvolvido para a RaphaBarber!

Status: 🟢 Production Ready | Última atualização: Maio de 2026
