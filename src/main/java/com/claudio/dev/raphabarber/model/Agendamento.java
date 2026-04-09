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
    private Servico servico;

    @Column(nullable = false)

    private LocalDateTime dataHora;
    private StatusAgendamento status;

}

