package entities;

import enums.TipoMovimentacao;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "movimentacoes_estoque")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @NotNull
    @ManyToOne
    @JoinColumn(name = "ingrediente_id", nullable = false)
    private Ingrediente ingrediente;

    @NotNull(message = "O tipo da movimentação é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimentacao tipo;

    @NotNull(message = "A quantidade é obrigatória")
    @Positive(message = "A quantidade deve ser maior que zero")
    @Digits(integer = 12, fraction = 3)
    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal quantidade;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "item_pedido_id")
    private ItemPedido itemPedido;

    @ToString.Exclude
    @NotNull
    @ManyToOne
    @JoinColumn(name = "responsavel_id", nullable = false)
    private Usuario responsavel;

    @NotBlank(message = "O motivo é obrigatório")
    @Size(max = 1000)
    @Column(nullable = false, length = 1000)
    private String motivo;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @PrePersist
    public void preencherData() {
        this.criadoEm = OffsetDateTime.now();
    }
}