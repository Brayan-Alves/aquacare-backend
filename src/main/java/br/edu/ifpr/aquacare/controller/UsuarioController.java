package br.edu.ifpr.aquacare.controller;

import java.time.LocalDateTime;

import br.edu.ifpr.aquacare.enums.Role;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import br.edu.ifpr.aquacare.entity.Usuario;
import br.edu.ifpr.aquacare.service.UsuarioService;
import jakarta.validation.Valid;

@RestController 
@RequestMapping("/usuarios")
public class UsuarioController {
    
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }

    public record UsuarioResponse(String id, String email, String nome, LocalDateTime dataCadastro, Role role){
        public static UsuarioResponse from(Usuario usuario){
            return new UsuarioResponse(usuario.getId(), usuario.getEmail(), usuario.getNome(), usuario.getDataCadastro(), usuario.getRole());
        }
    }


    public record CadastroRequest(@NotBlank @Email String email, @NotBlank String senha, @NotBlank String nome){}
    public record LoginRequest(@NotBlank @Email String email, @NotBlank String senha){}
    public record EmailRequest(@NotBlank @Email String email){}
    public record RedefinirSenhaRequest(@NotBlank String token, @NotBlank String novaSenha){}
    public record AtualizarContaRequest(@NotBlank String nome, @NotBlank @Email String email){}

    @PostMapping("/cadastrar")
    public ResponseEntity<Void> cadastrar(@Valid @RequestBody CadastroRequest dados){
        Usuario usuario = new Usuario();
        usuario.setEmail(dados.email());
        usuario.setSenhaHash((dados.senha()));
        usuario.setNome(dados.nome());

        usuarioService.cadastrar(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest dados){
        String token = usuarioService.autenticar(dados.email(), dados.senha());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<Void> recuperarSenha(@Valid @RequestBody EmailRequest dados){
        usuarioService.solicitarRecuperacaoSenha(dados.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<Void> redifinirSenha(@Valid @RequestBody RedefinirSenhaRequest dados){
        usuarioService.redefinirSenha(dados.token(), dados.novaSenha());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(@AuthenticationPrincipal Usuario usuario){
        return ResponseEntity.ok(UsuarioResponse.from(usuario));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable  String id, @Valid @RequestBody AtualizarContaRequest dados){
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



