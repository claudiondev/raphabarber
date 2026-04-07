package com.claudio.dev.raphabarber.service;

import com.claudio.dev.raphabarber.model.Servico;
import com.claudio.dev.raphabarber.repository.ServicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class ServicoService {
    private final ServicoRepository servicoRepository;

    public ServicoService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

public List<Servico> listarTodos(){
        return servicoRepository.findAll();
}

public Servico buscarPorId(Long id) {
    return servicoRepository.findById(id).orElseThrow();
}

public Servico save(Servico servico) {
        return servicoRepository.save(servico);
}
}
