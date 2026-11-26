package br.edu.ifpr.aquacare.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.aquacare.entity.Animal;
import br.edu.ifpr.aquacare.entity.AnimalAquario;
import br.edu.ifpr.aquacare.enums.TipoAgua;
import br.edu.ifpr.aquacare.repository.AnimalAquarioRepository;
import br.edu.ifpr.aquacare.repository.AnimalRepository;

@Service
public class RecomendacaoService {
    private final AnimalAquarioRepository animalAquarioRepository;
    private final AnimalRepository animalRepository;
    private final CompatibilidadeService compatibilidadeService;
    private final AquarioService aquarioService;

    public RecomendacaoService(AnimalAquarioRepository animalAquarioRepository, AnimalRepository animalRepository,
            CompatibilidadeService compatibilidadeService, AquarioService aquarioService) {
        this.animalAquarioRepository = animalAquarioRepository;
        this.animalRepository = animalRepository;
        this.compatibilidadeService = compatibilidadeService;
        this.aquarioService = aquarioService;
    }

    public record ParametrosIdeais(float phMin, float phMax, float tempMin, float tempMax) {
    }

    public ParametrosIdeais sugerirParametrosIdeais(int idAquario) {
        List<AnimalAquario> associados = animalAquarioRepository.findByAquarioId(idAquario);

        if (associados.isEmpty()) {
            throw new IllegalArgumentException(
                    "Aquário não possui animais associados para calcular parâmetros ideais.");
        }

        float phMin = Float.NEGATIVE_INFINITY;
        float phMax = Float.POSITIVE_INFINITY;
        float tempMin = Float.NEGATIVE_INFINITY;
        float tempMax = Float.POSITIVE_INFINITY;

        for (AnimalAquario animalAquario : associados) {

            Animal animal = animalAquario.getAnimal();
            phMin = Math.max(phMin, animal.getPhMin());
            phMax = Math.min(phMax, animal.getPhMax());
            tempMin = Math.max(tempMin, animal.getTempMin());
            tempMax = Math.min(tempMax, animal.getTempMax());
        }

        if(phMin > phMax || tempMin > tempMax){
            throw new IllegalArgumentException("As espécies deste aquário não têm uma faixa de pH ou temperatura em comum. Consulte os alertas de compatibilidade no diagnóstico.");
        }

        return new ParametrosIdeais(phMin, phMax, tempMin, tempMax);
    }

    public List<Animal> sugerirEspecies(int idAquario){
        boolean aquarioMarinho = aquarioService.buscarPorId(idAquario).getTipoAgua() == TipoAgua.MARINHO;
        List<Integer> jaNoAquario = animalAquarioRepository.findByAquarioId(idAquario).stream()
            .map(aa -> aa.getAnimal().getIdAnimal())
            .toList();
        return animalRepository.findAll().stream()
            .filter(candidato -> candidato.isDeAguaSalgada() == aquarioMarinho)
            .filter(candidato -> compatibilidadeService.isCompativel(idAquario, candidato))
            .filter(candidato -> !jaNoAquario.contains(candidato.getIdAnimal()))
            .toList();
        }
    }
