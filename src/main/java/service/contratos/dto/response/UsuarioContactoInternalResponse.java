package service.contratos.dto.response;

import java.util.UUID;

public record UsuarioContactoInternalResponse(
        UUID id,
        String nombre,
        String telefono,
        String email
) {
}
