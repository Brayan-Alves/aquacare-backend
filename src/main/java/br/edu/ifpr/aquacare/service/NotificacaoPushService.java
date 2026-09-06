package br.edu.ifpr.aquacare.service;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import br.edu.ifpr.aquacare.entity.DispositivoNotificacao;
import br.edu.ifpr.aquacare.repository.DispositivoNotificacaoRepository;

@Service 
public class NotificacaoPushService {
    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";

    private final DispositivoNotificacaoRepository dispositivoNotificacaoRepository;
    private final RestClient restClient = RestClient.create();

    public NotificacaoPushService(DispositivoNotificacaoRepository dispositivoNotificacaoRepository){
        this.dispositivoNotificacaoRepository = dispositivoNotificacaoRepository;
    }

    public record ExpoPushMessage(String to, String title, String body){}

    public void notificarUsuario(String idUsuario, String titulo, String corpo){
        List<DispositivoNotificacao> dispositivos = dispositivoNotificacaoRepository.findByUsuarioId(idUsuario);

        for (DispositivoNotificacao dispositivo : dispositivos) {
            restClient.post()
                .uri(EXPO_PUSH_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ExpoPushMessage(dispositivo.getExpoPushToken(), titulo, corpo))
                .retrieve()
                .toBodilessEntity();
        }
    }
}
