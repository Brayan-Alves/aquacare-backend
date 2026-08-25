package br.edu.ifpr.aquacare.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.edu.ifpr.aquacare.entity.PasswordResetToken;
import br.edu.ifpr.aquacare.entity.Usuario;
import br.edu.ifpr.aquacare.repository.PasswordResetTokenRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class PasswordResetTokenService {
    
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public PasswordResetTokenService(PasswordResetTokenRepository passwordResetTokenRepository){
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    public PasswordResetToken gerar(Usuario usuario){
        PasswordResetToken token = new PasswordResetToken();
        token.setUsuario(usuario);
        token.setToken(UUID.randomUUID().toString());
        token.setUsado(false);

        return passwordResetTokenRepository.save(token);
    }

    public PasswordResetToken buscarPorToken(String token){
        return passwordResetTokenRepository.findByToken(token).orElseThrow(() -> new EntityNotFoundException("Token não encontrado."));
    }

    public PasswordResetToken marcarComoUsado(String token){
        PasswordResetToken passwordResetToken = buscarPorToken(token);

        passwordResetToken.setUsado(true);

        return passwordResetTokenRepository.save(passwordResetToken);
    }
}
