package service.contratos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import service.contratos.entity.Contrato;

import java.util.UUID;

public interface ContratoRepository extends JpaRepository<Contrato, UUID>, JpaSpecificationExecutor<Contrato> {

    boolean existsByLeadId(UUID leadId);
}
