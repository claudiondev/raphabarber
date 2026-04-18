package com.claudio.dev.raphabarber.repository;

import com.claudio.dev.raphabarber.model.Agendamento;
import com.claudio.dev.raphabarber.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    // Buscar agendamentos de um cliente específico
    List<Agendamento> findByCliente(Usuario cliente);

    // Buscar agendamentos que não foram cancelados
    @Query("SELECT a FROM Agendamento a WHERE a.status != 'CANCELADO'")
    List<Agendamento> findAtivos();

    // Buscar agendamentos dentro de um intervalo de data
    @Query("SELECT a FROM Agendamento a WHERE DATE(a.dataHora) = :data AND a.status != 'CANCELADO' ORDER BY a.dataHora")
    List<Agendamento> findByData(@Param("data") LocalDate data);

    // Buscar agendamentos do cliente em um período
    @Query("SELECT a FROM Agendamento a WHERE a.cliente = :cliente AND DATE(a.dataHora) >= :dataInicio AND DATE(a.dataHora) <= :dataFim ORDER BY a.dataHora")
    List<Agendamento> findByClienteAndPeriodo(
        @Param("cliente") Usuario cliente,
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim
    );

    // Verificar se há agendamento no horário específico
    @Query("SELECT COUNT(a) > 0 FROM Agendamento a WHERE a.dataHora = :dataHora AND a.status != 'CANCELADO'")
    boolean existsAgendamentoNoHorario(@Param("dataHora") LocalDateTime dataHora);
}
