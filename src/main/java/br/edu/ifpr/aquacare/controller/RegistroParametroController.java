package br.edu.ifpr.aquacare.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpr.aquacare.entity.AlertaRegistro;
import br.edu.ifpr.aquacare.entity.RegistroParametro;
import br.edu.ifpr.aquacare.entity.Usuario;
import br.edu.ifpr.aquacare.enums.Parametro;
import br.edu.ifpr.aquacare.repository.AlertaRegistroRepository;
import br.edu.ifpr.aquacare.service.AquarioService;
import br.edu.ifpr.aquacare.enums.Severidade;

import br.edu.ifpr.aquacare.service.Alerta;
import br.edu.ifpr.aquacare.service.RegistroParametroService;
import br.edu.ifpr.aquacare.service.AnaliseParametroService;
import org.springframework.web.bind.annotation.RequestBody;

@RequestMapping("/registro")
@RestController
public class RegistroParametroController {

    private final RegistroParametroService registroParametroService;
    private final AnaliseParametroService analiseParametroService;
    private final AquarioService aquarioService;
    private final AlertaRegistroRepository alertaRegistroRepository;

    public RegistroParametroController(RegistroParametroService registroParametroService, AquarioService aquarioService,
            AnaliseParametroService analiseParametroService, AlertaRegistroRepository alertaRegistroRepository) {
        this.registroParametroService = registroParametroService;
        this.aquarioService = aquarioService;
        this.analiseParametroService = analiseParametroService;
        this.alertaRegistroRepository = alertaRegistroRepository;
    }

    public record AlertaResponse(Parametro parametro, String mensagem, Severidade severidade, String recomendacao) {
        public static AlertaResponse from(AlertaRegistro a) {
            return new AlertaResponse(a.getParametro(), a.getMensagem(), a.getSeveridade(), a.getRecomendacao());
        }
    }

    public record RegistroParametroResponse(int idParametro, int idAquario, LocalDateTime dataHora, float ph,
            float temperatura, float amonia, Float nitrato, Float nitrito, Float gh, Float kh, Float co2,
            Float salinidade, Float calcio, Float magnesio, List<AlertaResponse> alertas) {
        public static RegistroParametroResponse from(RegistroParametro registro, List<AlertaResponse> alertas) {
            return new RegistroParametroResponse(registro.getIdParametro(), registro.getAquario().getId(),
                    registro.getDataHora(), registro.getPh(), registro.getTemperatura(), registro.getAmonia(),
                    registro.getNitrato(), registro.getNitrito(), registro.getGh(), registro.getKh(), registro.getCo2(),
                    registro.getSalinidade(), registro.getCalcio(), registro.getMagnesio(), alertas);
        }
    }

    public record DadosRequest(int idAquario, float ph, float temperatura, float amonia, Float nitrato, Float nitrito,
            Float gh, Float kh, Float co2, Float salinidade, Float calcio, Float magnesio) {
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<List<Alerta>> cadastrar(@RequestBody DadosRequest dados,
            @AuthenticationPrincipal Usuario usuario) {

        RegistroParametro registro = new RegistroParametro();

        registro.setAmonia(dados.amonia());
        registro.setAquario(aquarioService.buscarPorId(dados.idAquario()));

        if (!registro.getAquario().getUsuario().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("Esse aquário não pertence a você.");
        }

        registro.setCalcio(dados.calcio());
        registro.setCo2(dados.co2());
        registro.setGh(dados.gh());
        registro.setKh(dados.kh());
        registro.setMagnesio(dados.magnesio());
        registro.setNitrato(dados.nitrato());
        registro.setNitrito(dados.nitrito());
        registro.setPh(dados.ph());
        registro.setSalinidade(dados.salinidade());
        registro.setTemperatura(dados.temperatura());

        RegistroParametro salvo = registroParametroService.cadastrar(registro);
        List<Alerta> alertas = analisarESalvarAlertas(salvo);

        return ResponseEntity.status(HttpStatus.CREATED).body(alertas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroParametroResponse> buscarPorId(@PathVariable int id,
            @AuthenticationPrincipal Usuario usuario) {
        if (!registroParametroService.buscarPorId(id).getAquario().getUsuario().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("Esse registro não pertence a você.");
        }
        RegistroParametro registro = registroParametroService.buscarPorId(id);

        List<AlertaResponse> alertas = alertaRegistroRepository.findByRegistroIdParametro(registro.getIdParametro())
                .stream().map(AlertaResponse::from).toList();
        return ResponseEntity.ok(RegistroParametroResponse.from(registro, alertas));
    }

    @GetMapping("/aquario/{id}")
    public ResponseEntity<List<RegistroParametroResponse>> buscarPorIdAquario(@PathVariable int id,
            @AuthenticationPrincipal Usuario usuario) {
        if (!aquarioService.buscarPorId(id).getUsuario().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("Esse aquário não pertence a você.");
        }
        List<RegistroParametro> registro = registroParametroService.buscarPorIdAquario(id);

        return ResponseEntity.ok(registro.stream()
                .map(r -> RegistroParametroResponse.from(r, alertaRegistroRepository
                        .findByRegistroIdParametro(r.getIdParametro()).stream().map(AlertaResponse::from).toList()))
                .toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<List<Alerta>> atualizar(@PathVariable int id, @RequestBody DadosRequest dados,
            @AuthenticationPrincipal Usuario usuario) {
        if (!registroParametroService.buscarPorId(id).getAquario().getUsuario().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("Esse registro não pertence a você.");
        }
        RegistroParametro registro = new RegistroParametro();

        registro.setAmonia(dados.amonia());
        registro.setCalcio(dados.calcio());
        registro.setCo2(dados.co2());
        registro.setGh(dados.gh());
        registro.setKh(dados.kh());
        registro.setMagnesio(dados.magnesio());
        registro.setNitrato(dados.nitrato());
        registro.setNitrito(dados.nitrito());
        registro.setPh(dados.ph());
        registro.setSalinidade(dados.salinidade());
        registro.setTemperatura(dados.temperatura());

        RegistroParametro atualizado = registroParametroService.atualizar(id, registro);

        return ResponseEntity.ok(analisarESalvarAlertas(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable int id, @AuthenticationPrincipal Usuario usuario) {
        if (!registroParametroService.buscarPorId(id).getAquario().getUsuario().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("Esse registro não pertence a você.");
        }
        registroParametroService.excluir(id);

        return ResponseEntity.noContent().build();
    }

    private List<Alerta> analisarESalvarAlertas(RegistroParametro registro) {
        alertaRegistroRepository
                .deleteAll(alertaRegistroRepository.findByRegistroIdParametro(registro.getIdParametro()));

        List<Alerta> alertas = analiseParametroService.analisar(registro);
        for (Alerta a : alertas) {
            AlertaRegistro entidade = new AlertaRegistro();
            entidade.setRegistro(registro);
            entidade.setParametro(a.parametro());
            entidade.setMensagem(a.mensagem());
            entidade.setSeveridade(a.severidade());
            entidade.setRecomendacao(a.recomendacao());
            alertaRegistroRepository.save(entidade);
        }
        return alertas;
    }
}
