package br.edu.ifpr.aquacare.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.aquacare.entity.Animal;
import br.edu.ifpr.aquacare.entity.AnimalAquario;
import br.edu.ifpr.aquacare.entity.RegistroParametro;
import br.edu.ifpr.aquacare.enums.Parametro;
import br.edu.ifpr.aquacare.enums.Sensibilidade;
import br.edu.ifpr.aquacare.enums.Severidade;
import br.edu.ifpr.aquacare.enums.TipoAgua;
import br.edu.ifpr.aquacare.repository.AnimalAquarioRepository;

@Service
public class AnaliseParametroService {

    private static final float LIMITE_NH3_BASE = 0.05f;
    private static final float MASSA_MOLAR_NH3 = 17.03f;
    private static final float LIMITE_NITRITO_BASE = 0.5f;
    private static final float MASSA_MOLAR_NITRITO = 46.01f;
    private static final float FATOR_SEGURANCA_CL50 = 0.10f;
    private static final float CALCIO_MIN = 380f;
    private static final float CALCIO_MAX = 450f;
    private static final float MAGNESIO_MIN = 1250f;
    private static final float MAGNESIO_MAX = 1400f;

    private final AnimalAquarioRepository animalAquarioRepository;
    private final ToxicidadeService toxicidadeService;

    public AnaliseParametroService(AnimalAquarioRepository animalAquarioRepository,
            ToxicidadeService toxicidadeService) {
        this.animalAquarioRepository = animalAquarioRepository;
        this.toxicidadeService = toxicidadeService;
    }

    public List<Alerta> analisar(RegistroParametro registro) {
        List<Alerta> alertas = new ArrayList<>();
        List<AnimalAquario> animaisAssociados = animalAquarioRepository.findByAquarioId(registro.getAquario().getId());

        if (animaisAssociados.isEmpty()) {
            alertas.add(new Alerta(null, "Cadastre espécies para receber análises.", Severidade.ATENCAO, "Associe pelo menos uma espécie ao aquário para que o sistema possa analisar os parâmetros registrados."));
        }
        for (AnimalAquario animalAquario : animaisAssociados) {
            Animal animal = animalAquario.getAnimal();

            if (registro.getPh() < animal.getPhMin()) {
                alertas.add(new Alerta(Parametro.PH, "pH abaixo da faixa tolerada por " + animal.getNomePopular() + ".",
                        Severidade.CRITICO,
                        "Eleve o pH gradualmente (tampão alcalino ou troca parcial com água de pH mais alto); mudanças bruscas estressam os peixes."));
            } else if (registro.getPh() > animal.getPhMax()) {
                alertas.add(new Alerta(Parametro.PH, "pH acima da faixa tolerada por " + animal.getNomePopular() + ".",
                        Severidade.CRITICO,
                        "Reduza o pH gradualmente (tampão ácido ou troca parcial de água); mudanças bruscas estressam os peixes."));
            }

            if (registro.getTemperatura() < animal.getTempMin()) {
                alertas.add(new Alerta(Parametro.TEMPERATURA,
                        "Temperatura abaixo da faixa tolerada por " + animal.getNomePopular() + ".", Severidade.CRITICO,
                        "Verifique o aquecedor/termostato do aquário e ajuste a temperatura pra dentro da faixa recomendada."));
            } else if (registro.getTemperatura() > animal.getTempMax()) {
                alertas.add(new Alerta(Parametro.TEMPERATURA,
                        "Temperatura acima da faixa tolerada por " + animal.getNomePopular() + ".", Severidade.CRITICO,
                        "Melhore a ventilação do ambiente, desligue aquecedores extras e evite luz solar direta sobre o aquário."));
            }

            if (toxicidadeService.calcularConcentracaoNH3(registro) > limiteNH3(animal)) {
                alertas.add(new Alerta(Parametro.AMONIA,
                        "Amônia tóxica acima do limite seguro para " + animal.getNomePopular() + ".",
                        Severidade.CRITICO,
                        "Faça uma troca parcial de 30–50% da água, suspenda a alimentação por 24h e remova restos de comida."));
            }

            Float nitrito = registro.getNitrito();
            if (nitrito != null && nitrito > limiteNitrito(animal)) {
                alertas.add(new Alerta(Parametro.NITRITO,
                        "Nitrito acima do limite seguro para " + animal.getNomePopular() + ".", Severidade.ATENCAO,
                        "Faça uma troca parcial de água e evite adicionar novos animais até o nitrito normalizar."));
            }
        }

        if (registro.getAquario().getTipoAgua() == TipoAgua.MARINHO) {
            alertas.addAll(analisarCalcioMagnesio(registro));
        }

        return alertas;
    }

    private List<Alerta> analisarCalcioMagnesio(RegistroParametro registro) {
        List<Alerta> alertas = new ArrayList<>();

        Float calcio = registro.getCalcio();
        if (calcio != null && calcio < CALCIO_MIN) {
            alertas.add(new Alerta(Parametro.CALCIO,
                    "Cálcio abaixo da faixa de referência para aquários marinhos (380 a 450 ppm).", Severidade.ATENCAO,
                    "Adicione um suplemento de cálcio específico pra aquários marinhos, seguindo a dosagem do fabricante."));
        } else if (calcio != null && calcio > CALCIO_MAX) {
            alertas.add(new Alerta(Parametro.CALCIO,
                    "Cálcio acima da faixa de referência para aquários marinhos (380 a 450 ppm).", Severidade.ATENCAO,
                    "Suspenda a suplementação de cálcio e faça uma troca parcial de água."));
        }

        Float magnesio = registro.getMagnesio();
        if (magnesio != null && magnesio < MAGNESIO_MIN) {
            alertas.add(new Alerta(Parametro.MAGNESIO,
                    "Magnésio abaixo da faixa de referência para aquários marinhos (1250 a 1400 ppm).",
                    Severidade.ATENCAO,
                    "Adicione um suplemento de magnésio específico pra aquários marinhos, seguindo a dosagem do fabricante."));
        } else if (magnesio != null && magnesio > MAGNESIO_MAX) {
            alertas.add(new Alerta(Parametro.MAGNESIO,
                    "Magnésio acima da faixa de referência para aquários marinhos (1250 a 1400 ppm).",
                    Severidade.ATENCAO, "Suspenda a suplementação de magnésio e faça uma troca parcial de água."));
        }

        return alertas;
    }

    private float limiteNH3(Animal animal) {
        if (animal.getLc50Amonia() != null) {
            return animal.getLc50Amonia() * MASSA_MOLAR_NH3 * FATOR_SEGURANCA_CL50;
        }
        return limitePorSensibilidade(animal.getSensibilidadeAmonia(), LIMITE_NH3_BASE);
    }

    private float limiteNitrito(Animal animal) {
        if (animal.getLc50Nitrito() != null) {
            return animal.getLc50Nitrito() * MASSA_MOLAR_NITRITO * FATOR_SEGURANCA_CL50;
        }
        return limitePorSensibilidade(animal.getSensibilidadeNitrito(), LIMITE_NITRITO_BASE);
    }

    private float limitePorSensibilidade(Sensibilidade sensibilidade, float base) {
        return switch (sensibilidade) {
            case ALTA -> base * 0.5f;
            case MEDIA -> base;
            case BAIXA -> base * 1.5f;
        };
    }

}