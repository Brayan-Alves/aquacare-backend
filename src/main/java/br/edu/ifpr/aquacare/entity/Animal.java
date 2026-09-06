package br.edu.ifpr.aquacare.entity;

import br.edu.ifpr.aquacare.enums.Agressividade;
import br.edu.ifpr.aquacare.enums.PadraoTerritorial;
import br.edu.ifpr.aquacare.enums.RegiaoNado;
import br.edu.ifpr.aquacare.enums.Sensibilidade;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Agressividade agressividade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PadraoTerritorial padraoTerritorial;

    @Column(nullable = false)
    private float tamanhoMedio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegiaoNado regiaoNado;

    @Column(nullable = false, length = 20)
    private String habitoAlimentar;

    @Column(nullable = true)
    private Float lc50Amonia;

    @Column(nullable = true)
    private Float lc50Nitrito;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sensibilidade sensibilidadeAmonia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sensibilidade sensibilidadeNitrito;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sensibilidade sensibilidadeNitrato;

    @Column(nullable = true)
    private Integer quantidadeMinimaGrupo;
}

