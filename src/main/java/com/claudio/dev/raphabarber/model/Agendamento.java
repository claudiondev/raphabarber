package com.claudio.dev.raphabarber.model;

import jakarta.persistence.*;
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
    private Usuario cliente; // Qual cliente agendou

    @ManyToOne(optional = false)
    private Servico servico; // Qual serviço foi agendado

    @Column(nullable = false)
    private LocalDateTime dataHora; // Quando o agendamento será realizado

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusAgendamento status; // AGENDADO, CONCLUÍDO, CANCELADO

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataAgendamento; // Quando o agendamento foi criado

    @Column(nullable = false)
    private LocalDateTime dataUltimaAtualizacao; // Quando foi atualizado por último

    @PrePersist // Executado automaticamente ANTES de salvar no banco
    protected void onCreate() {
        dataAgendamento = LocalDateTime.now();
        dataUltimaAtualizacao = LocalDateTime.now();
        if (status == null) {
            status = StatusAgendamento.AGENDADO; // Status padrão é AGENDADO
        }
    }

    @PreUpdate // Executado automaticamente ANTES de atualizar no banco
    protected void onUpdate() {
        dataUltimaAtualizacao = LocalDateTime.now();
    }
}
