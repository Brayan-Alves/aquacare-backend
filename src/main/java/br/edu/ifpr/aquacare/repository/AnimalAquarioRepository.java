package br.edu.ifpr.aquacare.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.aquacare.entity.AnimalAquario;

public interface AnimalAquarioRepository extends JpaRepository<AnimalAquario, Integer>{
    public List<AnimalAquario> findByAquarioId(int idAquario);
    Optional<AnimalAquario> findByAquarioIdAndAnimalIdAnimal(int idAquario, int idAnimal);
    boolean existsByAnimalIdAnimal(int idAnimal);
}
