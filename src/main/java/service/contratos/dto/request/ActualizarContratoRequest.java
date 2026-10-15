package service.contratos.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ActualizarContratoRequest(

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal precioAcordado
) {
}
