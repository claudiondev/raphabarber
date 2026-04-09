package com.claudio.dev.raphabarber.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity // Transforma a classe em uma tabela no banco de dados
@Table(name = "servicos") // Define o nome da tabela no MySQL
@Data // Cria Getters e Setters automaticamente

public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // O banco gera o ID automaticamente (1, 2, 3...)
    private Long id;

    @Column(nullable = false) // Campo obrigatório: não aceita valor vazio
    private String nome;

    @Column(nullable = false)

    private Integer duracaoMinutos;
}