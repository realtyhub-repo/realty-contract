package service.contratos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.contratos.dto.request.ActualizarContratoRequest;
import service.contratos.dto.response.ContratoDetalleResponse;
import service.contratos.dto.response.ContratoResponse;
import service.contratos.dto.response.DocumentoDescargaResponse;
import service.contratos.enums.EstadoContrato;
import service.contratos.security.ContextoUsuario;
import service.contratos.security.UsuarioActual;
import service.contratos.service.ContratoService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/contratos")
@RequiredArgsConstructor
public class ContratoController {

    private final ContratoService contratoService;

    @GetMapping("/mis-contratos")
    public ResponseEntity<List<ContratoResponse>> listarMisContratos(
            @UsuarioActual ContextoUsuario contexto,
            @RequestParam(required = false) EstadoContrato estado) {

        return ResponseEntity.ok(contratoService.listarMisContratos(contexto, estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContratoDetalleResponse> obtenerDetalle(
            @PathVariable UUID id,
            @UsuarioActual ContextoUsuario contexto) {

        return ResponseEntity.ok(contratoService.obtenerDetalle(id, contexto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContratoResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarContratoRequest request,
            @UsuarioActual ContextoUsuario contexto) {

        return ResponseEntity.ok(contratoService.actualizar(id, request.precioAcordado(), contexto));
    }

    @PatchMapping("/{id}/firmar")
    public ResponseEntity<ContratoResponse> firmar(
            @PathVariable UUID id,
            @UsuarioActual ContextoUsuario contexto) {

        return ResponseEntity.ok(contratoService.firmar(id, contexto));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ContratoResponse> cancelar(
            @PathVariable UUID id,
            @UsuarioActual ContextoUsuario contexto) {

        return ResponseEntity.ok(contratoService.cancelar(id, contexto));
    }

    @GetMapping(value = "/{id}/vista-previa", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> vistaPrevia(
            @PathVariable UUID id,
            @UsuarioActual ContextoUsuario contexto) {

        byte[] pdf = contratoService.generarVistaPrevia(id, contexto);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename("borrador-" + id + ".pdf").build().toString())
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/{id}/documento")
    public ResponseEntity<DocumentoDescargaResponse> obtenerDocumento(
            @PathVariable UUID id,
            @UsuarioActual ContextoUsuario contexto) {

        return ResponseEntity.ok(contratoService.obtenerDocumento(id, contexto));
    }
}
