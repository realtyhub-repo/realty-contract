package service.contratos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "documento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "contrato_id", nullable = false, updatable = false)
    private UUID contratoId;

    @Column(nullable = false, updatable = false)
    private Integer version;

    @Column(name = "clave_archivo", nullable = false, updatable = false)
    private String claveArchivo;

    @Column(name = "hash_sha256", nullable = false, updatable = false, length = 64)
    private String hashSha256;

    @Column(name = "tamano_bytes", nullable = false, updatable = false)
    private Long tamanoBytes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
