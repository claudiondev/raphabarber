package com.claudio.dev.raphabarber.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "agendamentos")
@Data

public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Usuario cliente;

    @NotNull(message = "O serviço é obrigatório")
    @ManyToOne(optional = false)
    private Servico servico;

    @NotNull(message = "A data e hora são obrigatórias")
    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusAgendamento status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataAgendamento;

    @Column(nullable = false)
    private LocalDateTime dataUltimaAtualizacao;

    @PrePersist
    protected void onCreate() {
        dataAgendamento = LocalDateTime.now();
        dataUltimaAtualizacao = LocalDateTime.now();
        if (status == null) {
            status = StatusAgendamento.AGENDADO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dataUltimaAtualizacao = LocalDateTime.now();
    }
}
