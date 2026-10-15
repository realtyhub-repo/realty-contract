package service.contratos.dto.event;

import java.util.UUID;

public record LeadOfertaAceptadaEvent(
        UUID leadId,
        UUID propiedadId,
        UUID clienteId,
        UUID agenteId
) {
}
