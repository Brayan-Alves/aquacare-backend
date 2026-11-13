package br.edu.ifpr.aquacare;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import br.edu.ifpr.aquacare.entity.Animal;
import br.edu.ifpr.aquacare.entity.AnimalAquario;
import br.edu.ifpr.aquacare.entity.Aquario;
import br.edu.ifpr.aquacare.entity.Equipamento;
import br.edu.ifpr.aquacare.entity.Planta;
import br.edu.ifpr.aquacare.entity.PlantaAquario;
import br.edu.ifpr.aquacare.entity.RegistroParametro;
import br.edu.ifpr.aquacare.enums.Agressividade;
import br.edu.ifpr.aquacare.enums.EstadoAquario;
import br.edu.ifpr.aquacare.enums.PadraoTerritorial;
import br.edu.ifpr.aquacare.enums.RegiaoNado;
import br.edu.ifpr.aquacare.enums.Sensibilidade;
import br.edu.ifpr.aquacare.enums.TipoAgua;
import br.edu.ifpr.aquacare.enums.TipoEquipamento;
import br.edu.ifpr.aquacare.repository.AnimalAquarioRepository;
import br.edu.ifpr.aquacare.repository.AnimalRepository;
import br.edu.ifpr.aquacare.repository.PlantaAquarioRepository;
import br.edu.ifpr.aquacare.repository.RegistroParametroRepository;
import br.edu.ifpr.aquacare.service.AnaliseParametroService;
import br.edu.ifpr.aquacare.service.CompatibilidadeService;
import br.edu.ifpr.aquacare.service.DiagnosticoService;
import br.edu.ifpr.aquacare.service.DiagnosticoService.Diagnostico;
import br.edu.ifpr.aquacare.service.DimensionamentoService;
import br.edu.ifpr.aquacare.service.RecomendacaoService;
import br.edu.ifpr.aquacare.service.ToxicidadeService;

/**
 * Cenários de uso simulados: exercitam as regras de negócio reais dos services
 * com repositórios simulados (Mockito), sem banco de dados. Cada cenário registra
 * entrada, resultado esperado e resultado obtido em target/cenarios.csv.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CenariosDeUsoTest {

    private static final int ID_AQUARIO = 1;
    private static final List<String> linhas = new ArrayList<>();

    private AnimalAquarioRepository animalAquarioRepo;
    private PlantaAquarioRepository plantaAquarioRepo;
    private RegistroParametroRepository registroRepo;
    private AnimalRepository animalRepo;

    private ToxicidadeService toxicidade;
    private CompatibilidadeService compatibilidade;
    private AnaliseParametroService analise;
    private DiagnosticoService diagnostico;
    private RecomendacaoService recomendacao;
    private DimensionamentoService dimensionamento;

    private Aquario aquarioDoce;

    @BeforeEach
    void setUp() {
        animalAquarioRepo = mock(AnimalAquarioRepository.class);
        plantaAquarioRepo = mock(PlantaAquarioRepository.class);
        registroRepo = mock(RegistroParametroRepository.class);
        animalRepo = mock(AnimalRepository.class);

        toxicidade = new ToxicidadeService();
        compatibilidade = new CompatibilidadeService(animalAquarioRepo, plantaAquarioRepo);
        analise = new AnaliseParametroService(animalAquarioRepo, toxicidade);
        diagnostico = new DiagnosticoService(registroRepo, animalAquarioRepo, analise);
        recomendacao = new RecomendacaoService(animalAquarioRepo, animalRepo, compatibilidade);
        dimensionamento = new DimensionamentoService();

        aquarioDoce = aquario(TipoAgua.DOCE, 100f);
        when(plantaAquarioRepo.findByAquarioId(ID_AQUARIO)).thenReturn(List.of());
        when(animalAquarioRepo.findByAquarioId(ID_AQUARIO)).thenReturn(List.of());
    }

    // ---------------------------------------------------------------- utilitários

    private static Aquario aquario(TipoAgua tipo, float litragem) {
        Aquario a = new Aquario();
        a.setId(ID_AQUARIO);
        a.setTipoAgua(tipo);
        a.setLitragem(litragem);
        return a;
    }

    private static Animal animal(String nome, float phMin, float phMax, float tMin, float tMax,
            Agressividade agr, PadraoTerritorial terr, RegiaoNado regiao, float tamanhoCm) {
        Animal a = new Animal();
        a.setNomePopular(nome);
        a.setPhMin(phMin);
        a.setPhMax(phMax);
        a.setTempMin(tMin);
        a.setTempMax(tMax);
        a.setAgressividade(agr);
        a.setPadraoTerritorial(terr);
        a.setRegiaoNado(regiao);
        a.setTamanhoMedio(tamanhoCm);
        a.setSensibilidadeAmonia(Sensibilidade.MEDIA);
        a.setSensibilidadeNitrito(Sensibilidade.MEDIA);
        a.setSensibilidadeNitrato(Sensibilidade.MEDIA);
        return a;
    }

    private static Animal pacifico(String nome, float phMin, float phMax, float tMin, float tMax, RegiaoNado regiao, float tamanhoCm) {
        return animal(nome, phMin, phMax, tMin, tMax, Agressividade.PACIFICO, PadraoTerritorial.NAO_TERRITORIAL, regiao, tamanhoCm);
    }

    private AnimalAquario noAquario(Animal animal, int quantidade) {
        AnimalAquario aa = new AnimalAquario();
        aa.setAnimal(animal);
        aa.setAquario(aquarioDoce);
        aa.setQuantidade(quantidade);
        return aa;
    }

    private void aquarioCom(AnimalAquario... associados) {
        when(animalAquarioRepo.findByAquarioId(ID_AQUARIO)).thenReturn(List.of(associados));
    }

    private static RegistroParametro registro(Aquario aquario, float ph, float temp, float amonia, Float nitrito, Float salinidade) {
        RegistroParametro r = new RegistroParametro();
        r.setAquario(aquario);
        r.setPh(ph);
        r.setTemperatura(temp);
        r.setAmonia(amonia);
        r.setNitrito(nitrito);
        r.setSalinidade(salinidade);
        return r;
    }

    private static String validar(Runnable acao) {
        try {
            acao.run();
            return "Aceito";
        } catch (IllegalArgumentException e) {
            return "Rejeitado: " + e.getMessage();
        }
    }

    private static void registrar(String id, String modulo, String entrada, String esperado, String obtido) {
        linhas.add(String.join(";", id, modulo, entrada, esperado, obtido));
    }

    private static String fmt(double v, int casas) {
        return String.format(Locale.forLanguageTag("pt-BR"), "%." + casas + "f", v);
    }

    @AfterAll
    static void exportar() throws IOException {
        Path saida = Path.of("target", "cenarios.csv");
        Files.createDirectories(saida.getParent());
        List<String> tudo = new ArrayList<>();
        tudo.add("id;modulo;entrada;esperado;obtido");
        tudo.addAll(linhas);
        Files.write(saida, tudo);
    }

    // ---------------------------------------------------------------- compatibilidade

    @Test @Order(1)
    void c1_faixasDePhSemSobreposicao() {
        Animal a = pacifico("Espécie A", 6.0f, 7.0f, 22f, 28f, RegiaoNado.MEIO, 4f);
        Animal b = pacifico("Espécie B", 7.5f, 8.0f, 22f, 28f, RegiaoNado.MEIO, 4f);
        aquarioCom(noAquario(a, 1));
        String r = validar(() -> compatibilidade.validarNovoAnimal(ID_AQUARIO, b));
        registrar("C1", "Compatibilidade", "pH 6,0–7,0 × pH 7,5–8,0", "Rejeitar (pH)", r);
        assertEquals(true, r.startsWith("Rejeitado") && r.contains("pH"));
    }

    @Test @Order(2)
    void c2_especiesCompativeis() {
        Animal a = pacifico("Espécie A", 6.0f, 7.5f, 22f, 28f, RegiaoNado.MEIO, 4f);
        Animal b = pacifico("Espécie B", 6.5f, 8.0f, 24f, 30f, RegiaoNado.MEIO, 5f);
        aquarioCom(noAquario(a, 1));
        String r = validar(() -> compatibilidade.validarNovoAnimal(ID_AQUARIO, b));
        registrar("C2", "Compatibilidade", "Faixas sobrepostas, ambos pacíficos, razão de tamanho 1,25", "Aceitar", r);
        assertEquals("Aceito", r);
    }

    @Test @Order(3)
    void c3_agressivoNaMesmaRegiao() {
        Animal a = pacifico("Espécie A", 6.0f, 8.0f, 22f, 28f, RegiaoNado.MEIO, 5f);
        Animal b = animal("Espécie B", 6.0f, 8.0f, 22f, 28f, Agressividade.AGRESSIVO, PadraoTerritorial.NAO_TERRITORIAL, RegiaoNado.MEIO, 6f);
        aquarioCom(noAquario(a, 1));
        String r = validar(() -> compatibilidade.validarNovoAnimal(ID_AQUARIO, b));
        registrar("C3", "Compatibilidade", "Agressivo × pacífico, mesma região (meio)", "Rejeitar (agressividade)", r);
        assertEquals(true, r.contains("agressivo"));
    }

    @Test @Order(4)
    void c4_agressivoEmRegiaoDiferente() {
        Animal a = pacifico("Espécie A", 6.0f, 8.0f, 22f, 28f, RegiaoNado.FUNDO, 5f);
        Animal b = animal("Espécie B", 6.0f, 8.0f, 22f, 28f, Agressividade.AGRESSIVO, PadraoTerritorial.NAO_TERRITORIAL, RegiaoNado.TOPO, 6f);
        aquarioCom(noAquario(a, 1));
        String r = validar(() -> compatibilidade.validarNovoAnimal(ID_AQUARIO, b));
        registrar("C4", "Compatibilidade", "Agressivo (topo) × pacífico (fundo)", "Aceitar", r);
        assertEquals("Aceito", r);
    }

    @Test @Order(5)
    void c5_doisTerritoriaisMesmaRegiao() {
        Animal a = animal("Espécie A", 6.0f, 8.0f, 22f, 28f, Agressividade.SEMI_AGRESSIVO, PadraoTerritorial.TERRITORIAL, RegiaoNado.FUNDO, 8f);
        Animal b = animal("Espécie B", 6.0f, 8.0f, 22f, 28f, Agressividade.SEMI_AGRESSIVO, PadraoTerritorial.TERRITORIAL, RegiaoNado.FUNDO, 9f);
        aquarioCom(noAquario(a, 1));
        String r = validar(() -> compatibilidade.validarNovoAnimal(ID_AQUARIO, b));
        registrar("C5", "Compatibilidade", "Dois territoriais no fundo", "Rejeitar (territorialismo)", r);
        assertEquals(true, r.contains("territorialistas"));
    }

    @Test @Order(6)
    void c6_predacaoNoLimiteDe3() {
        Animal a = pacifico("Espécie A", 6.0f, 8.0f, 22f, 28f, RegiaoNado.MEIO, 4f);
        Animal b = pacifico("Espécie B", 6.0f, 8.0f, 22f, 28f, RegiaoNado.FUNDO, 12f);
        aquarioCom(noAquario(a, 1));
        String r = validar(() -> compatibilidade.validarNovoAnimal(ID_AQUARIO, b));
        registrar("C6", "Compatibilidade", "4 cm × 12 cm (razão 3,0)", "Rejeitar (predação)", r);
        assertEquals(true, r.contains("predação"));
    }

    @Test @Order(7)
    void c7_predacaoAbaixoDoLimite() {
        Animal a = pacifico("Espécie A", 6.0f, 8.0f, 22f, 28f, RegiaoNado.MEIO, 4f);
        Animal b = pacifico("Espécie B", 6.0f, 8.0f, 22f, 28f, RegiaoNado.FUNDO, 11.6f);
        aquarioCom(noAquario(a, 1));
        String r = validar(() -> compatibilidade.validarNovoAnimal(ID_AQUARIO, b));
        registrar("C7", "Compatibilidade", "4 cm × 11,6 cm (razão 2,9)", "Aceitar", r);
        assertEquals("Aceito", r);
    }

    @Test @Order(8)
    void c8_plantaIncompativelComAnimal() {
        Animal a = pacifico("Espécie A", 7.5f, 8.5f, 22f, 28f, RegiaoNado.MEIO, 4f);
        Planta p = new Planta();
        p.setNomePopular("Planta P");
        p.setPhMin(5.5f);
        p.setPhMax(7.0f);
        p.setTempMin(20f);
        p.setTempMax(28f);
        aquarioCom(noAquario(a, 1));
        String r = validar(() -> compatibilidade.validarNovaPlanta(ID_AQUARIO, p));
        registrar("C8", "Compatibilidade", "Planta pH 5,5–7,0 × animal pH 7,5–8,5", "Rejeitar (pH)", r);
        assertEquals(true, r.startsWith("Rejeitado"));
    }

    // ---------------------------------------------------------------- toxicidade

    @Test @Order(10)
    void t1_t3_fracaoDeNH3() {
        double[][] doce = { { 7.0, 25 }, { 8.0, 25 }, { 8.0, 28 } };
        int i = 1;
        for (double[] c : doce) {
            RegistroParametro r = registro(aquarioDoce, (float) c[0], (float) c[1], 1.0f, null, null);
            double tk = c[1] + 273.15;
            double pka = 0.09018 + 2729.92 / tk;
            double esperado = 1.0 / (1.0 + Math.pow(10, pka - c[0]));
            double obtido = toxicidade.calcularPercentualNH3(r);
            registrar("T" + i, "Toxicidade (doce)", "pH " + fmt(c[0], 1) + "; " + fmt(c[1], 0) + " °C",
                    fmt(esperado * 100, 2) + " % NH3", fmt(obtido * 100, 2) + " % NH3");
            assertEquals(esperado, obtido, 1e-4);
            i++;
        }

        Aquario marinho = aquario(TipoAgua.MARINHO, 100f);
        RegistroParametro r = registro(marinho, 8.2f, 25f, 1.0f, null, 35f);
        double pka = 10.0423 - 0.0315536 * 25 + 0.003071 * 35;
        double esperado = 1.0 / (1.0 + Math.pow(10, pka - 8.2));
        double obtido = toxicidade.calcularPercentualNH3(r);
        registrar("T4", "Toxicidade (marinho)", "pH 8,2; 25 °C; S = 35", fmt(esperado * 100, 2) + " % NH3", fmt(obtido * 100, 2) + " % NH3");
        assertEquals(esperado, obtido, 1e-4);
    }

    // ---------------------------------------------------------------- diagnóstico

    private Animal especieDiagnostico() {
        return pacifico("Espécie A", 6.5f, 8.5f, 22f, 30f, RegiaoNado.MEIO, 4f);
    }

    private String diagnosticar(RegistroParametro r) {
        when(registroRepo.findFirstByAquarioIdOrderByDataHoraDesc(ID_AQUARIO)).thenReturn(r);
        Diagnostico d = diagnostico.diagnosticar(ID_AQUARIO);
        String alertas = d.alertas().isEmpty() ? "sem alertas"
                : String.join(" | ", d.alertas().stream().map(a -> a.severidade() + ": " + a.mensagem()).toList());
        return d.estado() + " (" + alertas + ")";
    }

    @Test @Order(20)
    void d1_d2_mesmaAmoniaTotalDiagnosticosDiferentes() {
        aquarioCom(noAquario(especieDiagnostico(), 1));
        String r1 = diagnosticar(registro(aquarioDoce, 7.0f, 25f, 1.0f, null, null));
        registrar("D1", "Diagnóstico", "Amônia total 1,0 mg/L; pH 7,0; 25 °C", "SAUDAVEL", r1);
        assertEquals(true, r1.startsWith(EstadoAquario.SAUDAVEL.name()));

        String r2 = diagnosticar(registro(aquarioDoce, 8.0f, 28f, 1.0f, null, null));
        registrar("D2", "Diagnóstico", "Amônia total 1,0 mg/L; pH 8,0; 28 °C", "CRITICO (NH3)", r2);
        assertEquals(true, r2.startsWith(EstadoAquario.CRITICO.name()) && r2.contains("Amônia"));
    }

    @Test @Order(21)
    void d3_phForaDaFaixa() {
        aquarioCom(noAquario(especieDiagnostico(), 1));
        String r = diagnosticar(registro(aquarioDoce, 6.0f, 25f, 0.1f, null, null));
        registrar("D3", "Diagnóstico", "pH 6,0 (espécie tolera 6,5–8,5)", "CRITICO (pH)", r);
        assertEquals(true, r.startsWith(EstadoAquario.CRITICO.name()) && r.contains("pH"));
    }

    @Test @Order(22)
    void d4_nitritoElevado() {
        aquarioCom(noAquario(especieDiagnostico(), 1));
        String r = diagnosticar(registro(aquarioDoce, 7.0f, 25f, 0.1f, 0.6f, null));
        registrar("D4", "Diagnóstico", "Nitrito 0,6 mg/L (limite 0,5; sensibilidade média)", "ATENCAO (nitrito)", r);
        assertEquals(true, r.startsWith(EstadoAquario.ATENCAO.name()) && r.contains("Nitrito"));
    }

    @Test @Order(23)
    void d5_cardumeAbaixoDoMinimo() {
        Animal cardume = especieDiagnostico();
        cardume.setQuantidadeMinimaGrupo(6);
        aquarioCom(noAquario(cardume, 3));
        String r = diagnosticar(registro(aquarioDoce, 7.0f, 25f, 0.1f, null, null));
        registrar("D5", "Diagnóstico", "3 indivíduos (grupo mínimo 6)", "ATENCAO (grupo)", r);
        assertEquals(true, r.startsWith(EstadoAquario.ATENCAO.name()) && r.contains("grupo"));
    }

    @Test @Order(24)
    void d6_sensibilidadeAlteraLimite() {
        // pH 7,5 e 25 °C: NH3 ~ 1,76 % -> 1,7 mg/L de amônia total gera ~0,030 mg/L de NH3
        Animal media = especieDiagnostico();
        aquarioCom(noAquario(media, 1));
        String rMedia = diagnosticar(registro(aquarioDoce, 7.5f, 25f, 1.7f, null, null));
        registrar("D6", "Diagnóstico", "NH3 ≈ 0,030 mg/L; sensibilidade média (limite 0,05)", "SAUDAVEL", rMedia);
        assertEquals(true, rMedia.startsWith(EstadoAquario.SAUDAVEL.name()));

        Animal alta = especieDiagnostico();
        alta.setSensibilidadeAmonia(Sensibilidade.ALTA);
        aquarioCom(noAquario(alta, 1));
        String rAlta = diagnosticar(registro(aquarioDoce, 7.5f, 25f, 1.7f, null, null));
        registrar("D7", "Diagnóstico", "NH3 ≈ 0,030 mg/L; sensibilidade alta (limite 0,025)", "CRITICO (NH3)", rAlta);
        assertEquals(true, rAlta.startsWith(EstadoAquario.CRITICO.name()));
    }

    // ---------------------------------------------------------------- dimensionamento

    private Equipamento equipamento(TipoEquipamento tipo, Integer vazao, Integer potencia, Float lumens) {
        Equipamento e = new Equipamento();
        e.setAquario(aquarioDoce);
        e.setTipo(tipo);
        e.setVazaoLH(vazao);
        e.setPotenciaWatts(potencia);
        e.setLumens(lumens);
        return e;
    }

    @Test @Order(30)
    void e_dimensionamento() {
        Object[][] casos = {
                { "E1", equipamento(TipoEquipamento.FILTRO, 400, null, null), "Filtro 400 L/h em 100 L (4×)", true },
                { "E2", equipamento(TipoEquipamento.FILTRO, 200, null, null), "Filtro 200 L/h em 100 L (2×)", false },
                { "E3", equipamento(TipoEquipamento.TERMOSTATO, null, 100, null), "Termostato 100 W em 100 L", true },
                { "E4", equipamento(TipoEquipamento.TERMOSTATO, null, 50, null), "Termostato 50 W em 100 L", false },
                { "E5", equipamento(TipoEquipamento.ILUMINACAO, null, null, 4000f), "Iluminação 4000 lm em 100 L", true },
                { "E6", equipamento(TipoEquipamento.ILUMINACAO, null, null, 2000f), "Iluminação 2000 lm em 100 L", false },
        };
        for (Object[] c : casos) {
            boolean obtido = dimensionamento.estaDimensionadoCorretamente((Equipamento) c[1]);
            boolean esperado = (boolean) c[3];
            registrar((String) c[0], "Dimensionamento", (String) c[2], esperado ? "Adequado" : "Inadequado", obtido ? "Adequado" : "Inadequado");
            assertEquals(esperado, obtido);
        }
    }

    // ---------------------------------------------------------------- recomendação

    @Test @Order(40)
    void r1_parametrosIdeais() {
        Animal a = pacifico("Espécie A", 6.0f, 7.5f, 22f, 28f, RegiaoNado.MEIO, 4f);
        Animal b = pacifico("Espécie B", 6.5f, 8.0f, 24f, 30f, RegiaoNado.FUNDO, 5f);
        aquarioCom(noAquario(a, 1), noAquario(b, 1));
        RecomendacaoService.ParametrosIdeais p = recomendacao.sugerirParametrosIdeais(ID_AQUARIO);
        String obtido = "pH " + fmt(p.phMin(), 1) + "–" + fmt(p.phMax(), 1) + "; " + fmt(p.tempMin(), 0) + "–" + fmt(p.tempMax(), 0) + " °C";
        registrar("R1", "Recomendação", "A: pH 6,0–7,5, 22–28 °C; B: pH 6,5–8,0, 24–30 °C", "pH 6,5–7,5; 24–28 °C", obtido);
        assertEquals("pH 6,5–7,5; 24–28 °C", obtido);
    }

    @Test @Order(41)
    void r2_especiesRecomendadas() {
        Animal residente = pacifico("Residente", 6.0f, 7.5f, 22f, 28f, RegiaoNado.MEIO, 4f);
        aquarioCom(noAquario(residente, 1));
        Animal ok = pacifico("Candidata 1", 6.5f, 7.5f, 24f, 28f, RegiaoNado.FUNDO, 5f);
        Animal phRuim = pacifico("Candidata 2", 7.8f, 8.5f, 24f, 28f, RegiaoNado.FUNDO, 5f);
        Animal predador = pacifico("Candidata 3", 6.5f, 7.5f, 24f, 28f, RegiaoNado.FUNDO, 15f);
        when(animalRepo.findAll()).thenReturn(List.of(ok, phRuim, predador));
        List<Animal> sugeridas = recomendacao.sugerirEspecies(ID_AQUARIO);
        String obtido = String.join(", ", sugeridas.stream().map(Animal::getNomePopular).toList());
        registrar("R2", "Recomendação", "Catálogo: 1 compatível, 1 com pH incompatível, 1 com razão 3,75", "Candidata 1", obtido);
        assertEquals("Candidata 1", obtido);
    }

    // ---------------------------------------------------------------- cálcio e magnésio (marinho)

    private String diagnosticarMarinho(Float calcio, Float magnesio) {
        Animal marinha = pacifico("Espécie M", 8.0f, 8.4f, 24f, 28f, RegiaoNado.MEIO, 5f);
        aquarioCom(noAquario(marinha, 1));
        RegistroParametro r = registro(aquario(TipoAgua.MARINHO, 100f), 8.2f, 25f, 0.05f, null, 35f);
        r.setCalcio(calcio);
        r.setMagnesio(magnesio);
        return diagnosticar(r);
    }

    @Test @Order(25)
    void d8_d10_calcioMagnesioMarinho() {
        String r8 = diagnosticarMarinho(350f, 1300f);
        registrar("D8", "Diagnóstico (marinho)", "Ca 350 ppm; Mg 1300 ppm", "ATENCAO (cálcio)", r8);
        assertEquals(true, r8.startsWith(EstadoAquario.ATENCAO.name()) && r8.contains("Cálcio") && !r8.contains("Magnésio"));

        String r9 = diagnosticarMarinho(420f, 1150f);
        registrar("D9", "Diagnóstico (marinho)", "Ca 420 ppm; Mg 1150 ppm", "ATENCAO (magnésio)", r9);
        assertEquals(true, r9.startsWith(EstadoAquario.ATENCAO.name()) && r9.contains("Magnésio") && !r9.contains("Cálcio"));

        String r10 = diagnosticarMarinho(420f, 1320f);
        registrar("D10", "Diagnóstico (marinho)", "Ca 420 ppm; Mg 1320 ppm", "SAUDAVEL", r10);
        assertEquals(true, r10.startsWith(EstadoAquario.SAUDAVEL.name()));
    }

    // ---------------------------------------------------------------- casos de borda (robustez)

    @Test @Order(50)
    void b1_marinhoSemSalinidade() {
        Aquario marinho = aquario(TipoAgua.MARINHO, 100f);
        RegistroParametro semSalinidade = registro(marinho, 8.2f, 25f, 1.0f, null, null);
        RegistroParametro comS35 = registro(marinho, 8.2f, 25f, 1.0f, null, 35f);
        double obtido = toxicidade.calcularPercentualNH3(semSalinidade);
        registrar("B1", "Robustez", "Aquário marinho, registro sem salinidade", "Cálculo com S = 35 (padrão)", fmt(obtido * 100, 2) + " % NH3");
        assertEquals(toxicidade.calcularPercentualNH3(comS35), obtido, 1e-6);
    }

    @Test @Order(51)
    void b2_diagnosticoSemRegistro() {
        aquarioCom(noAquario(especieDiagnostico(), 1));
        when(registroRepo.findFirstByAquarioIdOrderByDataHoraDesc(ID_AQUARIO)).thenReturn(null);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> diagnostico.diagnosticar(ID_AQUARIO));
        registrar("B2", "Robustez", "Diagnóstico de aquário sem nenhum registro", "Mensagem ao usuário (HTTP 400)", "Rejeitado: " + e.getMessage());
    }
}