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

import br.edu.ifpr.aquacare.service.AnimalAquarioService;
import br.edu.ifpr.aquacare.service.AnimalService;
import br.edu.ifpr.aquacare.service.AquarioService;
import br.edu.ifpr.aquacare.entity.AnimalAquario;

@RestController 
@RequestMapping("/animal-aquario")
public class AnimalAquarioController {
    

    private final AnimalAquarioService animalAquarioService;
    private final AquarioService aquarioService;
    private final AnimalService animalService;

    public AnimalAquarioController(AnimalAquarioService animalAquarioService, AnimalService animalService, AquarioService aquarioService){
        this.animalAquarioService = animalAquarioService;
        this.animalService = animalService;
        this.aquarioService = aquarioService;
    }

    public record DadosRequest(int idAnimal, int idAquario, int quantidade){}

    @PostMapping("/associar")
    public ResponseEntity<AnimalAquario> associar(@RequestBody DadosRequest dados){
        AnimalAquario animalAquario = new AnimalAquario();

        animalAquario.setAnimal(animalService.buscarPorId(dados.idAnimal()));
        animalAquario.setAquario(aquarioService.buscarPorId(dados.idAquario()));
        animalAquario.setQuantidade(dados.quantidade());

        animalAquarioService.associar(animalAquario);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalAquario> buscarPorId(@PathVariable int id){
        AnimalAquario animalAquario = animalAquarioService.buscarPorId(id);
        
        return ResponseEntity.ok(animalAquario);
    }

    @GetMapping("/aquario/{id}")
    public ResponseEntity<List<AnimalAquario>> buscarPorIdAquario(@PathVariable int id){
        List<AnimalAquario> animais = animalAquarioService.listarPorAquario(id);

        return ResponseEntity.ok(animais);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@RequestBody  DadosRequest dados, @PathVariable int id){
        animalAquarioService.atualizar(id, dados.quantidade());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable  int id){
        animalAquarioService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
