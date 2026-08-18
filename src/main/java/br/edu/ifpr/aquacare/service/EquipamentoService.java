package br.edu.ifpr.aquacare.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.aquacare.entity.Equipamento;
import br.edu.ifpr.aquacare.repository.EquipamentoRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class EquipamentoService {
    
    private final EquipamentoRepository equipamentoRepository;

    public EquipamentoService(EquipamentoRepository equipamentoRepository){
        this.equipamentoRepository = equipamentoRepository;
    }

    public Equipamento cadastrar(Equipamento equipamento){
        return equipamentoRepository.save(equipamento);
    }

    public Equipamento buscarPorId(int id){
        return equipamentoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Equipamento não encontrado."));
    }

    public List<Equipamento> buscarTodos(){
        return equipamentoRepository.findAll();
    }

}
