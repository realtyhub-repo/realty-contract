package service.contratos.repository;

import org.springframework.data.jpa.domain.Specification;
import service.contratos.entity.Contrato;
import service.contratos.enums.EstadoContrato;

import java.util.UUID;

public final class ContratoSpecifications {

    private ContratoSpecifications() {
    }

    public static Specification<Contrato> porAgente(UUID agenteId) {
        return (root, query, cb) -> cb.equal(root.get("agenteId"), agenteId);
    }

    public static Specification<Contrato> porCliente(UUID clienteId) {
        return (root, query, cb) -> cb.equal(root.get("clienteId"), clienteId);
    }

    public static Specification<Contrato> conEstado(EstadoContrato estado) {
        return (root, query, cb) -> cb.equal(root.get("estado"), estado);
    }
}
