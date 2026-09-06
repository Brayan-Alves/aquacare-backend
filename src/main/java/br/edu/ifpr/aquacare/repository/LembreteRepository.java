package br.edu.ifpr.aquacare.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.aquacare.entity.Lembrete;

public interface LembreteRepository extends JpaRepository<Lembrete, Integer>{
    
    public List<Lembrete> findByUsuarioId(String idUsuario);
    public List<Lembrete> findByAquarioId(int idAquario);
    public List<Lembrete> findByDataHoraLembreteBeforeAndNotificadoFalse(LocalDateTime momento)
;}
