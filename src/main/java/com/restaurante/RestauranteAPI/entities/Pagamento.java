package com.restaurante.RestauranteAPI.entities;

import com.restaurante.RestauranteAPI.enums.FormaPagamento;
import com.restaurante.RestauranteAPI.enums.StatusPagamento;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "pagamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @NotNull
    @ManyToOne
    @JoinColumn(name = "comanda_id", nullable = false)
    private Comanda comanda;

    @NotNull(message = "O valor é obrigatório")
    @Positive(message = "O valor deve ser maior que zero")
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @NotNull(message = "A forma de pagamento é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormaPagamento forma;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamento status;

    @NotBlank(message = "A chave de idempotência é obrigatória")
    @Size(max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String chaveIdempotencia;

    @ToString.Exclude
    @NotNull
    @ManyToOne
    @JoinColumn(name = "registrado_por_id", nullable = false)
    private Usuario registradoPor;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    private OffsetDateTime estornadoEm;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "estornado_por_id")
    private Usuario estornadoPor;

    @Size(max = 1000)
    @Column(length = 1000)
    private String motivoEstorno;

    @PrePersist
    public void preencherData() {
        this.criadoEm = OffsetDateTime.now();
    }
}