package br.edu.ifpr.aquacare.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "animal")
@Getter
@Setter
public class Animal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int idAnimal;

    @Column(nullable = false, length = 100)
    private String nomePopular;

    @Column(nullable = false, length = 100)
    private String nomeCientifico;

    @Column(nullable = false, length = 1000)
    private String descricao;

    @Column(nullable = false)
    private float tempMin;

    @Column(nullable = false)
    private float tempMax;

    @Column(nullable = false)
    private float phMin;

    @Column(nullable = false)
    private float phMax;

    @Column(nullable = true)
    private Float ghMin;

    @Column(nullable = true)
    private Float ghMax;

    @Column(nullable = true)
    private Float salinidadeMin;

    @Column(nullable = true)
    private Float salinidadeMax;

    @Column(nullable = false, length = 20)
    private String agressividade;

    @Column(nullable = false, length = 20)
    private String porte;

    @Column(nullable = false, length = 20)
    private String regiaoNado;

    @Column(nullable = false, length = 20)
    private String habitoAlimentar;

    @Column(nullable = false, length = 20)
    private String sensibilidadeAmonia;

    @Column(nullable = false, length = 20)
    private String sensibilidadeNitrito;

    @Column(nullable = false, length = 20)
    private String sensibilidadeNitrato;
}

