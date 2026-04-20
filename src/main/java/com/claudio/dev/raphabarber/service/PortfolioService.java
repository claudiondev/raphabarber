package com.claudio.dev.raphabarber.service;

import com.claudio.dev.raphabarber.model.Portfolio;
import com.claudio.dev.raphabarber.repository.PortfolioRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    public PortfolioService(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    public List<Portfolio> listarTodos() {
        return portfolioRepository.findAll();
    }

    public Portfolio salvar(Portfolio portfolio) {
        return portfolioRepository.save(portfolio);
    }

    public void deletar(Long id) {
        if (!portfolioRepository.existsById(id)) {
            throw new RuntimeException("Item de portfólio não encontrado.");
        }
        portfolioRepository.deleteById(id);
    }
}