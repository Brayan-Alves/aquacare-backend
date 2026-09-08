package br.edu.ifpr.aquacare.controller;

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

    public record DadosRequest(String nome, float litragem, TipoAgua tipoAgua, NivelPlanta nivelPlanta, boolean isCiclado, boolean injecaoCO2){}

    @PostMapping("/cadastrar")
    public ResponseEntity<Void> cadastrar(@RequestBody DadosRequest dados){
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

    @GetMapping("/listar-todos")
    public ResponseEntity<List<Aquario>> listarTodos(){
        List<Aquario> aquarios = aquarioService.listarTodos();
        return ResponseEntity.ok(aquarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aquario> buscarPorId(@PathVariable  int id){
        Aquario aquario = aquarioService.buscarPorId(id);
        return ResponseEntity.ok(aquario);
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<List<Aquario>> listarPorUsuario(@PathVariable String id){
        List<Aquario> aquario = aquarioService.listarPorUsuario(id);
        return ResponseEntity.ok(aquario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@RequestBody DadosRequest dados, @PathVariable  int id){
        Aquario aquario = new Aquario();

        aquario.setNome(dados.nome());
        aquario.setLitragem(dados.litragem());
        aquario.setTipoAgua(dados.tipoAgua());
        aquario.setNivelPlantas(dados.nivelPlanta());
        aquario.setCiclado(dados.isCiclado());
        aquario.setInjecaoCO2(dados.injecaoCO2());

        aquarioService.atualizar(id, aquario);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable int id){
        aquarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }

}
