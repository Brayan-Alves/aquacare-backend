package br.edu.ifpr.aquacare.entity;

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
@Table(name = "Equipamento")
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

    @Column(nullable = false, length = 100)
    private String tipo;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = true, length = 100)
    private String modelo;

    @Column(nullable = true, length = 100)
    private String marca;

    @Column(nullable = true)
    private int potenciaWatts;

    @Column(nullable = true)
    private int vazaoLH;

    @Column(nullable = true)
    private float lumens;
}
