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

import br.edu.ifpr.aquacare.entity.Animal;
import br.edu.ifpr.aquacare.enums.Agressividade;
import br.edu.ifpr.aquacare.enums.PadraoTerritorial;
import br.edu.ifpr.aquacare.enums.RegiaoNado;
import br.edu.ifpr.aquacare.enums.Sensibilidade;
import br.edu.ifpr.aquacare.service.AnimalService;

@RestController
@RequestMapping("/animal")
public class AnimalController {
    
    private final AnimalService animalService;

    public AnimalController(AnimalService animalService){
        this.animalService = animalService;
    }

    public record DadosRequest(String nomePopular, String nomeCientifico, String descricao, float tempMin, float tempMax, float phMin, float phMax, Float ghMin, Float ghMax, Float salinidadeMin, Float salinidadeMax, Agressividade agressividade, PadraoTerritorial padraoTerritorial, float tamanhoMedio, RegiaoNado regiaoNado, String habitoAlimentar, Float lc50Amonia, Float lc50Nitrito, Sensibilidade sensibilidadeAmonia, Sensibilidade sensibilidadeNitrito, Sensibilidade sensibilidadeNitrato, Integer quantidadeMinimaGrupo){}

    @PostMapping("/cadastrar")
    public ResponseEntity<Void> cadastrar(@RequestBody DadosRequest dados){
        Animal animal = new Animal();

        animal.setNomePopular(dados.nomePopular());
        animal.setNomeCientifico(dados.nomeCientifico());
        animal.setTempMin(dados.tempMin());
        animal.setTempMax(dados.tempMax());
        animal.setAgressividade(dados.agressividade());
        animal.setDescricao(dados.descricao());
        animal.setGhMax(dados.ghMax());
        animal.setGhMin(dados.ghMin());
        animal.setHabitoAlimentar(dados.habitoAlimentar());
        animal.setLc50Amonia(dados.lc50Amonia());
        animal.setLc50Nitrito(dados.lc50Nitrito());
        animal.setPadraoTerritorial(dados.padraoTerritorial());
        animal.setPhMax(dados.phMax());
        animal.setPhMin(dados.phMin());
        animal.setQuantidadeMinimaGrupo(dados.quantidadeMinimaGrupo());
        animal.setRegiaoNado(dados.regiaoNado());
        animal.setSalinidadeMax(dados.salinidadeMax());
        animal.setSalinidadeMin(dados.salinidadeMin());
        animal.setSensibilidadeAmonia(dados.sensibilidadeAmonia());
        animal.setSensibilidadeNitrato(dados.sensibilidadeNitrato());
        animal.setSensibilidadeNitrito(dados.sensibilidadeNitrito());
        animal.setTamanhoMedio(dados.tamanhoMedio());

        animalService.cadastrar(animal);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/listar-todos")
    public ResponseEntity<List<Animal>> listarTodos(){
       List<Animal> animais = animalService.buscarTodos();
       return ResponseEntity.ok(animais);
    } 
    
    @GetMapping("/{id}")
    public ResponseEntity<Animal> buscarPorId(@PathVariable int id){
        Animal animal = animalService.buscarPorId(id);
        return ResponseEntity.ok(animal);
    }

    @GetMapping("/nome-popular/{nome}")
    public ResponseEntity<List<Animal>> buscarPorNomePopular(@PathVariable String nome){
        List<Animal> animais = animalService.buscarPorNomePopular(nome);
        return ResponseEntity.ok(animais);
    }

    @GetMapping("/nome-cientifico/{nome}")
    public ResponseEntity<List<Animal>> buscarPorNomeCientifico(@PathVariable String nome){
        List<Animal> animais = animalService.buscarPorNomeCientifico(nome);
        return ResponseEntity.ok(animais);
    }

    @GetMapping("/agressividade/{agressividade}")
    public ResponseEntity<List<Animal>> buscarPorAgressividade(@PathVariable Agressividade agressividade){
        List<Animal> animais = animalService.buscarPorAgressividade(agressividade);
        return ResponseEntity.ok(animais);
    }

    @GetMapping("/regiao-nado/{regiaoNado}")
    public ResponseEntity<List<Animal>> buscarPorRegiaoDeNado(@PathVariable RegiaoNado regiaoNado){
        List<Animal> animais = animalService.buscarPorRegiaoDeNado(regiaoNado);
        return ResponseEntity.ok(animais);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable  int id, @RequestBody DadosRequest dados){
        Animal animal = new Animal();

        animal.setAgressividade(dados.agressividade());
        animal.setDescricao(dados.descricao());
        animal.setGhMax(dados.ghMax());
        animal.setGhMin(dados.ghMin());
        animal.setHabitoAlimentar(dados.habitoAlimentar());
        animal.setLc50Amonia(dados.lc50Amonia());
        animal.setLc50Nitrito(dados.lc50Nitrito());
        animal.setNomeCientifico(dados.nomeCientifico());
        animal.setNomePopular(dados.nomePopular());
        animal.setPadraoTerritorial(dados.padraoTerritorial());
        animal.setPhMax(dados.phMax());
        animal.setPhMin(dados.phMin());
        animal.setQuantidadeMinimaGrupo(dados.quantidadeMinimaGrupo());
        animal.setRegiaoNado(dados.regiaoNado());
        animal.setSalinidadeMax(dados.salinidadeMax());
        animal.setSalinidadeMin(dados.salinidadeMin());
        animal.setSensibilidadeAmonia(dados.sensibilidadeAmonia());
        animal.setSensibilidadeNitrato(dados.sensibilidadeNitrato());
        animal.setSensibilidadeNitrito(dados.sensibilidadeNitrito());
        animal.setTamanhoMedio(dados.tamanhoMedio());
        animal.setTempMax(dados.tempMax());
        animal.setTempMin(dados.tempMin());

        animalService.atualizar(id, animal);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable int id){
        animalService.excluir(id);
        return ResponseEntity.noContent().build();
    }













}
