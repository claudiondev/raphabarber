package com.claudio.dev.raphabarber.controller;

import com.claudio.dev.raphabarber.model.Portfolio;
import com.claudio.dev.raphabarber.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portfolio")
@Tag(name = "Portfólio", description = "Galeria de cortes da barbearia - leitura pública, escrita restrita a ADMIN")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public ResponseEntity<List<Portfolio>> listarTodos() {
        return ResponseEntity.ok(portfolioService.listarTodos());
    }

    @Operation(summary = "Adicionar item ao portfólio", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<Portfolio> adicionar(@Valid @RequestBody Portfolio portfolio) {
        Portfolio novoItem = portfolioService.salvar(portfolio);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoItem);
    }

    @Operation(summary = "Remover item do portfólio", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            portfolioService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}