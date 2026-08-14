package br.edu.ifpr.aquacare.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.aquacare.entity.Planta;

public interface PlantaRepository extends JpaRepository<Planta, Integer>{
    
}
