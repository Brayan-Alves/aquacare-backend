package br.edu.ifpr.aquacare.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.aquacare.entity.Animal;
import br.edu.ifpr.aquacare.entity.AnimalAquario;
import br.edu.ifpr.aquacare.entity.Planta;
import br.edu.ifpr.aquacare.entity.PlantaAquario;
import br.edu.ifpr.aquacare.enums.Agressividade;
import br.edu.ifpr.aquacare.enums.PadraoTerritorial;
import br.edu.ifpr.aquacare.repository.AnimalAquarioRepository;
import br.edu.ifpr.aquacare.repository.PlantaAquarioRepository;

@Service
public class CompatibilidadeService{

    private static final float RAZAO_PREDACAO_MAXIMA = 3f;

    private final AnimalAquarioRepository animalAquarioRepository;
    private final PlantaAquarioRepository plantaAquarioRepository;

    public CompatibilidadeService(AnimalAquarioRepository animalAquarioRepository, PlantaAquarioRepository plantaAquarioRepository){
        this.animalAquarioRepository = animalAquarioRepository;
        this.plantaAquarioRepository = plantaAquarioRepository;
    }

    private boolean faixasIncompativeis(float min1, float max1, float min2, float max2){
        return Math.max(min1,min2) > Math.min(max1,max2);
    }

    private boolean agressividadeIncompativel(Animal existente, Animal novoAnimal){
        return existente.getRegiaoNado() == novoAnimal.getRegiaoNado()
            && (existente.getAgressividade() == Agressividade.AGRESSIVO || novoAnimal.getAgressividade() == Agressividade.AGRESSIVO);
    }

    private boolean territorialismoIncompativel(Animal existente, Animal novoAnimal){
        return existente.getRegiaoNado() == novoAnimal.getRegiaoNado()
            && existente.getPadraoTerritorial() == PadraoTerritorial.TERRITORIAL
            && novoAnimal.getPadraoTerritorial() == PadraoTerritorial.TERRITORIAL;
    }

    private boolean riscoPredacao(Animal existente, Animal novoAnimal){
        float maior = Math.max(existente.getTamanhoMedio(), novoAnimal.getTamanhoMedio());
        float menor = Math.min(existente.getTamanhoMedio(), novoAnimal.getTamanhoMedio());
        return (maior / menor) >= RAZAO_PREDACAO_MAXIMA;
    }

    public void validarNovoAnimal(int idAquario, Animal novoAnimal){
        List<AnimalAquario> animaisAssociados = animalAquarioRepository.findByAquarioId(idAquario);
        List<PlantaAquario> plantasAssociadas = plantaAquarioRepository.findByAquarioId(idAquario);

        for (AnimalAquario animalAquario : animaisAssociados) {
            Animal existente = animalAquario.getAnimal();

            if (faixasIncompativeis(existente.getPhMin(), existente.getPhMax(), novoAnimal.getPhMin(), novoAnimal.getPhMax()) || (faixasIncompativeis(existente.getTempMin(), existente.getTempMax(), novoAnimal.getTempMin(), novoAnimal.getTempMax()))) {
                throw new IllegalArgumentException(novoAnimal.getNomePopular() + " é incompatível com " + existente.getNomePopular() + ": faixas de pH ou temperatura não se sobrepõem.");
            }

            if(existente.getGhMin() != null && existente.getGhMax() != null && novoAnimal.getGhMin() != null && novoAnimal.getGhMax() != null && faixasIncompativeis(existente.getGhMin(), existente.getGhMax(), novoAnimal.getGhMin(), novoAnimal.getGhMax())){
                throw new IllegalArgumentException(novoAnimal.getNomePopular() + " é incompatível com " + existente.getNomePopular() + ": faixa de gH não se sobrepõem.");
            }

            if(existente.getSalinidadeMin() != null && existente.getSalinidadeMax() != null && novoAnimal.getSalinidadeMin() != null && novoAnimal.getSalinidadeMax() != null && faixasIncompativeis(existente.getSalinidadeMin(), existente.getSalinidadeMax(), novoAnimal.getSalinidadeMin(), novoAnimal.getSalinidadeMax())){
                throw new IllegalArgumentException(novoAnimal.getNomePopular() + " é incompatível com " + existente.getNomePopular() + ": faixa de salinidade não se sobrepõem.");
            }

            if(agressividadeIncompativel(existente, novoAnimal)){
                throw new IllegalArgumentException(novoAnimal.getNomePopular() + " é incompatível com " + existente.getNomePopular() + ": ambos ocupam a mesma região de nado e pelo menos um é agressivo.");
            }

            if(territorialismoIncompativel(existente, novoAnimal)){
                throw new IllegalArgumentException(novoAnimal.getNomePopular() + " é incompatível com " + existente.getNomePopular() + ": ambos são territorialistas na mesma região de nado.");
            }

            if(riscoPredacao(existente, novoAnimal)){
                throw new IllegalArgumentException(novoAnimal.getNomePopular() + " é incompatível com " + existente.getNomePopular() + ": diferença de tamanho indica risco de predação.");
            }
        }

        for (PlantaAquario plantaAquario : plantasAssociadas) {
            Planta existente = plantaAquario.getPlanta();

            if (faixasIncompativeis(existente.getPhMin(), existente.getPhMax(), novoAnimal.getPhMin(), novoAnimal.getPhMax()) || faixasIncompativeis(existente.getTempMin(), existente.getTempMax(), novoAnimal.getTempMin(), novoAnimal.getTempMax())) {
                throw new IllegalArgumentException(novoAnimal.getNomePopular() + " é incompatível com " + existente.getNomePopular() + ": faixas de pH ou temperatura não se sobrepõem.");
            }
        }
    }

    public void validarNovaPlanta(int idAquario, Planta novaPlanta){
        List<AnimalAquario> animaisAssociados = animalAquarioRepository.findByAquarioId(idAquario);
        List<PlantaAquario> plantasAssociadas = plantaAquarioRepository.findByAquarioId(idAquario);

        for (AnimalAquario animalAquario : animaisAssociados) {
            Animal existente = animalAquario.getAnimal();

            if (faixasIncompativeis(existente.getPhMin(), existente.getPhMax(), novaPlanta.getPhMin(), novaPlanta.getPhMax()) || (faixasIncompativeis(existente.getTempMin(), existente.getTempMax(), novaPlanta.getTempMin(), novaPlanta.getTempMax()))) {
                throw new IllegalArgumentException(novaPlanta.getNomePopular() + " é incompatível com " + existente.getNomePopular() + ": faixas de pH ou temperatura não se sobrepõem.");
            }
            
        }

        for (PlantaAquario plantaAquario : plantasAssociadas) {
            Planta existente = plantaAquario.getPlanta();

            if (faixasIncompativeis(existente.getPhMin(), existente.getPhMax(), novaPlanta.getPhMin(), novaPlanta.getPhMax()) || faixasIncompativeis(existente.getTempMin(), existente.getTempMax(), novaPlanta.getTempMin(), novaPlanta.getTempMax())) {
                throw new IllegalArgumentException(novaPlanta.getNomePopular() + " é incompatível com " + existente.getNomePopular() + ": faixas de pH ou temperatura não se sobrepõem.");
            }
        }
    }



    
}