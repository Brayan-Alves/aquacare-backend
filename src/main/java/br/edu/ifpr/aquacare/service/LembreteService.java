package br.edu.ifpr.aquacare.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.aquacare.entity.Lembrete;
import br.edu.ifpr.aquacare.repository.LembreteRepository;

import jakarta.persistence.EntityNotFoundException;


@Service
public class LembreteService {
    
    private final LembreteRepository lembreteRepository;

    public LembreteService(LembreteRepository lembreteRepository){
        this.lembreteRepository = lembreteRepository;
    }

    public Lembrete cadastrar(Lembrete lembrete){
        return lembreteRepository.save(lembrete);
    }

    public Lembrete buscarPorId(int id){
        return lembreteRepository.findById(id).orElseThrow(() ->  new EntityNotFoundException("Lembrete não encontrado."));
    }

    public Lembrete atualizar(int id, Lembrete dadosAtualizados){
        Lembrete lembrete = buscarPorId(id);

        lembrete.setConcluido(dadosAtualizados.isConcluido());
        lembrete.setDataHoraLembrete(dadosAtualizados.getDataHoraLembrete());
        lembrete.setTitulo(dadosAtualizados.getTitulo());
        lembrete.setNotificado(dadosAtualizados.isNotificado());
        
        return lembreteRepository.save(lembrete);
    }

    public List<Lembrete> listarTodos(){
        return lembreteRepository.findAll();
    }

    public List<Lembrete> listarPorAquario(int idAquario){
        return lembreteRepository.findByAquarioId(idAquario);
    }

    public List<Lembrete> listarPorUsuario(String idUsuario){
        return lembreteRepository.findByUsuarioId(idUsuario);
    }

    public void excluir(int id){
        lembreteRepository.delete(buscarPorId(id));
    }
}
