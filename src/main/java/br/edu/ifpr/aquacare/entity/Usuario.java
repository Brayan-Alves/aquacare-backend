package br.edu.ifpr.aquacare.entity;

import jakarta.persistence.*;
import lombok.*;
import br.edu.ifpr.aquacare.enums.Role;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private String id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Aquario> aquarios;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Lembrete> lembretes;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<DispositivoNotificacao> dispositivos;

    @PrePersist
    public void prePersist(){
        this.dataCadastro = LocalDateTime.now();
        if(this.role == null){
            this.role = Role.USER;
        }
    }
}
