package br.edu.ifpr.aquacare.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.aquacare.entity.AnimalAquario;
import br.edu.ifpr.aquacare.repository.AnimalAquarioRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AnimalAquarioService {
    
    private final AnimalAquarioRepository animalAquarioRepository;
    private final CompatibilidadeService compatibilidadeService;

    public AnimalAquarioService(AnimalAquarioRepository animalAquarioRepository, CompatibilidadeService compatibilidadeService){
        this.animalAquarioRepository = animalAquarioRepository;
        this.compatibilidadeService = compatibilidadeService;
    }

    public AnimalAquario buscarPorId(int id){
        return animalAquarioRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Associação entre animal e aquário não encontrada."));
    }

    public AnimalAquario associar(AnimalAquario animalAquario){
        compatibilidadeService.validarNovoAnimal(animalAquario.getAquario().getId(), animalAquario.getAnimal());
        return animalAquarioRepository.save(animalAquario);
    }

    public List<AnimalAquario> listarPorAquario(int idAquario){
        return animalAquarioRepository.findByAquarioId(idAquario);
    }

    public AnimalAquario atualizar(int id, int novaQuantidade){
        AnimalAquario animalAquario = buscarPorId(id);

        animalAquario.setQuantidade(novaQuantidade);

        return animalAquarioRepository.save(animalAquario);
    }

    public void excluir(int id){
        animalAquarioRepository.delete(buscarPorId(id));
    }
}
