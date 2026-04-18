package com.claudio.dev.raphabarber.controller;

import com.claudio.dev.raphabarber.model.Servico;
import com.claudio.dev.raphabarber.service.ServicoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/servicos")
public class ServicoController {
    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    /**
     * Lista todos os serviços
     * GET /servicos
     */
    @GetMapping
    public ResponseEntity<List<Servico>> listarTodos() {
        List<Servico> servicos = servicoService.listarTodos();
        return ResponseEntity.ok(servicos);
    }

    /**
     * Busca um serviço por ID
     * GET /servicos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Servico> buscarPorId(@PathVariable Long id) {
        try {
            Servico servico = servicoService.buscarPorId(id);
            return ResponseEntity.ok(servico);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Cria um novo serviço
     * POST /servicos
     * {
     *   "nome": "Corte de Cabelo",
     *   "duracaoMinutos": 30,
     *   "preco": 50.00,
     *   "descricao": "Corte clássico com desenho"
     * }
     */
    @PostMapping
    public ResponseEntity<?> criarServico(@RequestBody Servico servico) {
        try {
            Servico novoServico = servicoService.criarServico(servico);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoServico);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Atualiza um serviço existente
     * PUT /servicos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarServico(
            @PathVariable Long id,
            @RequestBody Servico servicoAtualizado) {
        try {
            Servico servico = servicoService.atualizarServico(id, servicoAtualizado);
            return ResponseEntity.ok(servico);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Deleta um serviço
     * DELETE /servicos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarServico(@PathVariable Long id) {
        try {
            servicoService.deletarServico(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}