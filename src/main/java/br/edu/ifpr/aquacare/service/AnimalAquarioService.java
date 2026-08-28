package br.edu.ifpr.aquacare.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.aquacare.entity.Animal;
import br.edu.ifpr.aquacare.entity.AnimalAquario;
import br.edu.ifpr.aquacare.repository.AnimalAquarioRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AnimalAquarioService {
    
    private final AnimalAquarioRepository animalAquarioRepository;

    public AnimalAquarioService(AnimalAquarioRepository animalAquarioRepository){
        this.animalAquarioRepository = animalAquarioRepository;
    }

    public AnimalAquario buscarPorId(int id){
        return animalAquarioRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Associação entre animal e aquário não encontrada."));
    }

    public AnimalAquario associar(AnimalAquario animalAquario){
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

    private boolean faixasIncompativeis(float min1, float max1, float min2, float max2){
        return Math.max(min1, min2) > Math.min(max1, max2);
    }

    public List<String> verificarCompatibilidade(int idAquario) {
        List<Animal> animais = listarPorAquario(idAquario).stream().map(AnimalAquario::getAnimal).toList();

        List<String> conflitos = new ArrayList<>();

        for (int i = 0; i < animais.size(); i++) {
            for (int j = i+1; j < animais.size(); j++) {
                Animal a = animais.get(i);
                Animal b = animais.get(j);

                if(faixasIncompativeis(a.getPhMin(), a.getPhMax(), b.getPhMin(), b.getPhMax())){
                    conflitos.add(a.getNomePopular() + " e " + b.getNomePopular() + " incompatíveis: faixas de pH não se sobrepõem.");
                }

                if(faixasIncompativeis(a.getTempMin(), a.getTempMax(), b.getTempMin(), b.getTempMax())){
                    conflitos.add(a.getNomePopular() + " e " + b.getNomePopular() + " incompatíveis: faixas de temperatura não se sobrepõem.");
                }

                if(!(a.getGhMin() == null || b.getGhMin() == null || a.getGhMax() == null || b.getGhMax() == null)){
                    if(faixasIncompativeis(a.getGhMin(), a.getGhMax(), b.getGhMin(), b.getGhMax())){
                    conflitos.add(a.getNomePopular() + " e " + b.getNomePopular() + " incompatíveis: faixas de gH não se sobrepõem.");
                    }
                }
                
                if(!(a.getSalinidadeMin() == null || b.getSalinidadeMin() == null || a.getSalinidadeMax() == null || b.getSalinidadeMax() == null)){
                    if(faixasIncompativeis(a.getSalinidadeMin(), a.getSalinidadeMax(), b.getSalinidadeMin(), b.getSalinidadeMax())){
                        conflitos.add(a.getNomePopular() + " e " + b.getNomePopular() + " incompatíveis: faixas de salinidade não se sobrepõem.");
                    }
                }
            }

        
            
        }

        return conflitos;
    }
}
