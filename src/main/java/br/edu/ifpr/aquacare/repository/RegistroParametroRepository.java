package br.edu.ifpr.aquacare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.aquacare.entity.RegistroParametro;

public interface RegistroParametroRepository extends JpaRepository<RegistroParametro, Integer>{ 
    List<RegistroParametro> findByAquarioId(int id);
    RegistroParametro findFirstByAquarioIdOrderByDataHoraDesc(int idAquario);
}


