package br.edu.ifpr.aquacare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.aquacare.entity.Animal;
import br.edu.ifpr.aquacare.enums.Agressividade;
import br.edu.ifpr.aquacare.enums.RegiaoNado;

public interface AnimalRepository extends JpaRepository<Animal, Integer> {
    List<Animal> findByNomePopularContainingIgnoreCase(String nome);
    List<Animal> findByNomeCientificoContainingIgnoreCase(String nome);
    List<Animal> findByAgressividade(Agressividade agressividade);
    List<Animal> findByRegiaoNado(RegiaoNado regiaoNado);
}
