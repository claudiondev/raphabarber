package com.claudio.dev.raphabarber.repository;

import com.claudio.dev.raphabarber.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
}
