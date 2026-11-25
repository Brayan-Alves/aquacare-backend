package br.edu.ifpr.aquacare.controller;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpr.aquacare.entity.Equipamento;
import br.edu.ifpr.aquacare.entity.Usuario;
import br.edu.ifpr.aquacare.enums.TipoEquipamento;
import br.edu.ifpr.aquacare.service.AquarioService;
import br.edu.ifpr.aquacare.service.DimensionamentoService;
import br.edu.ifpr.aquacare.service.EquipamentoService;

@RequestMapping("/equipamento")
@RestController 
public class EquipamentoController {
    
    private final EquipamentoService equipamentoService;
    private final AquarioService aquarioService;
    private final DimensionamentoService dimensionamentoService;


    public EquipamentoController(EquipamentoService equipamentoService, AquarioService aquarioService, DimensionamentoService dimensionamentoService){
        this.equipamentoService = equipamentoService;
        this.aquarioService = aquarioService;
        this.dimensionamentoService = dimensionamentoService;
    }

    public record EquipamentoResponse(int idEquipamento, int idAquario, TipoEquipamento tipo, String nome, String modelo, String marca, Integer potenciaWatts, Integer vazaoLH, Float lumens, boolean dimensionadoCorretamente){
        public static EquipamentoResponse from(Equipamento equipamento, boolean dimensionadoCorretamente){
            return new EquipamentoResponse(equipamento.getIdEquipamento(), equipamento.getAquario().getId(), equipamento.getTipo(), equipamento.getNome(), equipamento.getModelo(), equipamento.getMarca(), equipamento.getPotenciaWatts(), equipamento.getVazaoLH(), equipamento.getLumens(), dimensionadoCorretamente);
        }
    }


    public record DadosRequest(int idAquario, TipoEquipamento tipo, String nome, String modelo, String marca, Integer potenciaWatts, Integer vazaoLH, Float lumens){}


    @PostMapping("/cadastrar")
    public ResponseEntity<EquipamentoResponse> cadastrar(@RequestBody DadosRequest dados,@AuthenticationPrincipal Usuario usuario){
        Equipamento equipamento = new Equipamento();

        equipamento.setAquario(aquarioService.buscarPorId(dados.idAquario()));
        if(!equipamento.getAquario().getUsuario().getId().equals(usuario.getId())){
            throw new AccessDeniedException("Esse Aquário não pertence a você.");
        }
        equipamento.setLumens(dados.lumens());
        equipamento.setMarca(dados.marca());
        equipamento.setModelo(dados.modelo());
        equipamento.setNome(dados.nome());
        equipamento.setPotenciaWatts(dados.potenciaWatts());
        equipamento.setTipo(dados.tipo());
        equipamento.setVazaoLH(dados.vazaoLH());

        
        Equipamento salvo = equipamentoService.cadastrar(equipamento);

        return ResponseEntity.status(HttpStatus.CREATED).body(EquipamentoResponse.from(salvo, dimensionamentoService.estaDimensionadoCorretamente(salvo)));
    }

    @GetMapping("/listar-todos")
    public ResponseEntity<List<EquipamentoResponse>> listarTodos(){
        List<Equipamento> equipamentos = equipamentoService.buscarTodos();

        return ResponseEntity.ok(equipamentos.stream().map(e -> EquipamentoResponse.from(e, dimensionamentoService.estaDimensionadoCorretamente(e))).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipamentoResponse> buscarPorId(@PathVariable int id, @AuthenticationPrincipal Usuario usuario){
        Equipamento equipamento = equipamentoService.buscarPorId(id);
        if(!equipamento.getAquario().getUsuario().getId().equals(usuario.getId())){
            throw new AccessDeniedException("Esse equipamento não pertence a você.");
        }
        return ResponseEntity.ok(EquipamentoResponse.from(equipamento, dimensionamentoService.estaDimensionadoCorretamente(equipamento)));
    }

    @GetMapping("/aquario/{id}")
    public ResponseEntity<List<EquipamentoResponse>> buscarPorIdAquario(@PathVariable int id, @AuthenticationPrincipal Usuario usuario){
        if(!aquarioService.buscarPorId(id).getUsuario().getId().equals(usuario.getId())){
            throw new AccessDeniedException("Esses equipamentos não pertence a você.");
        }
        List<Equipamento> equipamentos = equipamentoService.listarPorAquarios(id);
        return ResponseEntity.ok(equipamentos.stream().map(e -> EquipamentoResponse.from(e, dimensionamentoService.estaDimensionadoCorretamente(e))).toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable int id, @RequestBody DadosRequest dados, @AuthenticationPrincipal Usuario usuario){
        Equipamento equipamento = new Equipamento();

        equipamento.setLumens(dados.lumens());
        equipamento.setMarca(dados.marca());
        equipamento.setModelo(dados.modelo());
        equipamento.setNome(dados.nome());
        equipamento.setPotenciaWatts(dados.potenciaWatts());
        equipamento.setTipo(dados.tipo());
        equipamento.setVazaoLH(dados.vazaoLH());

        if(!equipamentoService.buscarPorId(id).getAquario().getUsuario().getId().equals(usuario.getId())){
            throw new AccessDeniedException("Esse equipamento não pertence a você");
        }

        equipamentoService.atualizar(id, equipamento);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable int id, @AuthenticationPrincipal Usuario usuario){
        if(!equipamentoService.buscarPorId(id).getAquario().getUsuario().getId().equals(usuario.getId())){
            throw new AccessDeniedException("Esse equipamento não pertence a você");
        }
        equipamentoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
