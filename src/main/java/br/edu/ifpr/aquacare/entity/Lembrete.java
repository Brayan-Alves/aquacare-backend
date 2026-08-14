package br.edu.ifpr.aquacare.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "lembrete")
@Getter
@Setter
public class Lembrete {
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int idLembrete;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_aquario", nullable = false)
    private Aquario aquario;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false)
    private LocalDateTime datahoraLembrete;

    @Column(nullable = false)
    private boolean isConcluido;
}
