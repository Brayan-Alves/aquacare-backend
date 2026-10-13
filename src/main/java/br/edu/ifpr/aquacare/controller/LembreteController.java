package br.edu.ifpr.aquacare.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpr.aquacare.entity.Lembrete;
import br.edu.ifpr.aquacare.entity.Usuario;
import br.edu.ifpr.aquacare.service.AquarioService;
import br.edu.ifpr.aquacare.service.LembreteService;


@RequestMapping("/lembrete")
@RestController
public class LembreteController {

    private final LembreteService lembreteService;
    private final AquarioService aquarioService;

    public record DadosRequest(int idAquario, String titulo, LocalDateTime dataHoraLembrete, boolean concluido, boolean notificado){}

    public record LembreteResponse(int idLembrete, int idAquario, String titulo, LocalDateTime dataHoraLembrete, boolean concluido, boolean notificado){
        public static LembreteResponse from(Lembrete lembrete){
            return new LembreteResponse(lembrete.getIdLembrete(), lembrete.getAquario().getId(), lembrete.getTitulo(), lembrete.getDataHoraLembrete(), lembrete.isConcluido(), lembrete.isNotificado());
        }
    }

    public LembreteController(LembreteService lembreteService, AquarioService aquarioService){
        this.lembreteService = lembreteService;
        this.aquarioService = aquarioService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<Void> cadastrar(@RequestBody  DadosRequest dados, @AuthenticationPrincipal Usuario usuario){
        Lembrete lembrete = new Lembrete();

        lembrete.setUsuario(usuario);
        lembrete.setAquario(aquarioService.buscarPorId(dados.idAquario()));
        lembrete.setConcluido(dados.concluido());
        lembrete.setDataHoraLembrete(dados.dataHoraLembrete());
        lembrete.setNotificado(dados.notificado());
        lembrete.setTitulo(dados.titulo());

        lembreteService.cadastrar(lembrete);

        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @GetMapping("/{id}")
    public ResponseEntity<LembreteResponse> buscraPorId(@PathVariable int id){
        Lembrete lembrete = lembreteService.buscarPorId(id);

        return ResponseEntity.ok(LembreteResponse.from(lembrete));
    }

    @GetMapping("/aquario/{id}")
    public ResponseEntity<List<LembreteResponse>> buscarPorIdAquario(@PathVariable int id){
        List<Lembrete> lembretes = lembreteService.listarPorAquario(id);

        return ResponseEntity.ok(lembretes.stream().map(LembreteResponse::from).toList());
    }

    @GetMapping("/meus")
    public ResponseEntity<List<LembreteResponse>> buscarPorIdUsuario(@AuthenticationPrincipal Usuario usuario){
        List<Lembrete> lembretes = lembreteService.listarPorUsuario(usuario.getId());

        return ResponseEntity.ok(lembretes.stream().map(LembreteResponse::from).toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizae(@PathVariable  int id, @RequestBody DadosRequest dados){
        Lembrete lembrete = new Lembrete();

        lembrete.setConcluido(dados.concluido());
        lembrete.setDataHoraLembrete(dados.dataHoraLembrete());
        lembrete.setNotificado(dados.notificado());
        lembrete.setTitulo(dados.titulo());

        lembreteService.atualizar(id, lembrete);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable int id){
        lembreteService.excluir(id);

        return ResponseEntity.noContent().build();
    }

}
