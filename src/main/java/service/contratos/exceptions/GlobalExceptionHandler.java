package service.contratos.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import service.contratos.dto.internal.ErrorResponse;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ContextoUsuarioInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleContextoInvalido(ContextoUsuarioInvalidoException ex) {
        return construir(HttpStatus.UNAUTHORIZED, "CONTEXTO_INVALIDO", ex.getMessage());
    }

    @ExceptionHandler(AccesoNoAutorizadoException.class)
    public ResponseEntity<ErrorResponse> handleAccesoNoAutorizado(AccesoNoAutorizadoException ex) {
        return construir(HttpStatus.FORBIDDEN, "ACCESO_NO_AUTORIZADO", ex.getMessage());
    }

    @ExceptionHandler(ContratoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleContratoNoEncontrado(ContratoNoEncontradoException ex) {
        return construir(HttpStatus.NOT_FOUND, "CONTRATO_NO_ENCONTRADO", ex.getMessage());
    }

    @ExceptionHandler(TransicionInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleTransicionInvalida(TransicionInvalidaException ex) {
        return construir(HttpStatus.CONFLICT, "TRANSICION_INVALIDA", ex.getMessage());
    }

    @ExceptionHandler(ContratoNoEditableException.class)
    public ResponseEntity<ErrorResponse> handleContratoNoEditable(ContratoNoEditableException ex) {
        return construir(HttpStatus.CONFLICT, "CONTRATO_NO_EDITABLE", ex.getMessage());
    }

    @ExceptionHandler(PrecioRequeridoException.class)
    public ResponseEntity<ErrorResponse> handlePrecioRequerido(PrecioRequeridoException ex) {
        return construir(HttpStatus.CONFLICT, "PRECIO_REQUERIDO", ex.getMessage());
    }

    @ExceptionHandler(DocumentoNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> handleDocumentoNoDisponible(DocumentoNoDisponibleException ex) {
        return construir(HttpStatus.CONFLICT, "DOCUMENTO_NO_DISPONIBLE", ex.getMessage());
    }

    @ExceptionHandler(ServicioNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> handleServicioNoDisponible(ServicioNoDisponibleException ex) {
        return construir(HttpStatus.SERVICE_UNAVAILABLE, "SERVICIO_NO_DISPONIBLE", ex.getMessage());
    }

    @ExceptionHandler(AlmacenamientoException.class)
    public ResponseEntity<ErrorResponse> handleAlmacenamiento(AlmacenamientoException ex) {
        log.error("Fallo de almacenamiento", ex);
        return construir(HttpStatus.BAD_GATEWAY, "ALMACENAMIENTO_NO_DISPONIBLE", ex.getMessage());
    }

    @ExceptionHandler(PdfGeneracionException.class)
    public ResponseEntity<ErrorResponse> handlePdf(PdfGeneracionException ex) {
        log.error("Fallo al generar el PDF", ex);
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, "PDF_NO_GENERADO", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidacion(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .orElse("Solicitud inválida");
        return construir(HttpStatus.BAD_REQUEST, "SOLICITUD_INVALIDA", mensaje);
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ErrorResponse> handleSolicitudMalFormada(Exception ex) {
        return construir(HttpStatus.BAD_REQUEST, "SOLICITUD_INVALIDA",
                "La solicitud tiene parámetros faltantes o con formato inválido");
    }

    private ResponseEntity<ErrorResponse> construir(HttpStatus status, String codigo, String mensaje) {
        ErrorResponse error = new ErrorResponse(codigo, mensaje, status.value(), LocalDateTime.now());
        return ResponseEntity.status(status).body(error);
    }
}
