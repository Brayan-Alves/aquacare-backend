package br.edu.ifpr.aquacare.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.aquacare.entity.Animal;

public interface AnimalRepository extends JpaRepository<Animal, Integer> {
    
}
