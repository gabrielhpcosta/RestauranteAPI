package com.restaurante.RestauranteAPI.entities;

import com.restaurante.RestauranteAPI.enums.StatusItemPedido;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "itemPedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @NotNull
    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ToString.Exclude
    @NotNull
    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @NotBlank(message = "O nome do produto é obrigatorio")
    @Column(nullable = false)
    private String nomeProduto;

    @NotNull(message = "A quantidade é obrigatória")
    @Positive(message = "A quantidade deve ser maior que zero")
    @Column(nullable = false)
    private Integer quantidade;

    @NotNull(message = "O preço unitário é obrigatório")
    @Positive(message = "O preço unitário deve ser maior que zero")
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precoUnitario;

    @NotNull(message = "Informe se o item exige preparo")
    @Column(nullable = false)
    private Boolean exigePreparo;

    @Size(max = 1000)
    @Column(length = 1000)
    private String observacao;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusItemPedido status;

    @Column(nullable = false)
    private OffsetDateTime atualizadoEm;

    @PrePersist
    @PreUpdate
    public void atualizarData() {
        this.atualizadoEm = OffsetDateTime.now();
    }

}
