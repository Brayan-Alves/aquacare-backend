package br.edu.ifpr.aquacare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.aquacare.entity.PlantaAquario;

public interface PlantaAquarioRepository extends JpaRepository<PlantaAquario, Integer>{
    
    public List<PlantaAquario> findByAquarioId(int idAquario);
}
