package com.claudio.dev.raphabarber.controller;

import com.claudio.dev.raphabarber.model.Agendamento;
import com.claudio.dev.raphabarber.service.AgendamentoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/agendamentos")
public class AgendamentoController {
    private final AgendamentoService agendamentoService;

    public AgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    /**
     * Lista todos os agendamentos ativos
     */
    @GetMapping
    public ResponseEntity<List<Agendamento>> listarTodos() {
        List<Agendamento> agendamentos = agendamentoService.listarTodos();
        return ResponseEntity.ok(agendamentos);
    }

    /**
     * Busca um agendamento por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Agendamento> buscarPorId(@PathVariable Long id) {
        try {
            Agendamento agendamento = agendamentoService.buscarPorId(id);
            return ResponseEntity.ok(agendamento);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Cria um novo agendamento
     * POST /agendamentos
     * {
     *   "cliente": { "id": 1 },
     *   "servico": { "id": 2 },
     *   "dataHora": "2026-04-20T14:00:00"
     * }
     */
    @PostMapping
    public ResponseEntity<?> criarAgendamento(@RequestBody Agendamento agendamento) {
        try {
            Agendamento novoAgendamento = agendamentoService.criarAgendamento(agendamento);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoAgendamento);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Atualiza um agendamento existente
     * PUT /agendamentos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarAgendamento(
            @PathVariable Long id,
            @RequestBody Agendamento agendamentoAtualizado) {
        try {
            Agendamento agendamento = agendamentoService.atualizarAgendamento(id, agendamentoAtualizado);
            return ResponseEntity.ok(agendamento);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Cancela um agendamento
     * DELETE /agendamentos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelarAgendamento(@PathVariable Long id) {
        try {
            Agendamento agendamento = agendamentoService.cancelarAgendamento(id);
            return ResponseEntity.ok(agendamento);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lista agendamentos de um dia específico (Agenda diária do Rapha)
     * GET /agendamentos/dia?data=2026-04-20
     */
    @GetMapping("/dia")
    public ResponseEntity<List<Agendamento>> listarAgendamentosDoDia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        List<Agendamento> agendamentos = agendamentoService.listarAgendamentosDoDia(data);
        return ResponseEntity.ok(agendamentos);
    }
}
