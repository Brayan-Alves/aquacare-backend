package br.edu.ifpr.aquacare.entity;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "animal_aquario")
@Getter
@Setter
public class AnimalAquario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_animal", nullable = false)
    private Animal animal;

    @ManyToOne
    @JoinColumn(name = "id_aquario", nullable = false)
    private Aquario aquario;

    @Column(nullable = false)
    private int quantidade;

    @Column(nullable = false)
    private LocalDate dataAdicao;

    @PrePersist
    public void prePersist(){
        this.dataAdicao = LocalDate.now();
    }
}
