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

import br.edu.ifpr.aquacare.entity.Planta;
import br.edu.ifpr.aquacare.enums.Dificuldade;
import br.edu.ifpr.aquacare.enums.Iluminacao;
import br.edu.ifpr.aquacare.service.PlantaService;

@RequestMapping("/planta")
@RestController
public class PlantaController {
    
    private final PlantaService plantaService;

    public PlantaController(PlantaService plantaService){
        this.plantaService = plantaService;
    }

    public record DadosRequest(String nomePopular, String nomeCientifico, String descricao, Dificuldade dificuldade, Iluminacao iluminacao, boolean co2Necessario, String crescimento, float alturaCm, float tempMin, float tempMax, float phMin, float phMax){}


    @PostMapping("/cadastrar")
    public ResponseEntity<Void> cadastrar(@RequestBody DadosRequest dados){
        Planta planta = new Planta();

        planta.setAlturaCm(dados.alturaCm());
        planta.setCo2Necessario(dados.co2Necessario());
        planta.setCrescimento(dados.crescimento());
        planta.setDescricao(dados.descricao());
        planta.setDificuldade(dados.dificuldade());
        planta.setIluminacao(dados.iluminacao());
        planta.setNomeCientifico(dados.nomeCientifico());
        planta.setNomePopular(dados.nomePopular());
        planta.setPhMax(dados.phMax());
        planta.setPhMin(dados.phMin());
        planta.setTempMax(dados.tempMax());
        planta.setTempMin(dados.tempMin());
        
        plantaService.cadastrar(planta);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/listar-todos")
    public ResponseEntity<List<Planta>> listarTodos(){
        List<Planta> plantas = plantaService.buscarTodos();
        
        return ResponseEntity.ok(plantas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Planta> buscarPorId(@PathVariable int id){
        Planta planta = plantaService.buscarPorId(id);
        
        return ResponseEntity.ok(planta);
    }

    @GetMapping("/nome-popular/{nome}")
    public ResponseEntity<List<Planta>> buscarPorNomePopular(@PathVariable String nome){
        List<Planta> plantas = plantaService.buscarPorNomePopular(nome);
        
        return ResponseEntity.ok(plantas);
    }

    @GetMapping("/nome-cientifico/{nome}")
    public ResponseEntity<List<Planta>> buscarPorNomeCientifico(@PathVariable String nome){
        List<Planta> plantas = plantaService.buscarPorNomeCientifico(nome);
        
        return ResponseEntity.ok(plantas);
    }

    @GetMapping("/dificuldade/{dificuldade}")
    public ResponseEntity<List<Planta>> buscarPorDificuldade(@PathVariable Dificuldade dificuldade){
        List<Planta> plantas = plantaService.buscarPorDificuldade(dificuldade);

        return ResponseEntity.ok(plantas);
    }

    @GetMapping("/iluminacao/{iluminacao}")
    public ResponseEntity<List<Planta>> buscarPorIluminacao(@PathVariable Iluminacao iluminacao){
        List<Planta> plantas = plantaService.buscarPorIluminacao(iluminacao);

        return ResponseEntity.ok(plantas);
    }

    @PutMapping ("/{id}")
    public ResponseEntity<Void> atualizar(@RequestBody DadosRequest dados, @PathVariable int id){
        Planta planta = new Planta();

        planta.setAlturaCm(dados.alturaCm());
        planta.setCo2Necessario(dados.co2Necessario());
        planta.setCrescimento(dados.crescimento());
        planta.setDescricao(dados.descricao());
        planta.setDificuldade(dados.dificuldade());
        planta.setIluminacao(dados.iluminacao());
        planta.setNomeCientifico(dados.nomeCientifico());
        planta.setNomePopular(dados.nomePopular());
        planta.setPhMax(dados.phMax());
        planta.setPhMin(dados.phMin());
        planta.setTempMax(dados.tempMax());
        planta.setTempMin(dados.tempMin());

        plantaService.atualizar(id, planta);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable int id){
        plantaService.excluir(id);

        return ResponseEntity.noContent().build();

    }
}
