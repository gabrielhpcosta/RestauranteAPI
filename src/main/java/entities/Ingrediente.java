package entities;

import enums.UnidadeMedida;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ingredientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class Ingrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O campo nome é obrigatório")
    @Column(nullable = false)
    private String nome;

    @NotNull(message = "A unidade de medida é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnidadeMedida unidadeMedida;

    @NotNull
    @PositiveOrZero(message = "O saldo não pode ser negativo")
    @Digits(integer = 12, fraction = 3)
    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal saldo = BigDecimal.ZERO;

    @NotNull(message = "O estoque mínimo é obrigatório")
    @PositiveOrZero(message = "O estoque mínimo não pode ser negativo")
    @Digits(integer = 12, fraction = 3)
    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal estoqueMinimo = BigDecimal.ZERO;

    @NotNull
    @Column(nullable = false)
    private Boolean ativo = true;

}
