package br.edu.ifpr.aquacare.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service 
public class EmailService {
    
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender){
        this.mailSender= mailSender;
    }

    public void enviarEmailRecuperacaoSenha(String destinatario, String token){
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(destinatario);
        mensagem.setSubject("Recuperação de senha - Aquacare");    
        mensagem.setText("Use o código abaixo para redefinir sua senha:\n\n" + token + "\n\nEsse código expira em 30 minutos.");
        
        mailSender.send(mensagem);
    }

}
