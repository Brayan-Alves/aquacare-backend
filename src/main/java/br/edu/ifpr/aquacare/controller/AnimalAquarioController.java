package br.edu.ifpr.aquacare.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestBody;

import br.edu.ifpr.aquacare.service.AnimalAquarioService;
import br.edu.ifpr.aquacare.service.Alerta;
import br.edu.ifpr.aquacare.service.AnimalService;
import br.edu.ifpr.aquacare.service.AquarioService;
import br.edu.ifpr.aquacare.entity.AnimalAquario;
import br.edu.ifpr.aquacare.entity.Usuario;
import br.edu.ifpr.aquacare.enums.Severidade;


@RestController 
@RequestMapping("/animal-aquario")
public class AnimalAquarioController {
    

    private final AnimalAquarioService animalAquarioService;
    private final AquarioService aquarioService;
    private final AnimalService animalService;


    public record AnimalAquarioResponse(int id, int idAnimal, int idAquario, int quantidade, LocalDate dataAdicao){
        public static AnimalAquarioResponse from(AnimalAquario animalAquario){
            return new AnimalAquarioResponse(animalAquario.getId(), animalAquario.getAnimal().getIdAnimal(), animalAquario.getAquario().getId(), animalAquario.getQuantidade(), animalAquario.getDataAdicao());
        }
    }

    public AnimalAquarioController(AnimalAquarioService animalAquarioService, AnimalService animalService, AquarioService aquarioService){
        this.animalAquarioService = animalAquarioService;
        this.animalService = animalService;
        this.aquarioService = aquarioService;
    }

    public record DadosRequest(int idAnimal, int idAquario, int quantidade){}

    @PostMapping("/associar")
    public ResponseEntity<List<Alerta>> associar(@RequestBody DadosRequest dados, @AuthenticationPrincipal Usuario usuario){
        AnimalAquario animalAquario = new AnimalAquario();


        animalAquario.setAnimal(animalService.buscarPorId(dados.idAnimal()));
        animalAquario.setAquario(aquarioService.buscarPorId(dados.idAquario()));

        
        if(!animalAquario.getAquario().getUsuario().getId().equals(usuario.getId())){
            throw new AccessDeniedException("Esse aquário não pertence a você.");
        }

        List<Alerta> alertas = new ArrayList<>();
        int diasMontagem = (int) java.time.temporal.ChronoUnit.DAYS.between(animalAquario.getAquario().getDataMontagem(), LocalDateTime.now());
        if(!animalAquario.getAquario().isCiclado() && diasMontagem < 30){
            alertas.add(new Alerta("Aquário em ciclagem (dia " + diasMontagem + " de 30): evite adicionar peixes agora.", Severidade.ATENCAO));
        }
        
        
        animalAquario.setQuantidade(dados.quantidade());

        animalAquarioService.associar(animalAquario);

        return ResponseEntity.status(HttpStatus.CREATED).body(alertas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalAquarioResponse> buscarPorId(@PathVariable int id, @AuthenticationPrincipal Usuario usuario){
        if(!animalAquarioService.buscarPorId(id).getAquario().getUsuario().getId().equals(usuario.getId())){
            throw new AccessDeniedException("Esse aquário não pertence a você.");
        }
        AnimalAquario animalAquario = animalAquarioService.buscarPorId(id);
        
        return ResponseEntity.ok(AnimalAquarioResponse.from(animalAquario));
    }

    @GetMapping("/aquario/{id}")
    public ResponseEntity<List<AnimalAquarioResponse>> buscarPorIdAquario(@PathVariable int id, @AuthenticationPrincipal Usuario usuario){
        if(!aquarioService.buscarPorId(id).getUsuario().getId().equals(usuario.getId())){
            throw new AccessDeniedException("Esse aquário não pertence a você.");
        }
        List<AnimalAquario> animais = animalAquarioService.listarPorAquario(id);

        return ResponseEntity.ok(animais.stream().map(AnimalAquarioResponse::from).toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@RequestBody  DadosRequest dados, @PathVariable int id, @AuthenticationPrincipal Usuario usuario){
        if(!animalAquarioService.buscarPorId(id).getAquario().getUsuario().getId().equals(usuario.getId())){
            throw new AccessDeniedException("Esse aquário não pertence a você.");
        }
        animalAquarioService.atualizar(id, dados.quantidade());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable  int id, @AuthenticationPrincipal Usuario usuario){
        if(!animalAquarioService.buscarPorId(id).getAquario().getUsuario().getId().equals(usuario.getId())){
            throw new AccessDeniedException("Esse aquário não pertence a você.");
        }
        animalAquarioService.excluir(id);


        return ResponseEntity.noContent().build();
    }
}
