package entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @NotNull
    @ManyToOne
    @JoinColumn(name = "comanda_id", nullable = false)
    private Comanda comanda;

    @ToString.Exclude
    @NotNull
    @ManyToOne
    @JoinColumn(name = "registrado_por_id", nullable = false)
    private Usuario registradoPor;

    @Size(max = 1000)
    @Column(length = 1000)
    private String observacao;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime criadoEm;
    @PrePersist
    public void prePersist() {
        this.criadoEm = OffsetDateTime.now();
    }

}
