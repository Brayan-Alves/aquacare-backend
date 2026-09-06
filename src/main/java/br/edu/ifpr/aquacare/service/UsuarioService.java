package br.edu.ifpr.aquacare.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.edu.ifpr.aquacare.entity.PasswordResetToken;
import br.edu.ifpr.aquacare.entity.Usuario;
import br.edu.ifpr.aquacare.repository.UsuarioRepository;
import br.edu.ifpr.aquacare.security.TokenService;
import jakarta.persistence.EntityNotFoundException;
import br.edu.ifpr.aquacare.service.PasswordResetTokenService;
import br.edu.ifpr.aquacare.service.EmailService;

@Service
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final EmailService emailService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, TokenService tokenService, PasswordResetTokenService passwordResetTokenService, EmailService emailService){
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.passwordResetTokenService = passwordResetTokenService;
        this.emailService = emailService;
    }

    public Usuario cadastrar(Usuario usuario){
        if(usuarioRepository.findByEmail(usuario.getEmail()).isPresent()){
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }
        usuario.setSenhaHash(passwordEncoder.encode(usuario.getSenhaHash()));
        return usuarioRepository.save(usuario);
    }   

    public String autenticar(String email, String senha){
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("E-mail ou senha inválidos."));
         
        if(!passwordEncoder.matches(senha, usuario.getSenhaHash())){
            throw new IllegalArgumentException("E-mail ou senha inválidos.");
        }

        return tokenService.gerarToken(email);
    }

    public Usuario buscarPorId(String id){
        return usuarioRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));
    }

    public Usuario atualizar(String id, Usuario dadosAtualizados){
        Usuario usuario = buscarPorId(id);

        usuario.setNome(dadosAtualizados.getNome());
        usuario.setEmail(dadosAtualizados.getEmail());

        return usuarioRepository.save(usuario);
    }

    public void excluir(String id){
        usuarioRepository.delete(buscarPorId(id));
    }

    public void solicitarRecuperacaoSenha(String email){
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Email não encontrado."));

        PasswordResetToken token = passwordResetTokenService.gerar(usuario);
        emailService.enviarEmailRecuperacaoSenha(usuario.getEmail(), token.getToken());
    }

    public void redefinirSenha(String token, String novaSenha){
        PasswordResetToken passwordResetToken = passwordResetTokenService.validarToken(token);

        Usuario usuario = passwordResetToken.getUsuario();
        usuario.setSenhaHash(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

        passwordResetTokenService.marcarComoUsado(token);
    }
}