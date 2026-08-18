package br.edu.ifpr.aquacare.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.edu.ifpr.aquacare.entity.Usuario;
import br.edu.ifpr.aquacare.repository.UsuarioRepository;
import br.edu.ifpr.aquacare.security.TokenService;

@Service
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, TokenService tokenService){
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
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
}