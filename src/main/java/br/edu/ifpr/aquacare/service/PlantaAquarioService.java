package br.edu.ifpr.aquacare.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.aquacare.entity.PlantaAquario;
import br.edu.ifpr.aquacare.repository.PlantaAquarioRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class PlantaAquarioService {
    private final PlantaAquarioRepository plantaAquarioRepository;
    private final CompatibilidadeService compatibilidadeService;

    public PlantaAquarioService(PlantaAquarioRepository plantaAquarioRepository, CompatibilidadeService compatibilidadeService){
        this.plantaAquarioRepository = plantaAquarioRepository;
        this.compatibilidadeService = compatibilidadeService;
    }

    public PlantaAquario associar(PlantaAquario plantaAquario){
        compatibilidadeService.validarNovaPlanta(plantaAquario.getAquario().getId(), plantaAquario.getPlanta());
        return plantaAquarioRepository.save(plantaAquario);
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