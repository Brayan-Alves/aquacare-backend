package br.edu.ifpr.aquacare.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import br.edu.ifpr.aquacare.entity.Aquario;
import br.edu.ifpr.aquacare.enums.NivelPlanta;
import br.edu.ifpr.aquacare.enums.TipoAgua;
import br.edu.ifpr.aquacare.service.AquarioService;

@RestController
@RequestMapping("/aquarios")
public class AquarioController {
    
    private final AquarioService aquarioService;

    public AquarioController(AquarioService aquarioService){
        this.aquarioService = aquarioService;
    }

    public record CadastroRequest(String nome, float litragem, TipoAgua tipoAgua, NivelPlanta nivelPlanta, boolean isCiclado, boolean injecaoCO2){}

    @PostMapping("/cadastrar")
    public ResponseEntity<Void> cadastrar(@RequestBody CadastroRequest dados){
        Aquario aquario = new Aquario();

        aquario.setNome(dados.nome());
        aquario.setLitragem(dados.litragem());
        aquario.setTipoAgua(dados.tipoAgua());
        aquario.setNivelPlantas(dados.nivelPlanta());
        aquario.setCiclado(dados.isCiclado());
        aquario.setInjecaoCO2(dados.injecaoCO2());

        aquarioService.cadastrar(aquario);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
