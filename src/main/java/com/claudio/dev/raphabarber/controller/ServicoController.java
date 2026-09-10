package com.claudio.dev.raphabarber.controller;

import com.claudio.dev.raphabarber.model.Servico;
import com.claudio.dev.raphabarber.service.ServicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/servicos")
@Tag(name = "Serviços", description = "Catálogo de serviços da barbearia - leitura pública, escrita restrita a ADMIN")
public class ServicoController {
    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @GetMapping
    public ResponseEntity<List<Servico>> listarTodos() {
        List<Servico> servicos = servicoService.listarTodos();
        return ResponseEntity.ok(servicos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Servico> buscarPorId(@PathVariable Long id) {
        try {
            Servico servico = servicoService.buscarPorId(id);
            return ResponseEntity.ok(servico);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Body: { "nome": "Corte de Cabelo", "duracaoMinutos": 30, "preco": 50.00, "descricao": "..." }
    @Operation(summary = "Criar serviço", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<?> criarServico(@Valid @RequestBody Servico servico) {
        try {
            Servico novoServico = servicoService.criarServico(servico);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoServico);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // update parcial: só altera os campos enviados no body, por isso não usa @Valid aqui
    @Operation(summary = "Atualizar serviço (parcial)", security = @SecurityRequirement(name = "bearerAuth"))
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

    @Operation(summary = "Excluir serviço", security = @SecurityRequirement(name = "bearerAuth"))
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