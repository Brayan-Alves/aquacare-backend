package br.edu.ifpr.aquacare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.aquacare.entity.Equipamento;

public interface EquipamentoRepository extends  JpaRepository<Equipamento, Integer>{
    
    public List<Equipamento> findByAquarioId(int idAquario);
}
