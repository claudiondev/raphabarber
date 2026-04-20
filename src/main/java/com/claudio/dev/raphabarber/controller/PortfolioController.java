package com.claudio.dev.raphabarber.controller;

import com.claudio.dev.raphabarber.model.Portfolio;
import com.claudio.dev.raphabarber.service.PortfolioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public ResponseEntity<List<Portfolio>> listarTodos() {
        return ResponseEntity.ok(portfolioService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<Portfolio> adicionar(@Valid @RequestBody Portfolio portfolio) {
        Portfolio novoItem = portfolioService.salvar(portfolio);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoItem);
    }

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