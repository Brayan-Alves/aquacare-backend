package br.edu.ifpr.aquacare.entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "dispositivo_notificacao")
@Getter
@Setter
public class DispositivoNotificacao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idDispositivo;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 255, unique = true)
    private String expoPushToken;
}
