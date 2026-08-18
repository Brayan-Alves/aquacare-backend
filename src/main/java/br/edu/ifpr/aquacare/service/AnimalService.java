package br.edu.ifpr.aquacare.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.aquacare.entity.Animal;
import br.edu.ifpr.aquacare.repository.AnimalRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AnimalService {
    
    private final AnimalRepository animalRepository;

    public AnimalService(AnimalRepository animalRepository){
        this.animalRepository = animalRepository;
    }

    public Animal cadastrar(Animal animal){
        return animalRepository.save(animal);
    }

    public Animal buscarPorId(int id){
        return animalRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Animal não encontrado."));
    }

    public List<Animal> buscarTodos(){
        return animalRepository.findAll();
    }

    public Animal atualizar(int id, Animal dadosAtualizados){
        Animal animal = buscarPorId(id);

        animal.setNomePopular(dadosAtualizados.getNomePopular());
        animal.setNomeCientifico(dadosAtualizados.getNomeCientifico());
        animal.setPhMin(dadosAtualizados.getPhMin());
        animal.setPhMax(dadosAtualizados.getPhMax());
        animal.setDescricao(dadosAtualizados.getDescricao());
        animal.setHabitoAlimentar(dadosAtualizados.getHabitoAlimentar());
        animal.setAgressividade(dadosAtualizados.getAgressividade());
        animal.setGhMin(dadosAtualizados.getGhMin());
        animal.setGhMax(dadosAtualizados.getGhMax());
        animal.setPorte(dadosAtualizados.getPorte());
        animal.setRegiaoNado(dadosAtualizados.getRegiaoNado());
        animal.setSalinidadeMin(dadosAtualizados.getSalinidadeMin());
        animal.setSalinidadeMax(dadosAtualizados.getSalinidadeMax());
        animal.setSensibilidadeAmonia(dadosAtualizados.getSensibilidadeAmonia());
        animal.setSensibilidadeNitrato(dadosAtualizados.getSensibilidadeNitrato());
        animal.setSensibilidadeNitrito(dadosAtualizados.getSensibilidadeNitrito());
        animal.setTempMin(dadosAtualizados.getTempMin());
        animal.setTempMax(dadosAtualizados.getTempMax());

        return animalRepository.save(animal);
    }

    public void excluir(int id){
        animalRepository.delete(buscarPorId(id));
    }


    
}
