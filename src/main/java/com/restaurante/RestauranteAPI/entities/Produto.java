package com.restaurante.RestauranteAPI.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @NotNull
    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @NotBlank(message = "O campo nome é obrigatório!")
    @Column(nullable = false)
    private String nome;

    @Size(max = 1000)
    @Column(length = 1000)
    private String descricao;

    @NotNull(message = "O campo preço é obrigatório")
    @Positive(message = "O preço deve ser maior que zero")
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;

    @NotNull(message = "Informe se o produto está disponivel")
    @Column(nullable = false)
    private Boolean disponivel;

    @NotNull(message = "Informe se o produto exige preparo")
    @Column(nullable = false)
    private Boolean exigePreparo;
}
