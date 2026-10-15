package service.contratos.exceptions;

public class DocumentoNoDisponibleException extends RuntimeException {

    public DocumentoNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
