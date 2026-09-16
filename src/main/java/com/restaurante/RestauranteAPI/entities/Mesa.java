package com.restaurante.RestauranteAPI.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Entity
@Table(name = "mesas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "É obrigatório informar o número da mesa")
    @Positive
    @Column(nullable = false, unique = true)
    private Integer numero;

    @NotNull(message = "É obrigatório informar a capacidade da mesa")
    @Positive(message = "A capacidade da mesa deve ser maior que zero")
    @Column(nullable = false)
    private Integer capacidade;

    @Column(nullable = false)
    private Boolean ativo = true;

}
