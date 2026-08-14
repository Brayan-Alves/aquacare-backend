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
    @Column(name = "id_usuario", updatable = false, nullable = false)
    private String id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 64)
    private String senhaHash;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private LocalDateTime dataCadastro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Aquario> aquarios;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lembrete> lembretes;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DispositivoNotificacao> dispositivos;

    @PrePersist
    public void prePersist(){
        this.dataCadastro = LocalDateTime.now();
        if(this.role == null){
            this.role = Role.USER;
        }
    }
}
