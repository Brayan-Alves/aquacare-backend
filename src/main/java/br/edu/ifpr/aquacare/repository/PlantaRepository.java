package br.edu.ifpr.aquacare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.aquacare.entity.Planta;
import br.edu.ifpr.aquacare.enums.Dificuldade;
import br.edu.ifpr.aquacare.enums.Iluminacao;

public interface PlantaRepository extends JpaRepository<Planta, Integer>{
    List<Planta> findByNomePopularContainingIgnoreCase(String nome);
    List<Planta> findByNomeCientificoContainingIgnoreCase(String nome);
    List<Planta> findByDificuldade(Dificuldade dificuldade);
    List<Planta> findByIluminacao(Iluminacao iluminacao);
}
