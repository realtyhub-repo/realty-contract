package service.contratos.entity;


import jakarta.persistence.*;
import lombok.*;
import service.contratos.enums.EstadoContrato;
import service.contratos.enums.TipoContrato;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "contrato")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "lead_id", nullable = false, updatable = false)
    private UUID leadId;

    @Column(name = "propiedad_id", nullable = false, updatable = false)
    private UUID propiedadId;

    @Column(name = "cliente_id", nullable = false, updatable = false)
    private UUID clienteId;

    @Column(name = "agente_id", nullable = false, updatable = false)
    private UUID agenteId;

    @Column(name = "plantilla_id", nullable = false, updatable = false)
    private UUID plantillaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private TipoContrato tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoContrato estado;

    @Column(nullable = false, length = 2, updatable = false)
    private String pais;

    @Column(name = "precio_acordado", precision = 15, scale = 2)
    private BigDecimal precioAcordado;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "nombre_cliente", nullable = false)
    private String nombreCliente;

    @Column(name = "nombre_agente", nullable = false)
    private String nombreAgente;

    @Column(name = "direccion_propiedad", nullable = false)
    private String direccionPropiedad;

    @Column(name = "ciudad_propiedad", nullable = false, length = 120)
    private String ciudadPropiedad;

    @Column(name = "firmado_en")
    private LocalDateTime firmadoEn;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}