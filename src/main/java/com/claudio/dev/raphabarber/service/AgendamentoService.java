package com.claudio.dev.raphabarber.service;

import com.claudio.dev.raphabarber.model.Agendamento;
import com.claudio.dev.raphabarber.repository.AgendamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class AgendamentoService {
    private final AgendamentoRepository agendamentoRepository;

    public AgendamentoService(AgendamentoRepository agendamentoRepository){
        this.agendamentoRepository = agendamentoRepository;
    }

    public List<Agendamento> listarTodos(){
        return agendamentoRepository.findAll();
    }

    public Agendamento buscarPorId(Long id) {
        return agendamentoRepository.findById(id).orElseThrow();
    }

    public Agendamento save(Agendamento agendamento) {
        return agendamentoRepository.save(agendamento);
    }

}
