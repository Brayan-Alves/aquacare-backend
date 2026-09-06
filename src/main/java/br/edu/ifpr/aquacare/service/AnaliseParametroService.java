package br.edu.ifpr.aquacare.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.aquacare.entity.Animal;
import br.edu.ifpr.aquacare.entity.AnimalAquario;
import br.edu.ifpr.aquacare.entity.RegistroParametro;
import br.edu.ifpr.aquacare.enums.Sensibilidade;
import br.edu.ifpr.aquacare.enums.Severidade;
import br.edu.ifpr.aquacare.repository.AnimalAquarioRepository;

@Service 
public class AnaliseParametroService {

    private static final float LIMITE_NH3_BASE = 0.05f;
    private static final float MASSA_MOLAR_NH3 = 17.03f;
    private static final float LIMITE_NITRITO_BASE = 0.5f;
    private static final float MASSA_MOLAR_NITRITO = 46.01f;
    private static final float FATOR_SEGURANCA_CL50 = 0.10f;

    private final AnimalAquarioRepository animalAquarioRepository;
    private final ToxicidadeService toxicidadeService;

    public AnaliseParametroService(AnimalAquarioRepository animalAquarioRepository, ToxicidadeService toxicidadeService){
        this.animalAquarioRepository = animalAquarioRepository;
        this.toxicidadeService = toxicidadeService;
    }

    public List<Alerta> analisar(RegistroParametro registro){
        List<Alerta> alertas = new ArrayList<>();
        List<AnimalAquario> animaisAssociados = animalAquarioRepository.findByAquarioId(registro.getAquario().getId());

        for (AnimalAquario animalAquario : animaisAssociados) {
            Animal animal = animalAquario.getAnimal();

            if(registro.getPh() < animal.getPhMin() || registro.getPh() > animal.getPhMax()){
                alertas.add(new Alerta("pH fora da faixa tolerada por " + animal.getNomePopular() + ".", Severidade.CRITICO));
            }

            if(registro.getTemperatura() < animal.getTempMin() || registro.getTemperatura() > animal.getTempMax()){
                alertas.add(new Alerta("Temperatura fora da faixa tolerada por " + animal.getNomePopular() + ".", Severidade.CRITICO));
            }

            if(toxicidadeService.calcularConcentracaoNH3(registro) > limiteNH3(animal)){
                alertas.add(new Alerta("Amônia tóxica acima do limite seguro para " + animal.getNomePopular() + ".", Severidade.CRITICO));
            }

            Float nitrito = registro.getNitrito();
            if(nitrito != null && nitrito > limiteNitrito(animal)){
                alertas.add(new Alerta("Nitrito acima limite seguro para " + animal.getNomePopular() + ".", Severidade.ATENCAO));
            }
        }

        return alertas;
    }

    private float limiteNH3(Animal animal){
        if(animal.getLc50Amonia() != null){
            return animal.getLc50Amonia() * MASSA_MOLAR_NH3 * FATOR_SEGURANCA_CL50;
        }
        return limitePorSensibilidade(animal.getSensibilidadeAmonia(), LIMITE_NH3_BASE);
    }

    private float limiteNitrito(Animal animal){
        if(animal.getLc50Nitrito() != null){
            return animal.getLc50Nitrito() * MASSA_MOLAR_NITRITO * FATOR_SEGURANCA_CL50;
        }
        return limitePorSensibilidade(animal.getSensibilidadeNitrito(), LIMITE_NITRITO_BASE);
    }

    private float limitePorSensibilidade(Sensibilidade sensibilidade, float base){
        return switch(sensibilidade){
            case ALTA -> base * 0.5f;
            case MEDIA -> base;
            case BAIXA -> base * 1.5f;
        };
    }



}
