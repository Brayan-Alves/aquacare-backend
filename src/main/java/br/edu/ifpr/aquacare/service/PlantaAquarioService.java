package br.edu.ifpr.aquacare.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.aquacare.entity.PlantaAquario;
import br.edu.ifpr.aquacare.enums.TipoAgua;
import br.edu.ifpr.aquacare.repository.PlantaAquarioRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class PlantaAquarioService {
    private final PlantaAquarioRepository plantaAquarioRepository;
   

    public PlantaAquarioService(PlantaAquarioRepository plantaAquarioRepository){
        this.plantaAquarioRepository = plantaAquarioRepository;
    }

    public PlantaAquario associar(PlantaAquario plantaAquario){
        if(plantaAquario.getAquario().getTipoAgua() == TipoAgua.MARINHO){
            throw new IllegalArgumentException("As plantas do catálogo são de água doce e não podem ser adicionadas a um aquário marinho.");
        }
        return plantaAquarioRepository.findByAquarioIdAndPlantaIdPlanta(plantaAquario.getAquario().getId(), plantaAquario.getPlanta().getIdPlanta())
            .map(existente -> {
                existente.setQuantidade(existente.getQuantidade() + plantaAquario.getQuantidade());
                return plantaAquarioRepository.save(existente);
            })
            .orElseGet(() -> plantaAquarioRepository.save(plantaAquario));
    }

    public PlantaAquario buscarPorId(int id){
        return plantaAquarioRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Associação não encontrada."));
    }

    public List<PlantaAquario> listarPorAquario(int idAquario){
        return plantaAquarioRepository.findByAquarioId(idAquario);
    }

    public PlantaAquario atualizar(int id, int novaQuantidade){
        PlantaAquario plantaAquario = buscarPorId(id);

        plantaAquario.setQuantidade(novaQuantidade);

        return plantaAquarioRepository.save(plantaAquario);
    }

    public void excluir(int id){
        plantaAquarioRepository.delete(buscarPorId(id));
    }
}