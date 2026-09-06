package br.edu.ifpr.aquacare.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.edu.ifpr.aquacare.entity.Lembrete;
import br.edu.ifpr.aquacare.repository.LembreteRepository;

@Service 
public class LembreteSchedulerService {
    private final LembreteRepository lembreteRepository;
    private final NotificacaoPushService notificaoPushService;

    public LembreteSchedulerService(LembreteRepository lembreteRepository, NotificacaoPushService notificaoPushService){
        this.lembreteRepository = lembreteRepository;
        this.notificaoPushService = notificaoPushService;
    }

    @Scheduled(fixedRate = 60000)
    public void verificarLembretesPendentes(){
        List<Lembrete> pendentes = lembreteRepository.findByDataHoraLembreteBeforeAndNotificadoFalse(LocalDateTime.now());
        
        for (Lembrete lembrete : pendentes) {
            if(lembrete.isConcluido()){
                continue;
            }

            notificaoPushService.notificarUsuario(lembrete.getUsuario().getId(), lembrete.getTitulo(), "Lembrete: " + lembrete.getTitulo());
            lembrete.setNotificado(true);
            lembreteRepository.save(lembrete);
        }
    }
}
