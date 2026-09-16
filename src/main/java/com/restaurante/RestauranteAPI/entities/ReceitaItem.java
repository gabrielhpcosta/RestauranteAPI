package com.restaurante.RestauranteAPI.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "receita_itens", uniqueConstraints = @UniqueConstraint(
    name = "uk_receita_produto_ingrediente", columnNames = {"produto_id", "ingrediente_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class ReceitaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @NotNull
    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @ToString.Exclude
    @NotNull
    @ManyToOne
    @JoinColumn(name = "ingrediente_id", nullable = false)
    private Ingrediente ingrediente;

    @Positive(message = "A quantidade deve ser maior que zero")
    @NotNull
    @Digits(integer = 12, fraction = 3)
    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal quantidade;

}
