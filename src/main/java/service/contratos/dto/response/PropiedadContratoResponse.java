package service.contratos.dto.response;

import service.contratos.enums.EstadoComercial;
import service.contratos.enums.Modalidad;

import java.math.BigDecimal;
import java.util.UUID;

public record PropiedadContratoResponse(
        UUID id,
        String titulo,
        String direccion,
        String ciudad,
        BigDecimal precio,
        Modalidad modalidad,
        EstadoComercial estadoComercial
) {
}
