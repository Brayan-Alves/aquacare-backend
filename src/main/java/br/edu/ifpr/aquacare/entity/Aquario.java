package br.edu.ifpr.aquacare.entity;

import java.time.LocalDateTime;
import java.util.List;

import br.edu.ifpr.aquacare.enums.NivelPlanta;
import br.edu.ifpr.aquacare.enums.TipoAgua;
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

@Enumerated(EnumType.STRING)
@Column(nullable = false)
private TipoAgua tipoAgua;

@Enumerated(EnumType.STRING)
@Column(nullable = false)
private NivelPlanta nivelPlantas;

@Column(nullable = false)
private LocalDateTime dataMontagem;

@Column(nullable = false)
private boolean isCiclado;

@Column(nullable = false)
private boolean injecaoCO2;

@OneToMany(mappedBy = "aquario", cascade = CascadeType.REMOVE)
private List<Equipamento> equipamentos;

@OneToMany(mappedBy = "aquario", cascade = CascadeType.REMOVE)
private List<AnimalAquario> animais;

@OneToMany(mappedBy = "aquario", cascade = CascadeType.REMOVE)
private List<PlantaAquario> plantas;

@OneToMany(mappedBy = "aquario", cascade = CascadeType.REMOVE)
private List<RegistroParametro> registros;

@OneToMany(mappedBy = "aquario", cascade = CascadeType.REMOVE)
private List<Lembrete> lembretes;

@PrePersist
public void prePersist(){
    this.dataMontagem = LocalDateTime.now();
}


    
}
