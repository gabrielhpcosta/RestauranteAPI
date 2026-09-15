package entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O campo nome é obrigatório!")
    @Column(nullable = false)
    private String nome;

    private String telefone;

    @NotBlank(message = "O campo e-mail é obrigatório!")
    @Email(message = "O e-mail é invalido!")
    @Column(nullable = false)
    private String email;

    @NotNull
    @Column(nullable = false)
    private Boolean ativo = true;

}
