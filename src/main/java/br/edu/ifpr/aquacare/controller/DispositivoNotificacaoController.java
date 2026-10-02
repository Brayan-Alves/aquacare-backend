package br.edu.ifpr.aquacare.controller;

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

import br.edu.ifpr.aquacare.entity.DispositivoNotificacao;
import br.edu.ifpr.aquacare.entity.Usuario;
import br.edu.ifpr.aquacare.service.DispositivoNotificacaoService;

@RestController
@RequestMapping("/dispositivo")
public class DispositivoNotificacaoController {
    

    private final DispositivoNotificacaoService dispositivoNotificacaoService;

    public DispositivoNotificacaoController(DispositivoNotificacaoService dispositivoNotificacaoService){
        this.dispositivoNotificacaoService = dispositivoNotificacaoService;
    }

    public record DadosRequest(String expoPushToken){}

    @PostMapping("/cadastrar")
    public ResponseEntity<DispositivoNotificacao> cadastrar(@RequestBody DadosRequest dados, @AuthenticationPrincipal Usuario usuario){
        DispositivoNotificacao dispositivo = new DispositivoNotificacao();

        dispositivo.setExpoPushToken(dados.expoPushToken());
        dispositivo.setUsuario(usuario);

        dispositivoNotificacaoService.cadastrar(dispositivo);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DispositivoNotificacao> buscarPorId(@PathVariable  int id){
        DispositivoNotificacao dispositivo = dispositivoNotificacaoService.buscarPorId(id);

        return ResponseEntity.ok(dispositivo);
    }

    @GetMapping("/meus")
    public ResponseEntity<List<DispositivoNotificacao>> buscarMeusDispositivos(@AuthenticationPrincipal Usuario usuario){
        List<DispositivoNotificacao> dispositivos = dispositivoNotificacaoService.listarPorUsuario(usuario.getId());

        return ResponseEntity.ok(dispositivos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable int id, @RequestBody DadosRequest dados){
        DispositivoNotificacao dispositivo = new DispositivoNotificacao();

        dispositivo.setExpoPushToken(dados.expoPushToken());

        dispositivoNotificacaoService.atualizar(id, dispositivo);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable int id){
        dispositivoNotificacaoService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
