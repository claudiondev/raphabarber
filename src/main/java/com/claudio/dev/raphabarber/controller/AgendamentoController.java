package com.claudio.dev.raphabarber.controller;


import com.claudio.dev.raphabarber.model.Agendamento;
import com.claudio.dev.raphabarber.service.AgendamentoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agendamentos")

public class AgendamentoController {
    private final AgendamentoService agendamentoService;

    public AgendamentoController(AgendamentoService agendamentoService) {this.agendamentoService = agendamentoService; }

    @GetMapping
    public List<Agendamento> listarTodos() {return agendamentoService.listarTodos(); }

    @GetMapping("/{id}")
    public Agendamento buscarPorId(@PathVariable Long id) {
        return agendamentoService.buscarPorId(id);
    }

    @PostMapping
    public Agendamento salvar(@RequestBody Agendamento agendamento) {return agendamentoService.save(agendamento);
    }
}
