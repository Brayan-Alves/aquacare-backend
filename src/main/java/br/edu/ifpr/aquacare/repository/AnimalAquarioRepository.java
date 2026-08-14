package br.edu.ifpr.aquacare.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.aquacare.entity.AnimalAquario;

public interface AnimalAquarioRepository extends JpaRepository<AnimalAquario, Integer>{
    
}
