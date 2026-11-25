package br.edu.ifpr.aquacare.entity;

import br.edu.ifpr.aquacare.enums.Parametro;
import br.edu.ifpr.aquacare.enums.Severidade;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "alerta")
@Getter
@Setter
public class AlertaRegistro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_registro", nullable = false)
    private RegistroParametro registro;

    @Enumerated(EnumType.STRING)
    private Parametro parametro;

    @Column(nullable = false)
    private String mensagem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severidade severidade;

    @Column(nullable = false)
    private String recomendacao;
}