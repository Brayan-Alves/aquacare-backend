package br.edu.ifpr.aquacare.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.aquacare.entity.Equipamento;

public interface EquipamentoRepository extends  JpaRepository<Equipamento, Integer>{
    
}
