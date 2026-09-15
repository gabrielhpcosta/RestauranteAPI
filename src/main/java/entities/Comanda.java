package entities;

import enums.StatusComanda;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "comandas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class Comanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @NotNull
    @ManyToOne
    @JoinColumn(name = "mesa_id", nullable = false)
    private Mesa mesa;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ToString.Exclude
    @NotNull
    @ManyToOne
    @JoinColumn(name = "aberta_por_id", nullable = false)
    private Usuario abertaPor;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusComanda status;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime abertaEm;

    private OffsetDateTime encerradaEm;
}
