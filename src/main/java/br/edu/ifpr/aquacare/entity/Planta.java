package br.edu.ifpr.aquacare.entity;

import br.edu.ifpr.aquacare.enums.Dificuldade;
import br.edu.ifpr.aquacare.enums.Iluminacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "planta")
@Getter
@Setter
public class Planta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int idPlanta;

    @Column(nullable = false, length = 100)
    private String nomePopular;

    @Column(nullable = false, length = 100)
    private String nomeCientifico;

    @Column(nullable = false, length = 100)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Dificuldade dificuldade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Iluminacao iluminacao;

    @Column(nullable = false)
    private boolean co2Necessario;

    @Column(nullable = false, length = 20)
    private String crescimento;

    @Column(nullable = false)
    private float alturaCm;

    @Column(nullable = false)
    private float tempMin;

    @Column(nullable = false)
    private float tempMax;

    @Column(nullable = false)
    private float phMin;

    @Column(nullable = false)
    private float phMax;
}
