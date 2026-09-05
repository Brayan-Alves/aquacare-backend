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
        verificarTipagem(equipamento);
        return equipamentoRepository.save(equipamento);
    }

    public Equipamento buscarPorId(int id){
        return equipamentoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Equipamento não encontrado."));
    }

    public List<Equipamento> buscarTodos(){
        return equipamentoRepository.findAll();
    }

    public List<Equipamento> listarPorAquarios(int idAquario){
        return equipamentoRepository.findByAquarioId(idAquario);
    }

    public Equipamento atualizar(int id, Equipamento dadosAtualizados){
        Equipamento equipamento = buscarPorId(id);

        equipamento.setLumens(dadosAtualizados.getLumens());
        equipamento.setMarca(dadosAtualizados.getMarca());
        equipamento.setModelo(dadosAtualizados.getModelo());
        equipamento.setNome(dadosAtualizados.getNome());
        equipamento.setPotenciaWatts(dadosAtualizados.getPotenciaWatts());
        equipamento.setTipo(dadosAtualizados.getTipo());
        equipamento.setVazaoLH(dadosAtualizados.getVazaoLH());

        verificarTipagem(equipamento);

        return equipamentoRepository.save(equipamento);
    }

    private void verificarTipagem(Equipamento equipamento){
        switch(equipamento.getTipo()){
            case FILTRO -> { if (equipamento.getVazaoLH() == null) throw new IllegalArgumentException("Vazão é obrigatória para equipamento do tipo FILTRO."); }
            case TERMOSTATO -> { if (equipamento.getPotenciaWatts() == null) throw new IllegalArgumentException("Potência é obrigatória para equipamento do tipo TERMOSTATO."); }
            case ILUMINACAO -> { if (equipamento.getLumens() == null) throw new IllegalArgumentException("Lumens é obrigatório para equipamento do tipo ILUMINACAO."); }
        }
    }

    public void excluir(int id){
        equipamentoRepository.delete(buscarPorId(id));
    }



}
