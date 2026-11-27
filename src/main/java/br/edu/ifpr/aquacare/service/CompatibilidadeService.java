package br.edu.ifpr.aquacare.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

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
public class CompatibilidadeService {

    private static final float RAZAO_PREDACAO_MAXIMA = 3f;
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    public record Incompatibilidade(String mensagem, String recomendacao) {
    }

    private final AnimalAquarioRepository animalAquarioRepository;
    private final PlantaAquarioRepository plantaAquarioRepository;

    public CompatibilidadeService(AnimalAquarioRepository animalAquarioRepository,
            PlantaAquarioRepository plantaAquarioRepository) {
        this.animalAquarioRepository = animalAquarioRepository;
        this.plantaAquarioRepository = plantaAquarioRepository;
    }

    private boolean faixasIncompativeis(float min1, float max1, float min2, float max2) {
        return Math.max(min1, min2) > Math.min(max1, max2);
    }

    private boolean agressividadeIncompativel(Animal existente, Animal novoAnimal) {
        return existente.getRegiaoNado() == novoAnimal.getRegiaoNado()
                && (existente.getAgressividade() == Agressividade.AGRESSIVO
                        || novoAnimal.getAgressividade() == Agressividade.AGRESSIVO);
    }

    private boolean territorialismoIncompativel(Animal existente, Animal novoAnimal) {
        return existente.getRegiaoNado() == novoAnimal.getRegiaoNado()
                && existente.getPadraoTerritorial() == PadraoTerritorial.TERRITORIAL
                && novoAnimal.getPadraoTerritorial() == PadraoTerritorial.TERRITORIAL;
    }

    private boolean riscoPredacao(Animal existente, Animal novoAnimal) {
        float maior = Math.max(existente.getTamanhoMedio(), novoAnimal.getTamanhoMedio());
        float menor = Math.min(existente.getTamanhoMedio(), novoAnimal.getTamanhoMedio());
        return (maior / menor) >= RAZAO_PREDACAO_MAXIMA;
    }
    private String numero(float valor) {
        String texto = String.format(PT_BR, "%.1f", valor);
        return texto.endsWith(",0") ? texto.substring(0, texto.length() - 2) : texto;
    }

    private Incompatibilidade faixaSemValorEmComum(String titulo, String parametro, String unidade,
            Animal a, float aMin, float aMax, Animal b, float bMin, float bMax) {
        return new Incompatibilidade(
                titulo + ": " + a.getNomePopular() + " precisa de " + parametro + " " + numero(aMin) + "–"
                        + numero(aMax) + unidade + " e " + b.getNomePopular() + " de " + numero(bMin) + "–"
                        + numero(bMax) + unidade + ", sem nenhum valor em comum.",
                "Não existe um valor de " + parametro + " que atenda as duas espécies ao mesmo tempo, então "
                        + "ajustar a água não resolve. Mantenha as espécies em aquários separados ou troque uma "
                        + "delas por outra com faixa de " + parametro + " compatível.");
    }

    public Optional<Incompatibilidade> verificar(Animal a, Animal b) {
        if (faixasIncompativeis(a.getPhMin(), a.getPhMax(), b.getPhMin(), b.getPhMax())) {
            return Optional.of(faixaSemValorEmComum("pH incompatível", "pH", "",
                    a, a.getPhMin(), a.getPhMax(), b, b.getPhMin(), b.getPhMax()));
        }

        if (faixasIncompativeis(a.getTempMin(), a.getTempMax(), b.getTempMin(), b.getTempMax())) {
            return Optional.of(faixaSemValorEmComum("Temperatura incompatível", "temperatura", " °C",
                    a, a.getTempMin(), a.getTempMax(), b, b.getTempMin(), b.getTempMax()));
        }

        if (a.getGhMin() != null && a.getGhMax() != null && b.getGhMin() != null && b.getGhMax() != null
                && faixasIncompativeis(a.getGhMin(), a.getGhMax(), b.getGhMin(), b.getGhMax())) {
            return Optional.of(faixaSemValorEmComum("Dureza (gH) incompatível", "gH", "",
                    a, a.getGhMin(), a.getGhMax(), b, b.getGhMin(), b.getGhMax()));
        }

        if (a.getSalinidadeMin() != null && a.getSalinidadeMax() != null && b.getSalinidadeMin() != null
                && b.getSalinidadeMax() != null && faixasIncompativeis(a.getSalinidadeMin(), a.getSalinidadeMax(),
                        b.getSalinidadeMin(), b.getSalinidadeMax())) {
            return Optional.of(faixaSemValorEmComum("Salinidade incompatível", "salinidade", "",
                    a, a.getSalinidadeMin(), a.getSalinidadeMax(), b, b.getSalinidadeMin(), b.getSalinidadeMax()));
        }

        String regiao = a.getRegiaoNado() != null ? a.getRegiaoNado().name().toLowerCase() : "";

        if (agressividadeIncompativel(a, b)) {
            String quem;
            if (a.getAgressividade() == Agressividade.AGRESSIVO && b.getAgressividade() == Agressividade.AGRESSIVO) {
                quem = a.getNomePopular() + " e " + b.getNomePopular()
                        + " são agressivos e dividem a mesma região do aquário (" + regiao + ").";
            } else {
                Animal agressivo = a.getAgressividade() == Agressividade.AGRESSIVO ? a : b;
                Animal outro = agressivo == a ? b : a;
                quem = agressivo.getNomePopular() + " é agressivo e divide a mesma região do aquário (" + regiao
                        + ") com " + outro.getNomePopular() + ".";
            }
            return Optional.of(new Incompatibilidade("Risco de agressão: " + quem,
                    "Peixes agressivos costumam perseguir e morder quem divide o mesmo espaço. O mais seguro é "
                            + "separar as espécies; se quiser mantê-las juntas, use um aquário maior, com plantas e "
                            + "esconderijos que quebrem a linha de visão, e fique atento a nadadeiras mordidas ou "
                            + "peixes sempre escondidos."));
        }

        if (territorialismoIncompativel(a, b)) {
            return Optional.of(new Incompatibilidade(
                    "Disputa de território: " + a.getNomePopular() + " e " + b.getNomePopular()
                            + " são territorialistas e disputam a mesma região do aquário (" + regiao + ").",
                    "Use rochas, troncos e plantas para dividir essa região em territórios separados e garanta "
                            + "espaço para cada um. Se as brigas continuarem, separe as espécies."));
        }

        if (riscoPredacao(a, b)) {
            Animal maior = a.getTamanhoMedio() >= b.getTamanhoMedio() ? a : b;
            Animal menor = maior == a ? b : a;
            float razao = maior.getTamanhoMedio() / menor.getTamanhoMedio();
            return Optional.of(new Incompatibilidade(
                    "Risco de predação: " + maior.getNomePopular() + " (" + numero(maior.getTamanhoMedio())
                            + " cm) tem " + numero(razao) + " vezes o tamanho de " + menor.getNomePopular() + " ("
                            + numero(menor.getTamanhoMedio()) + " cm) e pode tentar comê-lo.",
                    "Peixes que cabem na boca de outro costumam virar alimento, principalmente à noite. A forma "
                            + "segura de resolver é manter as espécies em aquários separados ou trocar uma delas por "
                            + "outra de porte parecido (menos de " + numero(RAZAO_PREDACAO_MAXIMA)
                            + " vezes de diferença de tamanho)."));
        }

        return Optional.empty();
    }

    public void validarNovoAnimal(int idAquario, Animal novoAnimal) {
        List<AnimalAquario> animaisAssociados = animalAquarioRepository.findByAquarioId(idAquario);
        List<PlantaAquario> plantasAssociadas = plantaAquarioRepository.findByAquarioId(idAquario);

        for (AnimalAquario animalAquario : animaisAssociados) {
            Optional<Incompatibilidade> problema = verificar(animalAquario.getAnimal(), novoAnimal);
            if (problema.isPresent()) {
                throw new IllegalArgumentException(problema.get().mensagem());
            }
        }

        for (PlantaAquario plantaAquario : plantasAssociadas) {
            Planta existente = plantaAquario.getPlanta();

            if (faixasIncompativeis(existente.getPhMin(), existente.getPhMax(), novoAnimal.getPhMin(),
                    novoAnimal.getPhMax())
                    || faixasIncompativeis(existente.getTempMin(), existente.getTempMax(), novoAnimal.getTempMin(),
                            novoAnimal.getTempMax())) {
                throw new IllegalArgumentException(novoAnimal.getNomePopular() + " é incompatível com "
                        + existente.getNomePopular() + ": faixas de pH ou temperatura não se sobrepõem.");
            }
        }
    }

    public void validarNovaPlanta(int idAquario, Planta novaPlanta) {
        List<AnimalAquario> animaisAssociados = animalAquarioRepository.findByAquarioId(idAquario);
        List<PlantaAquario> plantasAssociadas = plantaAquarioRepository.findByAquarioId(idAquario);

        for (AnimalAquario animalAquario : animaisAssociados) {
            Animal existente = animalAquario.getAnimal();

            if (faixasIncompativeis(existente.getPhMin(), existente.getPhMax(), novaPlanta.getPhMin(),
                    novaPlanta.getPhMax())
                    || (faixasIncompativeis(existente.getTempMin(), existente.getTempMax(), novaPlanta.getTempMin(),
                            novaPlanta.getTempMax()))) {
                throw new IllegalArgumentException(novaPlanta.getNomePopular() + " é incompatível com "
                        + existente.getNomePopular() + ": faixas de pH ou temperatura não se sobrepõem.");
            }

        }

        for (PlantaAquario plantaAquario : plantasAssociadas) {
            Planta existente = plantaAquario.getPlanta();

            if (faixasIncompativeis(existente.getPhMin(), existente.getPhMax(), novaPlanta.getPhMin(),
                    novaPlanta.getPhMax())
                    || faixasIncompativeis(existente.getTempMin(), existente.getTempMax(), novaPlanta.getTempMin(),
                            novaPlanta.getTempMax())) {
                throw new IllegalArgumentException(novaPlanta.getNomePopular() + " é incompatível com "
                        + existente.getNomePopular() + ": faixas de pH ou temperatura não se sobrepõem.");
            }
        }
    }

    public boolean isCompativel(int idAquario, Animal animal) {
        try {
            validarNovoAnimal(idAquario, animal);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }

    }

    public boolean saoCompativeis(Planta planta, Animal animal) {
        return !faixasIncompativeis(planta.getPhMin(), planta.getPhMax(), animal.getPhMin(), animal.getPhMax())
                && !faixasIncompativeis(planta.getTempMin(), planta.getTempMax(), animal.getTempMin(),
                        animal.getTempMax());
    }

    public boolean saoCompativeis(Planta a, Planta b) {
        return !faixasIncompativeis(a.getPhMin(), a.getPhMax(), b.getPhMin(), b.getPhMax())
                && !faixasIncompativeis(a.getTempMin(), a.getTempMax(), b.getTempMin(), b.getTempMax());
    }
}