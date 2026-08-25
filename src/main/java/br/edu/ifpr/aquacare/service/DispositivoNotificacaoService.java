package br.edu.ifpr.aquacare.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.aquacare.entity.DispositivoNotificacao;
import br.edu.ifpr.aquacare.repository.DispositivoNotificacaoRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class DispositivoNotificacaoService {
    

    private final DispositivoNotificacaoRepository dispositivoNotificacaoRepository;

    public DispositivoNotificacaoService(DispositivoNotificacaoRepository dispositivoNotificacaoRepository){
        this.dispositivoNotificacaoRepository = dispositivoNotificacaoRepository;
    }

    public DispositivoNotificacao cadastrar(DispositivoNotificacao dispositivoNotificacao){
        return dispositivoNotificacaoRepository.save(dispositivoNotificacao);
    }

    public List<DispositivoNotificacao> listarTodos(){
        return dispositivoNotificacaoRepository.findAll();
    }

    public DispositivoNotificacao buscarPorId(int id){
        return dispositivoNotificacaoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Dispositivo para Notificação não encontrado"));
    }

    public List<DispositivoNotificacao> listarPorUsuario(String idUsuario){
        return dispositivoNotificacaoRepository.findByUsuarioId(idUsuario);
    }

    public void excluir(int id){
        dispositivoNotificacaoRepository.delete(buscarPorId(id));
    }

    public DispositivoNotificacao atualizar(int id, DispositivoNotificacao dadosAtualizados){
        DispositivoNotificacao dispositivoNotificacao = buscarPorId(id);

        dispositivoNotificacao.setExpoPushToken(dadosAtualizados.getExpoPushToken());

        return dispositivoNotificacaoRepository.save(dispositivoNotificacao);
    }
}
