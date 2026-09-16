package com.restaurante.RestauranteAPI.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.Locale;

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

    @NotBlank(message = "O campo CPF é obrigatório")
    @Column(nullable = false, unique = true)
    private String cpf;

    private String telefone;

    @NotBlank(message = "O campo e-mail é obrigatório!")
    @Email(message = "O e-mail é invalido!")
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull
    @Column(nullable = false)
    private Boolean ativo = true;

    private String cep;
    private String logradouro;
    private String bairro;
    private String cidade;
    private String uf;

    @PreUpdate
    public void normalizarDados() {
        if (email != null) email = email.trim().toLowerCase(Locale.ROOT);
        if (cpf != null) cpf = cpf.replaceAll("[^0-9]", "");
    }

}
