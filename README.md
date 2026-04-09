# ✂️ Rapha Barber — Sistema de Barbearia

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green?style=for-the-badge&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-Database-blue?style=for-the-badge&logo=mysql)
![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow?style=for-the-badge)

> Sistema Full Stack para gerenciamento de barbearia — agendamentos, serviços, portfólio e painel administrativo.

---

## 📋 Sobre o Projeto

Sistema desenvolvido para o Rapha gerenciar sua barbearia de forma profissional. Permite cadastrar serviços, organizar agendamentos e exibir um portfólio de trabalhos para os clientes.

---

## ✨ Funcionalidades

- 📅 Agendamento de serviços com controle de horários
- ✂️ Cadastro de serviços e valores
- 👤 Autenticação de clientes e administrador
- 🖼️ Portfólio de trabalhos (fotos e vídeos)
- 📧 E-mail de boas-vindas e recuperação de senha
- 🔐 Segurança com Spring Security + JWT

---

## 🔗 Principais Endpoints

### ✂️ Serviços
- `GET /servicos` → Listar serviços
- `POST /servicos` → Cadastrar serviço
- `GET /servicos/{id}` → Buscar por ID

### 📅 Agendamentos
- `GET /agendamentos` → Listar agendamentos
- `POST /agendamentos` → Criar agendamento
- `GET /agendamentos/{id}` → Buscar por ID

---

## 📁 Estrutura de Pacotes

```
src/main/java/com/claudio/dev/raphabarber
├── controller   # Endpoints da API
├── service      # Regras de negócio
├── repository   # Acesso ao banco de dados
└── model        # Entidades (JPA)
```

---

## 🚀 Tecnologias Utilizadas

| Tecnologia | Descrição |
|---|---|
| Java 17 | Linguagem principal |
| Spring Boot 3.5 | Framework da API |
| Spring Security + JWT | Autenticação segura |
| Spring Data JPA | Comunicação com banco |
| MySQL | Banco de dados relacional |
| Railway | Deploy (em breve) |

---

## 📌 Autor

**Claudio Nascimento**
🔗 https://github.com/claudiondev