package br.edu.ifpr.aquacare.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpr.aquacare.enums.Agressividade;
import br.edu.ifpr.aquacare.enums.PadraoTerritorial;
import br.edu.ifpr.aquacare.enums.RegiaoNado;
import br.edu.ifpr.aquacare.enums.Sensibilidade;
import br.edu.ifpr.aquacare.service.AnimalService;

@RestController
@RequestMapping("/animal")
public class AnimalController {
    
    private final AnimalService animalService;

    public AnimalController(AnimalService animalService){
        this.animalService = animalService;
    }

    public record DadosRequest(String nomePopular, String nomeCientifico, String descricao, float tempMin, float tempMax, float phMin, float phMax, Float ghMin, Float ghMax, Float salinidadeMin, Float salinidadeMax, Agressividade agressividade, PadraoTerritorial padraoTerritorial, float tamanhoMedio, RegiaoNado regiaoNado, String habitoAlimentar, Float lc50Amonia, Float lc50Nitrito, Sensibilidade sensibilidadeAmonia, Sensibilidade sensibilidadeNitrito, Sensibilidade sensibilidadeNitrato, Integer quantidadeMinimaGrupo){}

}
