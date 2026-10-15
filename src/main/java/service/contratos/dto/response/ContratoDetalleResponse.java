package service.contratos.dto.response;

import lombok.Builder;
import service.contratos.entity.Contrato;
import service.contratos.enums.EstadoContrato;
import service.contratos.enums.TipoContrato;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ContratoDetalleResponse(
        UUID id,
        UUID leadId,
        UUID propiedadId,
        UUID clienteId,
        UUID agenteId,
        TipoContrato tipo,
        EstadoContrato estado,
        String pais,
        BigDecimal precioAcordado,
        String contenido,
        String nombreCliente,
        String nombreAgente,
        String direccionPropiedad,
        String ciudadPropiedad,
        LocalDateTime firmadoEn,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static ContratoDetalleResponse from(Contrato contrato) {
        return ContratoDetalleResponse.builder()
                .id(contrato.getId())
                .leadId(contrato.getLeadId())
                .propiedadId(contrato.getPropiedadId())
                .clienteId(contrato.getClienteId())
                .agenteId(contrato.getAgenteId())
                .tipo(contrato.getTipo())
                .estado(contrato.getEstado())
                .pais(contrato.getPais())
                .precioAcordado(contrato.getPrecioAcordado())
                .contenido(contrato.getContenido())
                .nombreCliente(contrato.getNombreCliente())
                .nombreAgente(contrato.getNombreAgente())
                .direccionPropiedad(contrato.getDireccionPropiedad())
                .ciudadPropiedad(contrato.getCiudadPropiedad())
                .firmadoEn(contrato.getFirmadoEn())
                .createdAt(contrato.getCreatedAt())
                .updatedAt(contrato.getUpdatedAt())
                .build();
    }
}
