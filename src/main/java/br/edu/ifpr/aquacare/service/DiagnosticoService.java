package br.edu.ifpr.aquacare.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.aquacare.entity.Animal;
import br.edu.ifpr.aquacare.entity.Aquario;
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
    private final CompatibilidadeService compatibilidadeService;
    private final AquarioService aquarioService;

    public DiagnosticoService(RegistroParametroRepository registroParametroRepository, AnimalAquarioRepository animalAquarioRepository, AnaliseParametroService analiseParametroService, CompatibilidadeService compatibilidadeService, AquarioService aquarioService){
        this.registroParametroRepository = registroParametroRepository;
        this.animalAquarioRepository = animalAquarioRepository;
        this.analiseParametroService = analiseParametroService;
        this.compatibilidadeService = compatibilidadeService;
        this.aquarioService = aquarioService;
    }

    public record Diagnostico(EstadoAquario estado, List<Alerta> alertas){}

    public Diagnostico diagnosticar(int idAquario){
        RegistroParametro registro = registroParametroRepository.findFirstByAquarioIdOrderByDataHoraDesc(idAquario);
        Aquario aquario = aquarioService.buscarPorId(idAquario);

        
        if(registro == null){
            throw new IllegalArgumentException("O aquário ainda não possui registros de parâmetros para gerar um diagnóstico.");
        }

        List<Alerta> alertas = new ArrayList<>(analiseParametroService.analisar(registro));
        alertas.addAll(verificarGrupos(idAquario));
        alertas.addAll(verificarCompatibilidade(idAquario));

        int diasMontagem = (int) java.time.temporal.ChronoUnit.DAYS.between(aquario.getDataMontagem(), LocalDateTime.now());
        if(!aquario.isCiclado() && diasMontagem < 30){
            alertas.add(new Alerta("Aquário em ciclagem (dia " + diasMontagem + " de 30): evite adicionar peixes; amônia e nitrito altos são esperados nesta fase.", Severidade.ATENCAO));
        }
        
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

    private List<Alerta> verificarCompatibilidade(int idAquario){
        List<Alerta> alertas = new ArrayList<>();
        List<AnimalAquario> associados = animalAquarioRepository.findByAquarioId(idAquario);

        for(int i = 0; i < associados.size(); i++){
            for(int j = i + 1; j < associados.size(); j++){
                Animal a = associados.get(i).getAnimal();
                Animal b = associados.get(j).getAnimal();
                if(a.getIdAnimal() != b.getIdAnimal() && !compatibilidadeService.saoCompativeis(a, b)){
                    alertas.add(new Alerta(a.getNomePopular() + " pode ser incompatível com " + b.getNomePopular() + ".", Severidade.ATENCAO));
                }
            }
        }
        return alertas;
    }
}