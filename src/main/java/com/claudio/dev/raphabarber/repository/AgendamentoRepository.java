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

    // Mesmo filtro de findAtivos(), restrito a um cliente
    @Query("SELECT a FROM Agendamento a WHERE a.cliente = :cliente AND a.status != 'CANCELADO'")
    List<Agendamento> findAtivosDoCliente(@Param("cliente") Usuario cliente);

    // Buscar agendamentos dentro de um intervalo de data - JOIN FETCH evita N+1 ao serializar o serviço de cada agendamento
    @Query("SELECT a FROM Agendamento a JOIN FETCH a.servico WHERE DATE(a.dataHora) = :data AND a.status != 'CANCELADO' ORDER BY a.dataHora")
    List<Agendamento> findByData(@Param("data") LocalDate data);

    // Buscar agendamentos do cliente em um período
    @Query("SELECT a FROM Agendamento a WHERE a.cliente = :cliente AND DATE(a.dataHora) >= :dataInicio AND DATE(a.dataHora) <= :dataFim ORDER BY a.dataHora")
    List<Agendamento> findByClienteAndPeriodo(
        @Param("cliente") Usuario cliente,
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim
    );

    // Candidatos a conflito de horário: todos os agendamentos ativos com início dentro da janela informada.
    // O cálculo de overlap real (que depende da duração de cada serviço) é feito em memória no AgendamentoService -
    // JPQL portável (H2/MySQL) não tem como comparar "dataHora + duracaoMinutos" de forma segura entre os dois dialetos.
    @Query("SELECT a FROM Agendamento a JOIN FETCH a.servico WHERE a.status != 'CANCELADO' AND a.dataHora BETWEEN :inicioJanela AND :fimJanela ORDER BY a.dataHora")
    List<Agendamento> findAtivosNoIntervalo(@Param("inicioJanela") LocalDateTime inicioJanela, @Param("fimJanela") LocalDateTime fimJanela);
}
