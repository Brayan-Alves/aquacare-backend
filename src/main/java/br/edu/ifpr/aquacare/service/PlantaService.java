package br.edu.ifpr.aquacare.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.aquacare.entity.Planta;
import br.edu.ifpr.aquacare.repository.PlantaRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class PlantaService {
    
    private final PlantaRepository plantaRepository;

    public PlantaService(PlantaRepository plantaRepository){
        this.plantaRepository = plantaRepository;
    }

    public Planta cadastrar(Planta planta){
        return plantaRepository.save(planta);
    }

    public List<Planta> buscarTodos(){
        return plantaRepository.findAll();
    }

    public Planta buscarPorId(int id){
        return plantaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Planta não encontrada."));
    }

    public Planta atualizar(int id, Planta dadosAtualizados){
        Planta planta = buscarPorId(id);

        planta.setNomePopular(dadosAtualizados.getNomePopular());
        planta.setNomeCientifico(dadosAtualizados.getNomeCientifico());
        planta.setAlturaCm(dadosAtualizados.getAlturaCm());
        planta.setCo2Necessario(dadosAtualizados.isCo2Necessario());
        planta.setCrescimento(dadosAtualizados.getCrescimento());
        planta.setDescricao(dadosAtualizados.getDescricao());
        planta.setDificuldade(dadosAtualizados.getDificuldade());
        planta.setIluminacao(dadosAtualizados.getIluminacao());
        planta.setPhMax(dadosAtualizados.getPhMax());
        planta.setPhMin(dadosAtualizados.getPhMin());
        planta.setTempMax(dadosAtualizados.getTempMax());
        planta.setTempMin(dadosAtualizados.getTempMin());
        
        return plantaRepository.save(planta);
    }

    public void excluir(int id){
        plantaRepository.delete(buscarPorId(id));
    }

}