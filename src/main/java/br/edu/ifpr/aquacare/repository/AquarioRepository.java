package br.edu.ifpr.aquacare.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.aquacare.entity.Aquario;

public interface AquarioRepository extends JpaRepository<Aquario, Integer>{
    
}
