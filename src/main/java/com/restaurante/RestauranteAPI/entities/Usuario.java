package com.restaurante.RestauranteAPI.entities;

import com.restaurante.RestauranteAPI.enums.Users;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O campo nome é obrigatório!")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "O campo e-mail é obrigatório!")
    @Email(message = "O e-mail é invalido!")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "O campo senha é obrigatório!")
    @Column(nullable = false)
    @ToString.Exclude
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Users role;

    @Column(nullable = false)
    private Boolean ativo = true;

    private Integer tentativasFalhas = 0;

    private LocalDateTime bloqueadoAte;

}
