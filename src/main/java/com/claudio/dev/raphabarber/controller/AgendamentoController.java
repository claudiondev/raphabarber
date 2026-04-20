package com.claudio.dev.raphabarber.controller;

import com.claudio.dev.raphabarber.exception.AcessoNegadoException;
import com.claudio.dev.raphabarber.model.Agendamento;
import com.claudio.dev.raphabarber.model.Usuario;
import com.claudio.dev.raphabarber.service.AgendamentoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/agendamentos")
public class AgendamentoController {
    private final AgendamentoService agendamentoService;

    public AgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    // Admin vê todos os agendamentos ativos; cliente vê apenas os seus
    @GetMapping
    public ResponseEntity<List<Agendamento>> listarTodos(Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        List<Agendamento> agendamentos = agendamentoService.listarTodos(usuarioLogado);
        return ResponseEntity.ok(agendamentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id, Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        try {
            Agendamento agendamento = agendamentoService.buscarPorId(id, usuarioLogado);
            return ResponseEntity.ok(agendamento);
        } catch (AcessoNegadoException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("erro", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Body: { "servico": { "id": 2 }, "dataHora": "2026-04-20T14:00:00" }
    // Um "cliente" enviado no body é ignorado - o dono do agendamento é sempre o usuário autenticado
    @PostMapping
    public ResponseEntity<?> criarAgendamento(@Valid @RequestBody Agendamento agendamento, Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        try {
            Agendamento novoAgendamento = agendamentoService.criarAgendamento(agendamento, usuarioLogado);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoAgendamento);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarAgendamento(
            @PathVariable Long id,
            @RequestBody Agendamento agendamentoAtualizado,
            Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        try {
            Agendamento agendamento = agendamentoService.atualizarAgendamento(id, agendamentoAtualizado, usuarioLogado);
            return ResponseEntity.ok(agendamento);
        } catch (AcessoNegadoException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("erro", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelarAgendamento(@PathVariable Long id, Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        try {
            Agendamento agendamento = agendamentoService.cancelarAgendamento(id, usuarioLogado);
            return ResponseEntity.ok(agendamento);
        } catch (AcessoNegadoException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("erro", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Agenda diária do Rapha - apenas admin
    @GetMapping("/dia")
    public ResponseEntity<?> listarAgendamentosDoDia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        try {
            List<Agendamento> agendamentos = agendamentoService.listarAgendamentosDoDia(data, usuarioLogado);
            return ResponseEntity.ok(agendamentos);
        } catch (AcessoNegadoException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("erro", e.getMessage()));
        }
    }
}
