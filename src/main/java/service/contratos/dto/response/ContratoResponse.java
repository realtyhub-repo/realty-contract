package service.contratos.dto.response;

import lombok.Builder;
import service.contratos.entity.Contrato;
import service.contratos.enums.EstadoContrato;
import service.contratos.enums.TipoContrato;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ContratoResponse(
        UUID id,
        UUID leadId,
        UUID propiedadId,
        UUID clienteId,
        UUID agenteId,
        TipoContrato tipo,
        EstadoContrato estado,
        BigDecimal precioAcordado,
        LocalDateTime firmadoEn,
        LocalDateTime createdAt
) {

    public static ContratoResponse from(Contrato contrato) {
        return ContratoResponse.builder()
                .id(contrato.getId())
                .leadId(contrato.getLeadId())
                .propiedadId(contrato.getPropiedadId())
                .clienteId(contrato.getClienteId())
                .agenteId(contrato.getAgenteId())
                .tipo(contrato.getTipo())
                .estado(contrato.getEstado())
                .precioAcordado(contrato.getPrecioAcordado())
                .firmadoEn(contrato.getFirmadoEn())
                .createdAt(contrato.getCreatedAt())
                .build();
    }
}
