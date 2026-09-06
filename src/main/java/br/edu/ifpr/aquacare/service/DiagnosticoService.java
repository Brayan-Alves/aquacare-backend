package br.edu.ifpr.aquacare.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.aquacare.entity.Animal;
import br.edu.ifpr.aquacare.entity.AnimalAquario;
import br.edu.ifpr.aquacare.entity.RegistroParametro;
import br.edu.ifpr.aquacare.enums.EstadoAquario;
import br.edu.ifpr.aquacare.enums.Severidade;
import br.edu.ifpr.aquacare.repository.AnimalAquarioRepository;
import br.edu.ifpr.aquacare.repository.RegistroParametroRepository;

@Service 
public class DiagnosticoService {
    
    private final RegistroParametroRepository registroParametroRepository;
    private final AnimalAquarioRepository animalAquarioRepository;
    private final AnaliseParametroService analiseParametroService;

    public DiagnosticoService(RegistroParametroRepository registroParametroRepository, AnimalAquarioRepository animalAquarioRepository, AnaliseParametroService analiseParametroService){
        this.registroParametroRepository = registroParametroRepository;
        this.animalAquarioRepository = animalAquarioRepository;
        this.analiseParametroService = analiseParametroService;
    }

    public record Diagnostico(EstadoAquario estado, List<Alerta> alertas){}

    public Diagnostico diagnosticar(int idAquario){
        RegistroParametro registro = registroParametroRepository.findFirstByAquarioIdOrderByDataHoraDesc(idAquario);
        
        List<Alerta> alertas = new ArrayList<>(analiseParametroService.analisar(registro));
        alertas.addAll(verificarGrupos(idAquario));

        EstadoAquario estado = classificar(alertas);

        return new Diagnostico(estado, alertas);
    }

    private List<Alerta> verificarGrupos(int idAquario){
        List<Alerta> alertas = new ArrayList<>();
        List<AnimalAquario> associados = animalAquarioRepository.findByAquarioId(idAquario);

        for (AnimalAquario animalAquario : associados) {
            Animal animal = animalAquario.getAnimal();

            if(animal.getQuantidadeMinimaGrupo() != null && animalAquario.getQuantidade() < animal.getQuantidadeMinimaGrupo()){
                alertas.add(new Alerta(animal.getNomePopular() + " está abaixo do grupo mínimo recomendado.", Severidade.ATENCAO));
            }
        }

        return alertas;
    }

    private EstadoAquario classificar(List<Alerta> alertas){
        if(alertas.isEmpty()){
            return EstadoAquario.SAUDAVEL;
        }

        boolean temCritico = alertas.stream().anyMatch(a -> a.severidade() == Severidade.CRITICO);
        return temCritico ? EstadoAquario.CRITICO : EstadoAquario.ATENCAO;
    }
}
