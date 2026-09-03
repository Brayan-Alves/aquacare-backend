package br.edu.ifpr.aquacare.entity;

import java.time.LocalDate;

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
@Table(name = "planta_aquario")
@Getter
@Setter
public class PlantaAquario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_planta", nullable = false)
    private Planta planta;

    @ManyToOne
    @JoinColumn(name = "id_aquario", nullable = false)
    private Aquario aquario;

    @Column(nullable = false)
    private int quantidade;

    @Column(nullable = false)
    private LocalDate dataPlantio;

    @PrePersist
    public void PrePersist(){
        this.dataPlantio = LocalDate.now();
    }
}
