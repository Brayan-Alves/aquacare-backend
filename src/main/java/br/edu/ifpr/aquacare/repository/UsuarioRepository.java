package br.edu.ifpr.aquacare.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.aquacare.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    
}
