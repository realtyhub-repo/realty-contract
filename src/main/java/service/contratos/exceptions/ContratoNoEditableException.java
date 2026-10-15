package service.contratos.exceptions;

public class ContratoNoEditableException extends RuntimeException {

    public ContratoNoEditableException(String mensaje) {
        super(mensaje);
    }
}
