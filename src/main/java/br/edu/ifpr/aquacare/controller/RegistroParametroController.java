package br.edu.ifpr.aquacare.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpr.aquacare.entity.RegistroParametro;
import br.edu.ifpr.aquacare.service.AquarioService;

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

    public RegistroParametroController(RegistroParametroService registroParametroService, AquarioService aquarioService, AnaliseParametroService analiseParametroService){
        this.registroParametroService = registroParametroService;
        this.aquarioService = aquarioService;
        this.analiseParametroService = analiseParametroService;
    }

    public record RegistroParametroResponse(int idParametro, int idAquario, LocalDateTime dataHora, float ph, float temperatura, float amonia, Float nitrato, Float nitrito, Float gh, Float kh, Float co2, Float salinidade, Float calcio, Float magnesio){
        public static RegistroParametroResponse from(RegistroParametro registro){
            return new RegistroParametroResponse(registro.getIdParametro(), registro.getAquario().getId(), registro.getDataHora(), registro.getPh(), registro.getTemperatura(), registro.getAmonia(), registro.getNitrato(), registro.getNitrito(), registro.getGh(), registro.getKh(), registro.getCo2(), registro.getSalinidade(), registro.getCalcio(), registro.getMagnesio());
    }
}


    public record DadosRequest(int idAquario, float ph, float temperatura, float amonia, Float nitrato, Float nitrito, Float gh, Float kh, Float co2, Float salinidade, Float calcio, Float magnesio){}

    @PostMapping("/cadastrar")
    public ResponseEntity<List<Alerta>> cadastrar(@RequestBody DadosRequest dados){
        RegistroParametro registro = new RegistroParametro();

        registro.setAmonia(dados.amonia());
        registro.setAquario(aquarioService.buscarPorId(dados.idAquario()));
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
        List<Alerta> alertas = analiseParametroService.analisar(salvo);

        return ResponseEntity.status(HttpStatus.CREATED).body(alertas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroParametroResponse> buscarPorId(@PathVariable int id){
        RegistroParametro registro = registroParametroService.buscarPorId(id);

        return ResponseEntity.ok(RegistroParametroResponse.from(registro));
    }

    @GetMapping("/aquario/{id}")
    public ResponseEntity<List<RegistroParametroResponse>> buscarPorIdAquario(@PathVariable int id){
        List<RegistroParametro> registro = registroParametroService.buscarPorIdAquario(id);

        return ResponseEntity.ok(registro.stream().map(RegistroParametroResponse::from).toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable int id, @RequestBody DadosRequest dados){
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

        registroParametroService.atualizar(id, registro);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable int id){
        registroParametroService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
