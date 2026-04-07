package com.claudio.dev.raphabarber.controller;

import com.claudio.dev.raphabarber.model.Servico;
import com.claudio.dev.raphabarber.service.ServicoService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/servicos")
public class ServicoController {

    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @GetMapping
    public List<Servico> listarTodos() {
        return servicoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Servico buscarPorId(@PathVariable Long id) {
        return servicoService.buscarPorId(id);
    }

    @PostMapping
    public Servico salvar(@RequestBody Servico servico) {
        return servicoService.save(servico);
    }
}