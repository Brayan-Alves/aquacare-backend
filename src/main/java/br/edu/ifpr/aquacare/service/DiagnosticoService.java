package br.edu.ifpr.aquacare.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.edu.ifpr.aquacare.entity.Animal;
import br.edu.ifpr.aquacare.entity.Planta;
import br.edu.ifpr.aquacare.entity.Aquario;
import br.edu.ifpr.aquacare.entity.AnimalAquario;
import br.edu.ifpr.aquacare.entity.RegistroParametro;
import br.edu.ifpr.aquacare.entity.PlantaAquario;
import br.edu.ifpr.aquacare.enums.EstadoAquario;
import br.edu.ifpr.aquacare.enums.Parametro;
import br.edu.ifpr.aquacare.enums.Severidade;
import br.edu.ifpr.aquacare.enums.Iluminacao;
import br.edu.ifpr.aquacare.enums.TipoEquipamento;
import br.edu.ifpr.aquacare.repository.AnimalAquarioRepository;
import br.edu.ifpr.aquacare.repository.RegistroParametroRepository;
import br.edu.ifpr.aquacare.repository.PlantaAquarioRepository;
import br.edu.ifpr.aquacare.repository.EquipamentoRepository;
import br.edu.ifpr.aquacare.service.CompatibilidadeService.Incompatibilidade;

@Service
public class DiagnosticoService {

    private final RegistroParametroRepository registroParametroRepository;
    private final AnimalAquarioRepository animalAquarioRepository;
    private final AnaliseParametroService analiseParametroService;
    private final CompatibilidadeService compatibilidadeService;
    private final AquarioService aquarioService;
    private final PlantaAquarioRepository plantaAquarioRepository;
    private final EquipamentoRepository equipamentoRepository;

    public DiagnosticoService(RegistroParametroRepository registroParametroRepository,
            AnimalAquarioRepository animalAquarioRepository, AnaliseParametroService analiseParametroService,
            CompatibilidadeService compatibilidadeService, AquarioService aquarioService,
            PlantaAquarioRepository plantaAquarioRepository, EquipamentoRepository equipamentoRepository) {
        this.registroParametroRepository = registroParametroRepository;
        this.animalAquarioRepository = animalAquarioRepository;
        this.analiseParametroService = analiseParametroService;
        this.compatibilidadeService = compatibilidadeService;
        this.aquarioService = aquarioService;
        this.plantaAquarioRepository = plantaAquarioRepository;
        this.equipamentoRepository = equipamentoRepository;
    }

    public record Diagnostico(EstadoAquario estado, List<Alerta> alertas) {
    }

    public Diagnostico diagnosticar(int idAquario) {
        RegistroParametro registro = registroParametroRepository.findFirstByAquarioIdOrderByDataHoraDesc(idAquario);
        Aquario aquario = aquarioService.buscarPorId(idAquario);

        if (registro == null) {
            throw new IllegalArgumentException(
                    "O aquário ainda não possui registros de parâmetros para gerar um diagnóstico.");
        }

        List<Alerta> alertas = new ArrayList<>(analiseParametroService.analisar(registro));
        alertas.addAll(verificarGrupos(idAquario));
        alertas.addAll(verificarCompatibilidade(idAquario));
        alertas.addAll(verificarPlantas(idAquario, aquario));

        int diasMontagem = (int) java.time.temporal.ChronoUnit.DAYS.between(aquario.getDataMontagem(),
                LocalDateTime.now());
        if (!aquario.isCiclado() && diasMontagem < 30) {
            alertas.add(new Alerta(Parametro.CICLAGEM,
                    "Aquário em ciclagem (dia " + diasMontagem
                            + " de 30): evite adicionar peixes; amônia e nitrito altos são esperados nesta fase.",
                    Severidade.ATENCAO,
                    "Monitore amônia e nitrito de perto; evite adicionar novos animais até a ciclagem terminar."));
        }

        EstadoAquario estado = classificar(alertas);

        return new Diagnostico(estado, alertas);
    }

    private List<Alerta> verificarGrupos(int idAquario) {
        List<Alerta> alertas = new ArrayList<>();
        List<AnimalAquario> associados = animalAquarioRepository.findByAquarioId(idAquario);

        for (AnimalAquario animalAquario : associados) {
            Animal animal = animalAquario.getAnimal();

            if (animal.getQuantidadeMinimaGrupo() != null
                    && animalAquario.getQuantidade() < animal.getQuantidadeMinimaGrupo()) {
                alertas.add(new Alerta(Parametro.GRUPO,
                        animal.getNomePopular() + " está abaixo do grupo mínimo recomendado.", Severidade.ATENCAO,
                        "Considere adicionar mais indivíduos dessa espécie para atingir o grupo mínimo recomendado, ou reavalie se essa espécie é adequada pro seu aquário."));
            }
        }

        return alertas;
    }

    private EstadoAquario classificar(List<Alerta> alertas) {
        if (alertas.isEmpty()) {
            return EstadoAquario.SAUDAVEL;
        }

        boolean temCritico = alertas.stream().anyMatch(a -> a.severidade() == Severidade.CRITICO);
        return temCritico ? EstadoAquario.CRITICO : EstadoAquario.ATENCAO;
    }

    private List<Alerta> verificarCompatibilidade(int idAquario) {
        List<Alerta> alertas = new ArrayList<>();
        List<AnimalAquario> associados = animalAquarioRepository.findByAquarioId(idAquario);

        for (int i = 0; i < associados.size(); i++) {
            for (int j = i + 1; j < associados.size(); j++) {
                Animal a = associados.get(i).getAnimal();
                Animal b = associados.get(j).getAnimal();
                if (a.getIdAnimal() == b.getIdAnimal()) {
                    continue;
                }
                Optional<Incompatibilidade> problema = compatibilidadeService.verificar(a, b);
                if (problema.isPresent()) {
                    alertas.add(new Alerta(Parametro.COMPATIBILIDADE, problema.get().mensagem(),
                            Severidade.ATENCAO, problema.get().recomendacao()));
                }
            }
        }
        return alertas;
    }

    private List<Alerta> verificarPlantas(int idAquario, Aquario aquario) {
        List<Alerta> alertas = new ArrayList<>();
        List<PlantaAquario> plantas = plantaAquarioRepository.findByAquarioId(idAquario);

        float lumensTotais = (float) equipamentoRepository.findByAquarioId(idAquario).stream()
                .filter(e -> e.getTipo() == TipoEquipamento.ILUMINACAO)
                .mapToDouble(e -> e.getLumens() != null ? e.getLumens() : 0f)
                .sum();
        float lumensPorLitro = aquario.getLitragem() > 0 ? lumensTotais / aquario.getLitragem() : 0;

        Iluminacao luzAtual;
        if (lumensPorLitro < 30) {
            luzAtual = Iluminacao.BAIXA;
        } else if (lumensPorLitro <= 60) {
            luzAtual = Iluminacao.MEDIA;
        } else {
            luzAtual = Iluminacao.ALTA;
        }

        List<AnimalAquario> animais = animalAquarioRepository.findByAquarioId(idAquario);

        for (PlantaAquario pa : plantas) {
            Planta planta = pa.getPlanta();

            if (luzAtual.ordinal() < planta.getIluminacao().ordinal()) {
                alertas.add(new Alerta(Parametro.ILUMINACAO,
                        planta.getNomePopular() + " precisa de mais luz do que o aquário oferece atualmente.",
                        Severidade.ATENCAO,
                        "Aumente a potência ou quantidade de luminárias para atender a necessidade dessa planta."));
            }

            if (planta.isCo2Necessario() && !aquario.isInjecaoCO2()) {
                alertas.add(new Alerta(Parametro.CO2,
                        planta.getNomePopular() + " precisa de injeção de CO2, que não está ativa neste aquário.",
                        Severidade.ATENCAO,
                        "Instale um sistema de injeção de CO2 ou substitua por uma espécie que não dependa dele."));
            }

            for (AnimalAquario aa : animais) {
                if (!compatibilidadeService.saoCompativeis(planta, aa.getAnimal())) {
                    alertas.add(new Alerta(Parametro.COMPATIBILIDADE,
                            planta.getNomePopular() + " e " + aa.getAnimal().getNomePopular()
                                    + " não têm faixa de pH ou temperatura em comum.",
                            Severidade.ATENCAO,
                            "Mantenha a água na faixa da espécie mais sensível ou substitua a planta por uma compatível."));
                }
            }
        }

        for (int i = 0; i < plantas.size(); i++) {
            for (int j = i + 1; j < plantas.size(); j++) {
                Planta a = plantas.get(i).getPlanta();
                Planta b = plantas.get(j).getPlanta();
                if (!compatibilidadeService.saoCompativeis(a, b)) {
                    alertas.add(new Alerta(Parametro.COMPATIBILIDADE,
                            a.getNomePopular() + " e " + b.getNomePopular()
                                    + " não têm faixa de pH ou temperatura em comum.",
                            Severidade.ATENCAO,
                            "Mantenha a água na faixa da espécie mais sensível ou substitua uma das plantas."));
                }
            }
        }

        return alertas;
    }
}