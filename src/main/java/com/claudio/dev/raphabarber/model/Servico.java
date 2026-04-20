package com.claudio.dev.raphabarber.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "servicos")
@Data

public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do serviço é obrigatório")
    @Column(nullable = false)
    private String nome;

    @NotNull(message = "A duração é obrigatória")
    @Positive(message = "A duração deve ser maior que zero")
    @Column(nullable = false)
    private Integer duracaoMinutos;

    @NotNull(message = "O preço é obrigatório")
    @Positive(message = "O preço deve ser maior que zero")
    @Column(nullable = false)
    private BigDecimal preco;

    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    @Column(length = 500)
    private String descricao;
}