package br.edu.ifpr.aquacare.entity;

import br.edu.ifpr.aquacare.enums.TipoEquipamento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "equipamento")
@Getter
@Setter
public class Equipamento {
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int idEquipamento;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_aquario")
    private Aquario aquario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEquipamento tipo;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = true, length = 100)
    private String modelo;

    @Column(nullable = true, length = 100)
    private String marca;

    @Column(nullable = true)
    private Integer potenciaWatts;

    @Column(nullable = true)
    private Integer vazaoLH;

    @Column(nullable = true)
    private Float lumens;
}
