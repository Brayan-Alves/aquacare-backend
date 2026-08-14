package br.edu.ifpr.aquacare.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.aquacare.entity.Lembrete;

public interface LembreteRepository extends JpaRepository<Lembrete, Integer>{
    
}
