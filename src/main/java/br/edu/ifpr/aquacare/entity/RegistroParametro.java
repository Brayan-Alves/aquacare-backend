package br.edu.ifpr.aquacare.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Registro_Parametro")
@Getter
@Setter
public class RegistroParametro {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int idParametro;

    @ManyToOne
    @JoinColumn(name = "id_aquario", nullable = false)
    private Aquario aquario;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private float ph;

    @Column(nullable = false)
    private float temperatura;

    @Column(nullable = false)
    private float amonia;

    @Column(nullable = true)
    private float nitrato;

    @Column(nullable = true)
    private float nitrito;

    @Column(nullable = true)
    private float gh;

    @Column(nullable = true)
    private float kh;

    @Column(nullable = true)
    private float co2;

    @Column(nullable = true)
    private float salinidade;

    @Column(nullable = true)
    private float calcio;

    @Column(nullable = true)
    private float magnesio;

    @PrePersist
    public void prePersist(){
        this.dataHora = LocalDateTime.now();
    }
}
