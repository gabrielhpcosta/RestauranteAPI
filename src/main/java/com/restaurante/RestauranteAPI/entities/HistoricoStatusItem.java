package com.restaurante.RestauranteAPI.entities;

import com.restaurante.RestauranteAPI.enums.StatusItemPedido;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "historicos_status_itens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class HistoricoStatusItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @NotNull
    @ManyToOne
    @JoinColumn(name = "item_pedido_id", nullable = false)
    private ItemPedido itemPedido;

    @Enumerated(EnumType.STRING)
    private StatusItemPedido statusAnterior;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusItemPedido statusNovo;

    @ToString.Exclude
    @NotNull
    @ManyToOne
    @JoinColumn(name = "alterado_pelo_id", nullable = false)
    private Usuario alteradoPor;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime alteradoEm;

    @Size(max = 1000)
    @Column(length = 1000)
    private String motivo;

    @PrePersist
    public void preencherData() {
        this.alteradoEm = OffsetDateTime.now();
    }

}
