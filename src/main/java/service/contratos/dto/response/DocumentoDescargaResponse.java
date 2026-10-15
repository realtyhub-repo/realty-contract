package service.contratos.dto.response;

import java.time.LocalDateTime;

public record DocumentoDescargaResponse(
        String url,
        Integer version,
        String hashSha256,
        LocalDateTime expiraEn
) {
}
