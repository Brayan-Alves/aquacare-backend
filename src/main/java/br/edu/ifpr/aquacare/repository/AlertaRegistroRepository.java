package br.edu.ifpr.aquacare.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import br.edu.ifpr.aquacare.entity.AlertaRegistro;

public interface AlertaRegistroRepository extends JpaRepository<AlertaRegistro, Integer> {
    List<AlertaRegistro> findByRegistroIdParametro(int idParametro);
}