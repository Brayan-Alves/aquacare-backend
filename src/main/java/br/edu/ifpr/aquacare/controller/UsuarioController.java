package br.edu.ifpr.aquacare.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import br.edu.ifpr.aquacare.entity.Usuario;
import br.edu.ifpr.aquacare.service.UsuarioService;

@RestController 
@RequestMapping("/usuarios")
public class UsuarioController {
    
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }

    public record CadastroRequest(String email, String senha, String nome){}
    public record LoginRequest(String email, String senha){}
    public record EmailRequest(String email){}
    public record RedefinirSenhaRequest(String token, String novaSenha){}
    public record AtualizarContaRequest(String nome, String email){}

    @PostMapping("/cadastrar")
    public ResponseEntity<Void> cadastrar(@RequestBody CadastroRequest dados){
        Usuario usuario = new Usuario();
        usuario.setEmail(dados.email());
        usuario.setSenhaHash((dados.senha()));
        usuario.setNome(dados.nome());

        usuarioService.cadastrar(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest dados){
        String token = usuarioService.autenticar(dados.email(), dados.senha());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<Void> recuperarSenha(@RequestBody EmailRequest dados){
        usuarioService.solicitarRecuperacaoSenha(dados.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<Void> redifinirSenha(@RequestBody RedefinirSenhaRequest dados){
        usuarioService.redefinirSenha(dados.token(), dados.novaSenha());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable  String id, @RequestBody AtualizarContaRequest dados){
        Usuario dadosAtualizados = new Usuario();
        dadosAtualizados.setNome(dados.nome());
        dadosAtualizados.setEmail(dados.email());    

        usuarioService.atualizar(id, dadosAtualizados);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id){
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}



