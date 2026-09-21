package br.edu.ifpr.aquacare.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpr.aquacare.entity.Equipamento;
import br.edu.ifpr.aquacare.enums.TipoEquipamento;
import br.edu.ifpr.aquacare.service.AquarioService;
import br.edu.ifpr.aquacare.service.EquipamentoService;

@RequestMapping("/equipamento")
@RestController 
public class EquipamentoController {
    
    private final EquipamentoService equipamentoService;
    private final AquarioService aquarioService;

    public EquipamentoController(EquipamentoService equipamentoService, AquarioService aquarioService){
        this.equipamentoService = equipamentoService;
        this.aquarioService = aquarioService;
    }

    public record DadosRequest(int idAquario, TipoEquipamento tipo, String nome, String modelo, String marca, Integer potenciaWatts, Integer vazaoLH, Float lumens){}


    @PostMapping("/cadastrar")
    public ResponseEntity<Void> cadastrar(@RequestBody DadosRequest dados){
        Equipamento equipamento = new Equipamento();

        equipamento.setAquario(aquarioService.buscarPorId(dados.idAquario()));
        equipamento.setLumens(dados.lumens());
        equipamento.setMarca(dados.marca());
        equipamento.setModelo(dados.modelo());
        equipamento.setNome(dados.nome());
        equipamento.setPotenciaWatts(dados.potenciaWatts());
        equipamento.setTipo(dados.tipo());
        equipamento.setVazaoLH(dados.vazaoLH());
        
        equipamentoService.cadastrar(equipamento);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/listar-todos")
    public ResponseEntity<List<Equipamento>> listarTodos(){
        List<Equipamento> equipamentos = equipamentoService.buscarTodos();

        return ResponseEntity.ok(equipamentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipamento> buscarPorId(@PathVariable int id){
        Equipamento equipamento = equipamentoService.buscarPorId(id);

        return ResponseEntity.ok(equipamento);
    }

    @GetMapping("/aquario/{id}")
    public ResponseEntity<List<Equipamento>> buscarPorIdAquario(@PathVariable int id){
        List<Equipamento> equipamentos = equipamentoService.listarPorAquarios(id);

        return ResponseEntity.ok(equipamentos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable int id, @RequestBody DadosRequest dados){
        Equipamento equipamento = new Equipamento();

        equipamento.setLumens(dados.lumens());
        equipamento.setMarca(dados.marca());
        equipamento.setModelo(dados.modelo());
        equipamento.setNome(dados.nome());
        equipamento.setPotenciaWatts(dados.potenciaWatts());
        equipamento.setTipo(dados.tipo());
        equipamento.setVazaoLH(dados.vazaoLH());

        equipamentoService.atualizar(id, equipamento);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable int id){
        equipamentoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
