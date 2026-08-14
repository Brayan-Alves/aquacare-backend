package br.edu.ifpr.aquacare.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "aquario")
@Getter
@Setter
public class Aquario {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id_aquario")
private int id;

@ManyToOne
@JoinColumn(name = "id_usuario", nullable = false)
private Usuario usuario;

@Column(nullable = false, length = 100)
private String nome;

@Column(nullable = false)
private float litragem;

@Column(nullable = false, length = 20)
private String tipoAgua;

@Column(nullable = false, length = 20)
private String nivelPlantas;

@Column(nullable = false)
private LocalDateTime dataMontagem;

@Column(nullable = false)
private boolean isCiclado;

@Column(nullable = false)
private boolean injecaoCO2;

@PrePersist
public void prePersist(){
    this.dataMontagem = LocalDateTime.now();
}


    
}
