package br.edu.ifpr.aquacare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.aquacare.entity.DispositivoNotificacao;

public interface DispositivoNotificacaoRepository extends JpaRepository<DispositivoNotificacao, Integer>{
    public List<DispositivoNotificacao> findByUsuarioId(String idUsuario);
}
