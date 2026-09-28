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

import br.edu.ifpr.aquacare.service.PlantaAquarioService;
import br.edu.ifpr.aquacare.service.PlantaService;
import br.edu.ifpr.aquacare.service.AquarioService;
import br.edu.ifpr.aquacare.entity.PlantaAquario;

@RestController 
@RequestMapping("/planta-aquario")
public class PlantaAquarioController {
    
    private final PlantaAquarioService plantaAquarioService;
    private final PlantaService plantaService;
    private final AquarioService aquarioService;

    public record DadosRequest(int idPlanta, int idAquario, int quantidade){}


    public PlantaAquarioController(PlantaAquarioService plantaAquarioService, PlantaService plantaService, AquarioService aquarioService){
        this.plantaService = plantaService;
        this.plantaAquarioService = plantaAquarioService;
        this.aquarioService = aquarioService;
    }

    @PostMapping("/associar")
    public ResponseEntity<PlantaAquario> associar(@RequestBody DadosRequest dados){
        PlantaAquario plantaAquario = new PlantaAquario();

        plantaAquario.setAquario(aquarioService.buscarPorId(dados.idAquario()));
        plantaAquario.setPlanta(plantaService.buscarPorId(dados.idPlanta()));
        plantaAquario.setQuantidade(dados.quantidade());

        plantaAquarioService.associar(plantaAquario);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantaAquario> buscarPorId(@PathVariable int id){
        PlantaAquario plantaAquario = plantaAquarioService.buscarPorId(id);

        return ResponseEntity.ok(plantaAquario);
    }

    @GetMapping("/aquario/{id}")
    public ResponseEntity<List<PlantaAquario>> listarPorIdAqauario(@PathVariable int id){
        List<PlantaAquario> plantas = plantaAquarioService.listarPorAquario(id);

        return ResponseEntity.ok(plantas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable int id, @RequestBody DadosRequest dados){
        plantaAquarioService.atualizar(id, dados.quantidade());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable int id){
        plantaAquarioService.excluir(id);
        
        return ResponseEntity.noContent().build();
    }
}
