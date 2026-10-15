package service.contratos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.contratos.entity.Documento;

import java.util.Optional;
import java.util.UUID;

public interface DocumentoRepository extends JpaRepository<Documento, UUID> {

    Optional<Documento> findFirstByContratoIdOrderByVersionDesc(UUID contratoId);
}
