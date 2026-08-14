package br.edu.ifpr.aquacare.service;

import org.springframework.stereotype.Service;

import br.edu.ifpr.aquacare.entity.Usuario;
import br.edu.ifpr.aquacare.repository.UsuarioRepository;

@Service
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository){
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario cadastrar(Usuario usuario){
        return usuarioRepository.save(usuario);
    }   
}
